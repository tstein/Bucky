package net.tedstein.Bucky.util;

import net.tedstein.Bucky.BuckyProvider;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/*
 * This class will produce a list that shows only the name of each set. To also display the
 * createdAt time, check out DatasetCursorAdapter.
 */
public class SimpleDatasetCursorAdapter extends CursorAdapter {
    public SimpleDatasetCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        TextView name_view = new TextView(context);
        name_view.setTextSize(24);
        name_view.setPadding(3, 4, 0, 4);
        return name_view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int id_index = cursor.getColumnIndex(BuckyProvider.DS_ID);
        int name_index = cursor.getColumnIndex(BuckyProvider.DS_NAME);

        ((TextView)view).setText(cursor.getString(name_index));

        // Store an Integer with the set's ID as view's tag for later retrieval.
        view.setTag(new Integer(cursor.getInt(id_index)));
    }
}
