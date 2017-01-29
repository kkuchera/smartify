package com.example.smartify.editdevice;

import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import com.example.smartify.R;
import com.example.smartify.data.Injection;
import com.example.smartify.data.LoaderProvider;
import com.example.smartify.geofence.LocationActivity;

/**
 * Activity that lets the user edit the device's configuration.
 */
public class EditDeviceActivity extends LocationActivity implements EditDeviceContract.View,
        LoaderManager.LoaderCallbacks<Cursor>{

    /**
     * Key to retrieve id of the device from the intent's extras.
     */
    public static final String EXTRA_ID = "com.example.smartify.editdevice.EXTRA_ID";

    /**
     * Key to retrieve name of the device from the intent's extras.
     */
    public static final String EXTRA_NAME = "com.example.smartify.editdevice.EXTRA_NAME";

    /**
     * The device's id.
     */
    private int mId;

    /**
     * Indicates whether to create a new device, or update existing one.
     */
    private boolean isNewDevice;

    /**
     * Listener for the user's UI actions.
     */
    private EditDeviceContract.UserActionsListener mActionListener;

    // The edit texts in the view. Used to get and set the text.
    private TextInputEditText mNameEditTextView;
    private TextInputEditText mDeviceIdEditTextView;
    private TextInputEditText mAccessTokenEditTextView;
    private TextInputEditText mRelayNumberEditTextView;
    private TextInputEditText mLatitudeEditTextView;
    private TextInputEditText mLongitudeEditTextView;
    private CheckBox mAutoActionCheckBox;

    /**
     * Provider for the data to show in UI.
     */
    private LoaderProvider mLoaderProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null;
        actionbar.setDisplayHomeAsUpEnabled(true);

        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mId = extras.getInt(EXTRA_ID);
            setTitle(extras.getString(EXTRA_NAME));
            isNewDevice = false;
        } else {
            isNewDevice = true;
        }

        mActionListener = new EditDevicePresenter(this, Injection.provideDevicesRepository
                (getApplicationContext()));

        mNameEditTextView = (TextInputEditText) findViewById(R.id.device_name);
        mDeviceIdEditTextView = (TextInputEditText) findViewById(R.id.device_id);
        mAccessTokenEditTextView = (TextInputEditText) findViewById(R.id.device_access_token);
        mRelayNumberEditTextView = (TextInputEditText) findViewById(R.id.device_relay_number);
        mLatitudeEditTextView = (TextInputEditText) findViewById(R.id.device_latitude);
        mLongitudeEditTextView = (TextInputEditText) findViewById(R.id.device_longitude);
        mAutoActionCheckBox = (CheckBox) findViewById(R.id.device_auto_action);

        if (!isNewDevice) {
            mLoaderProvider = new LoaderProvider(this);
            getLoaderManager().initLoader(0, null, this);
        }

        mNameEditTextView.setText("My Garage");
        mDeviceIdEditTextView.setText("220043001651353530333533");
        mAccessTokenEditTextView.setText("ea89ce40ddd4141583e4d14d24a28afbc5bebb09");
        mRelayNumberEditTextView.setText("1");
        mLatitudeEditTextView.setText("0");
        mLongitudeEditTextView.setText("0");
        mAutoActionCheckBox.setChecked(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_device_edit, menu);
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
            case (R.id.action_done):
                final String name = mNameEditTextView.getText().toString();
                final String deviceId = mDeviceIdEditTextView.getText().toString();
                final String accessToken = mAccessTokenEditTextView.getText().toString();
                final String relayNumber = mRelayNumberEditTextView.getText().toString();
                final String latitude = mLatitudeEditTextView.getText().toString();
                final String longitude = mLongitudeEditTextView.getText().toString();
                final Boolean autoAction = mAutoActionCheckBox.isChecked();
                if (isNewDevice) {
                    mActionListener.attemptAddDevice(name, deviceId, accessToken, relayNumber, latitude, longitude, autoAction);
                } else {
                    mActionListener.attemptUpdateDevice(mId, name, deviceId, accessToken, relayNumber, latitude, longitude, autoAction);
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void clearUIErrors() {
        mNameEditTextView.setError(null);
        mDeviceIdEditTextView.setError(null);
        mAccessTokenEditTextView.setError(null);
    }

    @Override
    public void nameRequired() {
        mNameEditTextView.setError(getString(R.string.error_field_required));
        mNameEditTextView.requestFocus();
    }

    @Override
    public void deviceIdRequired() {
        mDeviceIdEditTextView.setError(getString(R.string.error_field_required));
        mDeviceIdEditTextView.requestFocus();
    }

    @Override
    public void accessTokenRequired() {
        mAccessTokenEditTextView.setError(getString(R.string.error_field_required));
        mAccessTokenEditTextView.requestFocus();
    }

    @Override
    public void relayNumberRequired() {
        mRelayNumberEditTextView.setError(getString(R.string.error_field_required));
        mRelayNumberEditTextView.requestFocus();
    }

    @Override
    public void latitudeRequired() {
        mLatitudeEditTextView.setError(getString(R.string.error_field_required));
        mLatitudeEditTextView.requestFocus();
    }

    @Override
    public void longitudeRequired() {
        mLongitudeEditTextView.setError(getString(R.string.error_field_required));
        mLongitudeEditTextView.requestFocus();
    }

    @Override
    public void deviceIdInvalid() {
        mDeviceIdEditTextView.setError(getString(R.string.error_invalid_field));
        mDeviceIdEditTextView.requestFocus();
    }

    @Override
    public void accessTokenInvalid() {
        mAccessTokenEditTextView.setError(getString(R.string.error_invalid_field));
        mAccessTokenEditTextView.requestFocus();
    }

    @Override
    public void relayNumberInvalid() {
        mRelayNumberEditTextView.setError(getString(R.string.error_invalid_field));
        mRelayNumberEditTextView.requestFocus();
    }

    @Override
    public void openDevices() {
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return mLoaderProvider.getDeviceDetailCursorLoader(mId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            final String name = cursor.getString(0);
            final String deviceId = cursor.getString(1);
            final String accessToken = cursor.getString(2);
            final int relayNumber = cursor.getInt(3);
            final double latitude = cursor.getDouble(4);
            final double longitude = cursor.getDouble(5);
            final boolean autoAction = cursor.getInt(6) == 1;
            updateUIText(name, deviceId, accessToken, String.valueOf(relayNumber), String.valueOf
                    (latitude), String.valueOf(longitude), autoAction);
        } else {
            throw new RuntimeException("Couldn't move cursor to first position.");
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
    }

    public void onSetCurrentLocation(View view) {
        Location location = getLastLocation();
        mLatitudeEditTextView.setText(String.valueOf(location.getLatitude()));
        mLongitudeEditTextView.setText(String.valueOf(location.getLongitude()));
    }

    private void updateUIText(String name, String deviceId, String accessToken, String relayNumber,
                              String latitude, String longitude, boolean autoAction) {
        mNameEditTextView.setText(name);
        mDeviceIdEditTextView.setText(deviceId);
        mAccessTokenEditTextView.setText(accessToken);
        mRelayNumberEditTextView.setText(relayNumber);
        mLatitudeEditTextView.setText(latitude);
        mLongitudeEditTextView.setText(longitude);
        mAutoActionCheckBox.setChecked(autoAction);
    }

}
