package com.example.smartify.cursorrecycleradapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.View;

import com.example.smartify.selector.Selector;

/**
 * ViewHolder that can be selected.
 */
public abstract class SelectableViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    /**
     * Listener for when a view has been clicked.
     */
    private ViewClickListener mListener;

    /**
     * Type of selector in action mode. Allows for one or more selections at a time.
     */
    private final Selector mSelector;

    /**
     * The callback for when entering/exiting ActionMode.
     */
    private final ActionMode.Callback mActionModeCallback;

    /**
     * The Activity on which to start ActionMode.
     */
    private final Activity mActivity;

    /**
     * Construct a new SelectableViewHolder.
     *
     * @param itemView The item's view.
     * @param selector The selector used in this action mode.
     * @param activity The activity on which to start the ActionMode.
     * @param callback The ActionMode callback with activity specific actions.
     */
    public SelectableViewHolder(View itemView, Selector selector, Activity activity,
                                ActionMode.Callback callback) {
        super(itemView);
        mSelector = selector;
        mActivity = activity;
        mActionModeCallback = callback;
    }

    /**
     * Set a ViewClickListener for when a view has been clicked.
     *
     * @param listener The listener to which callbacks are done.
     */
    public void setViewClickListener(ViewClickListener listener) {
        mListener = listener;
    }

    /**
     * If in selectable mode, set ths ViewHolder to selected, otherwise tell the listener that a
     * view in this ViewHolder has been clicked.
     *
     * @param view The View that has been clicked.
     */
    @Override
    public void onClick(View view) {
        if (mSelector.isSelectable()) {
            mSelector.setSelected(this, true);
        } else {
            if (mListener != null) {
                mListener.onViewClicked(getAdapterPosition(), view);
            }
        }
    }

    /**
     * If in not in selectable mode, go to selectable mode and set the ViewHolder selected,
     * otherwise do nothing.
     *
     * @param view The View that has been clicked.
     */
    @Override
    public boolean onLongClick(View view) {
        if (!mSelector.isSelectable()){
            mActivity.startActionMode(mActionModeCallback);
            mSelector.setSelectable(true);
            mSelector.setSelected(this, true);
            return true;
        }
        return false;
    }

}
