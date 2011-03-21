package net.tedstein.Bucky;

import net.tedstein.Bucky.util.DatasetCursorAdapter;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
		case R.id.add_dataset:
			startActivity(new Intent(this, NewSetActivity.class));
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

		ListView setlist = (ListView)findViewById(R.id.OverviewSetList);
		DatasetCursorAdapter sets_adapter = new DatasetCursorAdapter(
				this,
				sets,
				R.layout.overview_item);
		setlist.setAdapter(sets_adapter);
	}
}