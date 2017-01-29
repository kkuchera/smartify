package com.example.smartify.selector;

import android.view.ActionMode;
import android.view.Menu;

/**
 * Callbacks for selectable mode which is an action mode where list items can be selected.
 */
public abstract class SelectableModeCallback implements ActionMode.Callback {

    /**
     * The type of selector used in the selectable mode.
     */
    private final Selector mSelector;

    /**
     * Constructor to create a SelectableModeCallback.
     *
     * @param selector The type of selector used in this selectable mode.
     */
    public SelectableModeCallback(Selector selector) {
        mSelector = selector;
    }

    // Called each time the action mode is shown. Always called after onCreateActionMode, but
    // may be called multiple times if the mode is invalidated.
    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        mSelector.setSelectable(true);
        return false; // Return false if nothing is done
    }

    // Called when the user exits the action mode
    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mSelector.setSelectable(false);
    }
}
