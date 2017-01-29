package com.example.smartify.devices;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smartify.data.LoaderProvider;
import com.example.smartify.data.Injection;
import com.example.smartify.R;
import com.example.smartify.editdevice.EditDeviceActivity;
import com.example.smartify.geofence.LocationActivity;
import com.example.smartify.selector.SelectableModeCallback;
import com.example.smartify.cursorrecycleradapter.CursorRecyclerAdapter;
import com.example.smartify.cursorrecycleradapter.SelectableViewHolder;
import com.example.smartify.cursorrecycleradapter.ViewClickListener;
import com.example.smartify.selector.Selector;
import com.example.smartify.selector.SingleSelector;
import com.example.smartify.util.DividerItemDecoration;

/**
 * Activity that displays the devices. It gives information about whether the garage is open/closed
 * and allows the user to open/close it. When the user clicks on a device the
 * <code>EditDeviceActivity</code> is started
 */
public class DevicesActivity extends LocationActivity implements DevicesContract.View,
        ViewClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Listener for the user's UI actions.
     */
    private DevicesContract.UserActionsListener mActionListener;

    /**
     * Adapter to display the devices in the RecyclerView.
     */
    private DevicesAdapter mDevicesAdapter;

    /**
     * Provider for the data to show in UI.
     */
    private LoaderProvider mLoaderProvider;

    /**
     * Type of selector in action mode. Allows for one selection at a time.
     */
    private final Selector mSelector = new SingleSelector();

    /**
     * Contextual action mode, menu that appears when a user is clicked.
     */
    private final ActionMode.Callback actionModeCallback = new SelectableModeCallback(mSelector) {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_devices_actionmode, menu);
            return true;
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    deleteDevice(mSelector.getPosition());
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                default:
                    return false;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.devices);
        assert recyclerView != null;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mDevicesAdapter = new DevicesAdapter();
        recyclerView.setAdapter(mDevicesAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this));

        mActionListener = new DevicesPresenter(this, Injection.provideDevicesRepository
                (getApplicationContext()));

        mLoaderProvider = new LoaderProvider(this);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_devices, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case (R.id.action_settings):
                return true;
            case (R.id.action_add):
                mActionListener.addDevice();
                return true;
            }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mActionListener.loadUI();
    }

    @Override
    public void showAddDevice() {
        Intent intent = new Intent(this, EditDeviceActivity.class);
        startActivity(intent);
    }

    @Override
    public void showDeviceDetail(int id, String name) {
        final Intent intent = new Intent(this, EditDeviceActivity.class);
        intent.putExtra(EditDeviceActivity.EXTRA_ID, id);
        intent.putExtra(EditDeviceActivity.EXTRA_NAME, name);
        startActivity(intent);
    }

    @Override
    public void onViewClicked(int position, View view) {
        Cursor c = mDevicesAdapter.getCursor();
        c.moveToPosition(position);
        int id = c.getInt(0);
        switch (view.getId()) {
            case R.id.device_name:
                mActionListener.openDeviceDetail(id, c.getString(1));
                break;
            case R.id.close:
                mActionListener.close(id);
                break;
            case R.id.open:
                mActionListener.open(id);
                break;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return mLoaderProvider.getDevicesCursorLoader();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mDevicesAdapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mDevicesAdapter.changeCursor(null);
    }

    private void deleteDevice(int position) {
        Cursor c = mDevicesAdapter.getCursor();
        c.moveToPosition(position);
        int id = c.getInt(0);
        mActionListener.deleteDevice(id);
    }

    /**
     * Adapter class that is responsible for selecting what data to display in each ViewHolder.
     */
    private class DevicesAdapter extends CursorRecyclerAdapter<DevicesAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.devices_row, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder) {
            holder.deviceName.setText(getCursor().getString(1));
            int color = getCursor().getInt(2) == 1 ? Color.GREEN : Color.RED;
            ((GradientDrawable) holder.deviceOpened.getBackground()).setColor(color);
        }

        /**
         * Class containing the views contained within a row of the lock list.
         */
        class ViewHolder extends SelectableViewHolder {
            private final TextView deviceName;
            private final Button open;
            private final Button close;
            private final ImageView deviceOpened;


            public ViewHolder(View itemView) {
                super(itemView, mSelector, DevicesActivity.this, actionModeCallback);
                setViewClickListener(DevicesActivity.this);
//                itemView.setOnClickListener(this);
                deviceOpened = (ImageView) itemView.findViewById(R.id.device_opened);
                deviceName = (TextView) itemView.findViewById(R.id.device_name);
                open = (Button) itemView.findViewById(R.id.open);
                close = (Button) itemView.findViewById(R.id.close);

//                deviceOpened.setOnClickListener(this);
                deviceName.setOnClickListener(this);
                open.setOnClickListener(this);
                close.setOnClickListener(this);
//                deviceOpened.setOnLongClickListener(this);
                deviceName.setOnLongClickListener(this);
                open.setOnLongClickListener(this);
                close.setOnLongClickListener(this);
            }
        }
    }
}
