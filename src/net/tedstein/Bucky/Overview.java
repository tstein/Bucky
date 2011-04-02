package net.tedstein.Bucky;

import net.tedstein.Bucky.util.DatapointAdder;
import net.tedstein.Bucky.util.DatasetCursorAdapter;
import net.tedstein.Bucky.util.SetChoiceHandler;
import net.tedstein.Bucky.util.SetChooser;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Overview extends Activity {
    private static final String TAG = "Bucky";

    private int total_sets = 0;
    private Cursor mSetCursor;



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.overview, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.overview_add_dataset:
            startActivity(new Intent(this, CreateSet.class));
            return true;
        case R.id.overview_add_datapoint:
            final Context context = this;
            if (total_sets == 0) {
                Toast.makeText(context,
                        R.string.add_dataset_first_message,
                        Toast.LENGTH_SHORT)
                .show();
                return true;
            }

            SetChooser.createSetChooserDialog(this, mSetCursor, new SetChoiceHandler(){
                @Override
                public void onSetChosen(int setId) {
                    DatapointAdder.createAddPointDialog(context, setId);
                }
            });
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        // Retrieve the TextView of the selected item so we can set a useful title.
        LinearLayout item_view = (LinearLayout)((AdapterContextMenuInfo)menuInfo).targetView;
        TextView name_view = (TextView)item_view.getChildAt(0);
        CharSequence name = name_view.getText();
        menu.setHeaderTitle(name);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.overview_item_context, menu);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
        TextView name_view = (TextView)((LinearLayout)info.targetView).getChildAt(0);
        final CharSequence name = name_view.getText();
        final int set_id = (Integer)info.targetView.getTag();

        final Context context = this;
        final ContentResolver cr = getContentResolver();

        switch (item.getItemId()) {
        case R.id.overview_item_context_add_point:
            DatapointAdder.createAddPointDialog(context, set_id);
            return true;
        case R.id.overview_item_context_delete:
            new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dataset_delete_prompt, name))
                .setPositiveButton(getString(R.string.confirm), new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Delete all points that belong to the given set.
                        cr.delete(BuckyProvider.DATAPOINTS_URI,
                                BuckyProvider.DP_SETID + "=?",
                                new String[] {
                                    String.valueOf(set_id) });

                        // Then, delete the set.
                        Uri datapoint_uri = Uri.withAppendedPath(
                                BuckyProvider.DATASETS_URI,
                                String.valueOf(set_id));
                        cr.delete(datapoint_uri, null, null);

                        Toast.makeText(context,
                                getString(R.string.dataset_delete_prompt, name),
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(getString(R.string.decline), null)
                .show();
            return true;
        default:
            return super.onContextItemSelected(item);
        } 
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overview);
        final Context context = this;

        mSetCursor = getContentResolver().query(
                BuckyProvider.DATASETS_URI,
                null,
                null,
                null,
                null);

        // See if we have any sets to display.
        total_sets = 0;
        while (mSetCursor.isAfterLast() == false) {
            ++total_sets;
            mSetCursor.moveToNext();
        }
        if (total_sets == 0) {
            Log.d(TAG, "Overview: No sets to display.");
        }

        // Let DatasetCursorAdapter do all the heavy lifting to populate the list.
        ListView setlist = (ListView)findViewById(R.id.OverviewSetList);
        DatasetCursorAdapter sets_adapter = new DatasetCursorAdapter(
                this,
                mSetCursor,
                R.layout.overview_item);
        setlist.setAdapter(sets_adapter);
        setlist.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(context, SetDetail.class);
                i.putExtra(BuckyProvider.DS_ID, (Integer)view.getTag());
                startActivity(i);
            }
        });
        registerForContextMenu(setlist);
    }


    @Override
    protected void onResume() {
        super.onResume();
   }
}