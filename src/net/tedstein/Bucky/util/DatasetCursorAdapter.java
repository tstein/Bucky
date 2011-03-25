package net.tedstein.Bucky.util;

import java.util.Date;

import net.tedstein.Bucky.BuckyProvider;
import net.tedstein.Bucky.R;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class DatasetCursorAdapter extends CursorAdapter {
    private int layout;

    public DatasetCursorAdapter(Context context, Cursor c, int layout) {
        super(context, c);
        this.layout = layout;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater infl = LayoutInflater.from(context);
        View set_view = infl.inflate(layout, parent, false);
        return set_view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView name = (TextView)view.findViewById(R.id.OverviewItemName);
        TextView date = (TextView)view.findViewById(R.id.OverviewItemDate);

        int id_index = cursor.getColumnIndex(BuckyProvider.DS_ID);
        int name_index = cursor.getColumnIndex(BuckyProvider.DS_NAME);
        int date_index = cursor.getColumnIndex(BuckyProvider.DS_WHENCREATED);

        name.setText(cursor.getString(name_index));
        date.setText(new Date(cursor.getLong(date_index)).toString());

        // Store an Integer with the set's ID as view's tag for later retrieval.
        view.setTag(new Integer(cursor.getInt(id_index)));
    }
}
