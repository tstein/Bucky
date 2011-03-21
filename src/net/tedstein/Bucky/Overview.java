package net.tedstein.Bucky;

import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class Overview extends Activity {
	private static final String TAG = "Bucky";

	private int total_sets = 0;


	private class DatasetCursorAdapter extends CursorAdapter {
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

			int name_index = cursor.getColumnIndex(BuckyProvider.DS_NAME);
			int date_index = cursor.getColumnIndex(BuckyProvider.DS_WHENCREATED);

			name.setText(cursor.getString(name_index));
			date.setText(new Date(cursor.getLong(date_index)).toString());
		}
	}



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