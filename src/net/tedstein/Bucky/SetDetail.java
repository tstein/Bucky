package net.tedstein.Bucky;

import java.util.Date;

import net.tedstein.Bucky.util.DatapointAdder;
import net.tedstein.Bucky.util.DatapointCursorAdapter;

import org.apache.commons.math.stat.descriptive.SummaryStatistics;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class SetDetail extends Activity {
	private static final String TAG = "Bucky";

	private int setID;
	private Cursor mPointsCursor;
	private SummaryStatistics mSummaryStatistics;



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.setdetail, menu);
		return true;
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.setdetail_add_datapoint:
			DatapointAdder.createAddPointDialog(this, this.setID);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}


	private void updateStats() {
        // Do a single pass over the data to collect stats.
		mSummaryStatistics.clear();
        int value_index = mPointsCursor.getColumnIndex(BuckyProvider.DP_VALUE);
        mPointsCursor.moveToFirst();
        while (mPointsCursor.isAfterLast() == false) {
            double value = Double.parseDouble(mPointsCursor.getString(value_index));
            mSummaryStatistics.addValue(value);
            mPointsCursor.moveToNext();
        }

        // Fill in stats values from the SummaryStatistics.
        TextView mean = (TextView)findViewById(R.id.SetDetailMeanValue);
        TextView min = (TextView)findViewById(R.id.SetDetailMinValue);
        TextView max = (TextView)findViewById(R.id.SetDetailMaxValue);
        mean.setText(String.format("%.2f", mSummaryStatistics.getMean()));
        min.setText(String.valueOf(mSummaryStatistics.getMin()));
        max.setText(String.valueOf(mSummaryStatistics.getMax()));
	}


	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setdetail);

        Intent i = getIntent();
        this.setID = i.getIntExtra(BuckyProvider.DS_ID, BuckyProvider.NO_DATASET);
        assert this.setID != BuckyProvider.NO_DATASET : "SetDetail was started without a dataset!";

        // Create Activity-persistent objects.
        this.mPointsCursor = managedQuery(BuckyProvider.DATAPOINTS_URI,
                new String[] {
                BuckyProvider.DP_ID,
                BuckyProvider.DP_WHENCREATED,
                BuckyProvider.DP_VALUE },
            BuckyProvider.DP_SETID + "=?",
            new String[] {
                String.valueOf(setID) },
            BuckyProvider.DP_WHENCREATED + " DESC");
        this.mSummaryStatistics = new SummaryStatistics();

        // Retrieve general info from the datasets table.
        Cursor sets = managedQuery(
                Uri.withAppendedPath(BuckyProvider.DATASETS_URI, String.valueOf(this.setID)),
        		new String[] {
        			BuckyProvider.DS_NAME,
        			BuckyProvider.DS_WHENCREATED },
                null,
                null,
        		null);
        int name_index = sets.getColumnIndex(BuckyProvider.DS_NAME);
        int whencreated_index = sets.getColumnIndex(BuckyProvider.DS_WHENCREATED);
        sets.moveToFirst();
        String name = sets.getString(name_index);
        long whencreated = sets.getLong(whencreated_index);
        sets.close();

        // Display all our summary info about this dataset.
        TextView name_view = (TextView)findViewById(R.id.SetDetailName);
        TextView date_view = (TextView)findViewById(R.id.SetDetailDate);
        name_view.setText(name);
        date_view.setText(new Date(whencreated).toString());
        updateStats();

        // Set up the ListView with the individual datapoints.
        ListView points_list = (ListView)findViewById(R.id.SetDetailData);
        DatapointCursorAdapter points_adapter = new DatapointCursorAdapter(this,
                this.mPointsCursor,
                R.layout.setdetail_item);
        points_list.setAdapter(points_adapter);
    }


	protected void onResume() {
		super.onResume();
		LinearLayout header = (LinearLayout)findViewById(R.id.SetDetailHeader);

		switch (Resources.getSystem().getConfiguration().orientation) {
		case Configuration.ORIENTATION_UNDEFINED:
		case Configuration.ORIENTATION_PORTRAIT:
		case Configuration.ORIENTATION_SQUARE:
			header.setOrientation(LinearLayout.VERTICAL);
			Log.d(TAG, "SetDetail: drawing header in portrait.");
			break;
		case Configuration.ORIENTATION_LANDSCAPE:
			header.setOrientation(LinearLayout.HORIZONTAL);
			Log.d(TAG, "SetDetail: drawing header in landscape.");
			break;
		}
	}
}
