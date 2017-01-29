package com.example.smartify.selector;

import android.support.v7.widget.RecyclerView;

/**
 * Interface for Selectors. Selectors are used to select one or multiple items from a list.
 */
public interface Selector {

    /**
     * Returns whether the Selector is in selectable mode.
     *
     * @return True if selectable, false otherwise.
     */
    boolean isSelectable();

    /**
     * Set the Selector to selectable mode.
     *
     * @param selectable Enable or disable selectable mode.
     */
    void setSelectable(boolean selectable);

    /**
     * Set a ViewHolder to be selected.
     *
     * @param viewHolder The ViewHolder to be selected.
     * @param selected Select or unselect the ViewHolder.
     */
    void setSelected(RecyclerView.ViewHolder viewHolder, Boolean selected);

    /**
     * Remove all selections.
     */
    void clearSelection();

    /**
     * Get the position of the currently selected ViewHolder.
     *
     * @return The position of the selected ViewHolder
     */
    int getPosition();
}
