package com.example.smartify.cursorrecycleradapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;

/**
 * Adapter class that is responsible for selecting what data to display in each ViewHolder of the
 * RecyclerView.
 */
public abstract class CursorRecyclerAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    /**
     * Cursor that contains data requested from the repository.
     */
    private Cursor mCursor;

    @Override
    public void onBindViewHolder(VH holder, int position) {
        if (mCursor == null) {
            throw new IllegalStateException("Cursor is null.");
        }
        if (!mCursor.moveToPosition(position)) {
            throw new RuntimeException("Couldn't move cursor to position " + position);
        }
        onBindViewHolder(holder);
    }

    /**
     * Method must be inherited by subclasses in order to update the views.
     *
     * @param holder The <code>ViewHolder</code> which needs to be updated.
     */
    public abstract void onBindViewHolder(VH holder);

    @Override
    public int getItemCount() {
        return (mCursor == null) ? 0 : mCursor.getCount();
    }

    /**
     * Get the Cursor object containing repository data.
     */
    public Cursor getCursor() {
        return mCursor;
    }

    /**
     * Swap this adapter's cursor and return the old one.
     *
     * @param cursor New cursor for this adapter.
     * @return The old cursor of this adapter.
     */
    public Cursor swapCursor(Cursor cursor) {
        final Cursor oldCursor = mCursor;
        mCursor = cursor;
        notifyDataSetChanged();
//            mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
        return oldCursor;
    }

    /**
     * Set this adaptor's cursor to the new cursor and close the old one.
     *
     * @param cursor New cursor for this adapter.
     */
    public void changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }
}
