package net.tedstein.Bucky;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;


public class BuckyProvider extends ContentProvider {
	private static final String TAG = "Bucky";
	private static final String DB_NAME = "bucky.db";
	private static final int DB_VERSION = 1;
	
	public static final String AUTHORITY = "net.tedstein.Bucky.buckyprovider";
	private static final String DATASET_TABLE = "datasets";
	private static final String DATAPOINT_TABLE = "datapoints";
	public static final String DATASET_TYPE = "net.tedstein.bucky.dataset";
	public static final String DATAPOINT_TYPE = "net.tedstein.bucky.datapoint";

	public static final Uri CONTENT_URI = Uri.parse(
			"content://" + AUTHORITY);
	public static final Uri DATASETS_URI = Uri.parse(
			"content://" + AUTHORITY + "/" + DATASET_TABLE);
	public static final Uri DATAPOINTS_URI = Uri.parse(
			"content://" + AUTHORITY + "/" +DATAPOINT_TABLE);

	public static final int NO_DATASET = 0;
	public static enum Datatype {BAG, SERIES, HASHMAP};
	public static enum URIPattern {DATASET, DATAPOINTS};

	// Column constants.
	// dataset table columns:
	public static final String DS_ID = "_id";
	public static final String DS_NAME = "name";
	public static final String DS_WHENCREATED = "whenCreated";
	public static final String DS_DATATYPE = "datatype";
	public static final String[] DATASET_PROJECTION = new String[] {
		DS_ID, DS_NAME, DS_WHENCREATED, DS_DATATYPE
	};
	private static HashMap<String, String> DATASET_PROJECTION_MAP;

	// datapoint tables' columns:
	public static final String DP_ID = "_id";
	public static final String DP_SETID = "setId";
	public static final String DP_WHENCREATED = "whenCreated";
	public static final String DP_KEY = "key";
	public static final String DP_VALUE = "value";
	public static final String[] DATAPOINT_PROJECTION = new String[] {
		DP_ID, DP_SETID, DP_WHENCREATED, DP_KEY, DP_VALUE
	};
	private static HashMap<String, String> DATAPOINT_PROJECTION_MAP;

	private static UriMatcher urim;
	private DatabaseHelper dbh;

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context c) {
			super(c, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + DATASET_TABLE + " ("
					+ DS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ DS_NAME + " TEXT, "
					+ DS_WHENCREATED + " INTEGER, "
					+ DS_DATATYPE + " INTEGER"
					+ ");");
		
			db.execSQL("CREATE TABLE " + DATAPOINT_TABLE + " ("
					+ DP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ DP_SETID + " INTEGER, "
					+ DP_WHENCREATED + " INTEGER, "
					+ DP_KEY + " TEXT, "
					+ DP_VALUE + " TEXT"
					+ ");");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int old_version, int new_version) {
			// TODO: anything
		}
	}

	static {
		// Set up our URI matcher.
		urim = new UriMatcher(UriMatcher.NO_MATCH);
		urim.addURI(AUTHORITY, DATASET_TABLE, URIPattern.DATASET.ordinal());
		urim.addURI(AUTHORITY, DATAPOINT_TABLE, URIPattern.DATAPOINTS.ordinal());

		// Set up our projection maps.
		DATASET_PROJECTION_MAP = new HashMap<String, String>();
		DATAPOINT_PROJECTION_MAP = new HashMap<String, String>();
		for (int i = 0; i < DATASET_PROJECTION.length; ++i) {
			DATASET_PROJECTION_MAP.put(DATASET_PROJECTION[i], DATASET_PROJECTION[i]);
		}
		for (int i = 0; i < DATAPOINT_PROJECTION.length; ++i) {
			DATAPOINT_PROJECTION_MAP.put(DATAPOINT_PROJECTION[i], DATAPOINT_PROJECTION[i]);
		}
	}




	private boolean validateSetCV(ContentValues cv) {
		// TODO: validate!
		return true;
	}

	private boolean validatePointCV(ContentValues cv) {
		// TODO: validate!
		return true;
	}


	@Override
	public boolean onCreate() {
		dbh = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public String getType(Uri uri) {
		switch (URIPattern.values()[urim.match(uri)]) {
		case DATASET:
			return DATASET_TYPE;
		case DATAPOINTS:
			return DATAPOINT_TYPE;
		default:
			throw new IllegalArgumentException("getType: unmatched URI: " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Log.d(TAG, "Handling insert on URI: " + uri);
		SQLiteDatabase db;
		long rowId;

		switch (URIPattern.values()[urim.match(uri)]) {
		case DATASET:
			if (validateSetCV(values) == false) {
				throw new IllegalArgumentException("Invalid values for a dataset: " + values);
			}

			db = dbh.getWritableDatabase();
			rowId = db.insert(DATASET_TABLE, null, values);
			Uri dataset_uri = ContentUris.withAppendedId(uri, rowId);

			// Good form to notify observers, even though we may not need this functionality.
			getContext().getContentResolver().notifyChange(dataset_uri, null);
			return dataset_uri;
		case DATAPOINTS:
			if (validatePointCV(values) == false) {
				throw new IllegalArgumentException("Invalid values for a datapoint: " + values);
			}

			db = dbh.getWritableDatabase();
			rowId = db.insert(DATASET_TABLE, null, values);
			Uri datapoint_uri = ContentUris.withAppendedId(uri, rowId);

			// Good form to notify observers, even though we may not need this functionality.
			getContext().getContentResolver().notifyChange(datapoint_uri, null);
			return datapoint_uri;
		default:
			throw new IllegalArgumentException("insert: unmatched URI: " + uri);
		}
	}

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO: Allow data deletion.
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO: Allow data modification.
		return 0;
	}

	@Override
	public Cursor query(Uri uri, String[] projection,
			String selection, String[] selectionArgs,
			String sortOrder) {
		Log.d(TAG, "Provider: Handling query on URI " + uri);
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		switch (URIPattern.values()[urim.match(uri)]) {
		case DATASET:
			qb.setTables(DATASET_TABLE);
			qb.setProjectionMap(DATASET_PROJECTION_MAP);
			break;
		case DATAPOINTS:
			qb.setTables(DATAPOINT_TABLE);
			qb.setProjectionMap(DATAPOINT_PROJECTION_MAP);
		default:
			throw new IllegalArgumentException("query: unmatched URI: " + uri);
		}

		SQLiteDatabase db = dbh.getReadableDatabase();
		Cursor c = qb.query(db,
				projection,
				selection,
				selectionArgs,
				null,
				null,
				sortOrder);

		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}
}
