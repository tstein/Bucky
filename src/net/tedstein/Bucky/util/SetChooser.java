package net.tedstein.Bucky.util;

import net.tedstein.Bucky.R;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class SetChooser {
    /**
     * Create a dialog in the given context prompting the user to choose a dataset 
     * @param c The context in which to create the dialog.
     * @param cursor A Cursor containing the results of a select from the database table.
     * @param h A SetChoiceHandler that will be invoked after the user makes a selection.
     */
    public static void createSetChooserDialog(Context c, Cursor cursor, SetChoiceHandler h) {
        final Context context = c;
        final SetChoiceHandler handler = h;
        final Dialog set_chooser = new Dialog(context);
        set_chooser.setTitle(context.getString(R.string.choose_set_prompt));

        LayoutInflater infl = LayoutInflater.from(context);
        ListView dialog_contents = (ListView)infl.inflate(R.layout.set_chooser_dialog, null);
        set_chooser.setContentView(dialog_contents);

        SimpleDatasetCursorAdapter sets_adapter = new SimpleDatasetCursorAdapter(context, cursor);
        dialog_contents.setAdapter(sets_adapter);
        dialog_contents.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int setId = (Integer)view.getTag();
                handler.onSetChosen(setId);
                set_chooser.dismiss();
            }
        });

        set_chooser.show();
    }
}
