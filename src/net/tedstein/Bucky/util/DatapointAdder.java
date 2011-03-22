package net.tedstein.Bucky.util;

import java.util.Calendar;

import net.tedstein.Bucky.BuckyProvider;
import net.tedstein.Bucky.R;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

public class DatapointAdder {
	private static final String TAG = "Bucky";
	private static final Object DATE_TAG = new Object();
	private static final Object TIME_TAG = new Object();



	/**
	 * Create a dialog in the given context prompting the user to input a new datapoint and attempt
	 * to insert it if he submits the form.
	 * @param c The context in which to create the dialog.
	 * @param setId The ID (BuckyProvider.DS_ID) into which the new point should be inserted.
	 * @return true if a new datapoint was added, false otherwise.
	 */
	public static void createAddPointDialog(Context c, int setId) {
		final Context context = c;
		final int set = setId;
		final Dialog new_datapoint = new Dialog(context);
		new_datapoint.setTitle("New datapoint");

		LayoutInflater infl = LayoutInflater.from(context);
		LinearLayout dialog_contents = (LinearLayout)infl.inflate(R.layout.newpoint_dialog, null);
		new_datapoint.setContentView(dialog_contents);

		// Set up EditText to {en,dis}able the submit button.
		final EditText data_field = (EditText)dialog_contents.findViewById(R.id.NewPointData);
		final Button submit = (Button)dialog_contents.findViewById(R.id.NewPointSubmit);
		data_field.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s.length() == 0) {
					submit.setEnabled(false);
				} else {
					submit.setEnabled(true);
				}
			}
			@Override
			public void afterTextChanged(Editable s) {
				return;
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				return;
			}
		});

		// Set up "now?" checkbox to add Date and Time Pickers if unchecked.
		final CheckBox now = (CheckBox)dialog_contents.findViewById(R.id.NewPointNow);
		final LinearLayout datetime_container =
			(LinearLayout)dialog_contents.findViewById(R.id.NewPointDateTimeContainer);
		now.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				assert isChecked == false : "now? checkbox should never be re-checked!";
				datetime_container.removeAllViews();

				DatePicker date = new DatePicker(context);
				TimePicker time = new TimePicker(context);
				// We'll need to retrieve them later, so tag each with a do-nothing Object.
				date.setTag(DATE_TAG);
				time.setTag(TIME_TAG);

				// Both Pickers are big, so put them side-by-side if we're in landscape.
				switch (Resources.getSystem().getConfiguration().orientation) {
				case Configuration.ORIENTATION_UNDEFINED:
				case Configuration.ORIENTATION_PORTRAIT:
				case Configuration.ORIENTATION_SQUARE:
					// newpoint_dialog.xml is written for portrait, so we can just continue.
					Log.d(TAG, "Overview: Expanding newpoint Dialog in portrait.");
					break;
				case Configuration.ORIENTATION_LANDSCAPE:
					datetime_container.setOrientation(LinearLayout.HORIZONTAL);
					date.setPadding(0, 0, 15, 0);
					Log.d(TAG, "Overview: Expanding newpoint Dialog in landscape.");
					break;
				}

				datetime_container.addView(date);
				datetime_container.addView(time);
			}
		});

		// Set up the button to create a new datapoint record.
		final DatePicker date = (DatePicker)datetime_container.findViewWithTag(DATE_TAG);
		final TimePicker time = (TimePicker)datetime_container.findViewWithTag(TIME_TAG);

		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String data = data_field.getText().toString();
				long createdAt = Calendar.getInstance().getTimeInMillis();
				long timestamp = createdAt;
				if (now.isChecked() == false) {
					assert date != null : "now? was unchecked, but couldn't find DatePicker!";
					assert time != null : "now? was unchecked, but couldn't find TimePicker!";

					Calendar timestamp_cal = Calendar.getInstance();
					timestamp_cal.set(Calendar.YEAR, date.getYear());
					timestamp_cal.set(Calendar.MONTH, date.getMonth());
					timestamp_cal.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());
					timestamp_cal.set(Calendar.HOUR_OF_DAY, time.getCurrentHour());
					timestamp_cal.set(Calendar.MINUTE, time.getCurrentMinute());

					timestamp = timestamp_cal.getTimeInMillis();
				}

				DatapointAdder.insertPoint(context, set, data, createdAt, timestamp);
				new_datapoint.dismiss();
			}
		});

		new_datapoint.show();
	}


	private static void insertPoint(Context context, int setId, String data, long createdAt, long timestamp) {
		ContentValues values = new ContentValues(4);
		values.put(BuckyProvider.DP_SETID, setId);
		values.put(BuckyProvider.DP_WHENCREATED, createdAt);
		values.put(BuckyProvider.DP_KEY, timestamp);
		values.put(BuckyProvider.DP_VALUE, data);

		context.getContentResolver().insert(BuckyProvider.DATAPOINTS_URI, values);
		Toast.makeText(context, "Added datapoint.", Toast.LENGTH_SHORT).show();
	}
}
