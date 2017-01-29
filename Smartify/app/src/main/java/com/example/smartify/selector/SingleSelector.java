package com.example.smartify.selector;

import android.support.v7.widget.RecyclerView;

/**
 * Selector that allows for single selection of ViewHolders.
 */
public class SingleSelector implements Selector {

    /**
     * The ViewHolder that is currently selected.
     */
    private RecyclerView.ViewHolder mViewHolder;

    /**
     * States whether the selector is in selectable mode.
     */
    private boolean isSelectable = false;

    @Override
    public boolean isSelectable() {
        return isSelectable;
    }

    @Override
    public void setSelectable(boolean selectable) {
        isSelectable = selectable;
        if (!isSelectable()) {
            clearSelection();
        }
    }

    @Override
    public void setSelected(RecyclerView.ViewHolder viewHolder, Boolean selected) {
        if (!isSelectable) throw new IllegalStateException("Selector is not selectable.");
        if (selected) {
            if (mViewHolder != null && mViewHolder != viewHolder) {
                mViewHolder.itemView.setSelected(false);
            }
            mViewHolder = viewHolder;
        }
        viewHolder.itemView.setSelected(selected);
    }

    @Override
    public void clearSelection() {
        if (mViewHolder != null) {
            mViewHolder.itemView.setSelected(false);
            mViewHolder = null;
        }
    }

    @Override
    public int getPosition() {
        return mViewHolder.getAdapterPosition();
    }

}
