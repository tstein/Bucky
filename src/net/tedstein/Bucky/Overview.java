package net.tedstein.Bucky;

import net.tedstein.Bucky.util.DatapointAdder;
import net.tedstein.Bucky.util.DatasetCursorAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class Overview extends Activity {
    private static final String TAG = "Bucky";

    private int total_sets = 0;



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
            DatapointAdder.createAddPointDialog(this, 1);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overview);
    }


    @Override
    protected void onResume() {
        super.onResume();
        final Context context = this;

        // Query BuckyProvider to see if we have any datasets to show.
        Cursor sets = getContentResolver().query(
                BuckyProvider.DATASETS_URI,
                null,
                null,
                null,
                null);
        while (sets.isAfterLast() == false) {
            ++total_sets;
            sets.moveToNext();
        }

        if (total_sets == 0) {
            sets.close();
            Log.d(TAG, "Overview: No sets to display.");
            return;
        }

        // Let DatasetCursorAdapter do all the heavy lifting to populate the list.
        ListView setlist = (ListView)findViewById(R.id.OverviewSetList);
        DatasetCursorAdapter sets_adapter = new DatasetCursorAdapter(
                this,
                sets,
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
    }
}