package com.example.smartify.cursorrecycleradapter;

import android.view.View;

/**
 * Listener for when a view has been clicked.
 */
public interface ViewClickListener {

    /**
     * Callback when a view has been clicked.
     *
     * @param position The position of the ViewHolder that has been clicked.
     * @param view The View in the ViewHolder that has been clicked.
     */
    void onViewClicked(int position, View view);

}
