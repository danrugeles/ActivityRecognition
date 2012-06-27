package com.i2r.dm.samz;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_ACCDATA = "AccelerometerData";
	public static final String ACCDATA_COLUMN_ID = "id";
	public static final String ACCDATA_COLUMN_SESSION = "session";
	public static final String ACCDATA_COLUMN_TIMESTAMP = "timestamp";
	public static final String ACCDATA_COLUMN_X = "x";
	public static final String ACCDATA_COLUMN_Y = "y";
	public static final String ACCDATA_COLUMN_Z = "z";
	public static final String ACCDATA_COLUMN_RATE = "rate";
	public static final String ACCDATA_COLUMN_RSS = "rss";
	
	private static final String DATABASE_NAME = "motion.db";
	private static final int DATABASE_VERSION = 1;
	
	private static final String DATABASE_CREATE_ACCDATA = "create table if not exists "
			+ TABLE_ACCDATA
			+ "( "
			+ ACCDATA_COLUMN_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ ACCDATA_COLUMN_SESSION
			+ " INTEGER,"
			+ ACCDATA_COLUMN_TIMESTAMP
			+ " DATETIME NOT NULL,"
			+ ACCDATA_COLUMN_X
			+" REAL NOT NULL,"
			+ ACCDATA_COLUMN_Y
			+" REAL NOT NULL,"
			+ ACCDATA_COLUMN_Z
			+" REAL NOT NULL,"
			+ ACCDATA_COLUMN_RATE
			+ " INTEGER,"
			+ ACCDATA_COLUMN_RSS
			+" REAL);";
	
	public MySQLiteHelper(Context context) {
	
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public void dropTable(SQLiteDatabase database, String tableName) {
		String command = "drop table if exists " + tableName;
		database.execSQL(command);
	}


	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE_ACCDATA);

	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(MySQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS" + TABLE_ACCDATA);
		onCreate(database);
	}

}
