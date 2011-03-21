package net.tedstein.Bucky;

import java.util.Calendar;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class NewSetActivity extends Activity {
	@SuppressWarnings("unused")
	private static final String TAG = "Bucky";



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newset);

		final Button submit = (Button)findViewById(R.id.NewSetSubmitButton);
		EditText name_field = (EditText)findViewById(R.id.NewSetNameBox);

		// Set this listener so it is impossible to hit the submit button without entering a name.
		name_field.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() == 0) {
					submit.setEnabled(false);
				} else {
					submit.setEnabled(true);
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				return;
			}
			@Override
			public void afterTextChanged(Editable s) {
				return;
			}
		});

		submit.setEnabled(false);
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String name = ((EditText)findViewById(R.id.NewSetNameBox)).getText().toString();

				ContentValues values = new ContentValues(3);
				values.put(BuckyProvider.DS_NAME, name);
				values.put(BuckyProvider.DS_WHENCREATED, Calendar.getInstance().getTimeInMillis());
				values.put(BuckyProvider.DS_DATATYPE, BuckyProvider.Datatype.SERIES.ordinal());

				getContentResolver().insert(BuckyProvider.DATASETS_URI, values);
				finish();
			}
		});
	}
}
