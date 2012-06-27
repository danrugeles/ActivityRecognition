package com.i2r.dm.samz;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AccelerometerDataSource {
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.ACCDATA_COLUMN_ID,
			MySQLiteHelper.ACCDATA_COLUMN_SESSION,
			MySQLiteHelper.ACCDATA_COLUMN_TIMESTAMP,
			MySQLiteHelper.ACCDATA_COLUMN_X, MySQLiteHelper.ACCDATA_COLUMN_Y,
			MySQLiteHelper.ACCDATA_COLUMN_Z,
			MySQLiteHelper.ACCDATA_COLUMN_RATE,
			MySQLiteHelper.ACCDATA_COLUMN_RSS };
	public static final String DEBUG_TAG = "Accelerometer Log";

	public AccelerometerDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();

	}

	public void close() throws SQLException {
		database.close();
	}

	public void insertData(AccData data) throws Exception {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.ACCDATA_COLUMN_SESSION, data.getSession());
		values.put(MySQLiteHelper.ACCDATA_COLUMN_TIMESTAMP, data.getTime());
		values.put(MySQLiteHelper.ACCDATA_COLUMN_X, data.getX());
		values.put(MySQLiteHelper.ACCDATA_COLUMN_Y, data.getY());
		values.put(MySQLiteHelper.ACCDATA_COLUMN_Z, data.getZ());
		values.put(MySQLiteHelper.ACCDATA_COLUMN_RATE, data.getRate());
		values.put(MySQLiteHelper.ACCDATA_COLUMN_RSS, data.getRSS());
		long insertId = database.insert(MySQLiteHelper.TABLE_ACCDATA, null,
				values);
		Log.i(DEBUG_TAG, "ACC Data inserted with id: " + insertId);
	}

	public List<AccData> getSampleData(int startID, int offset)
			throws SQLException {
		List<AccData> dataList = new ArrayList<AccData>();
		int last = startID + offset;
		Cursor cursor = database.query(MySQLiteHelper.TABLE_ACCDATA,
				allColumns, MySQLiteHelper.ACCDATA_COLUMN_ID + " >= " + startID
						+ " AND " + MySQLiteHelper.ACCDATA_COLUMN_ID + " < "
						+ last, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			AccData data = makeData(cursor);
			dataList.add(data);
			cursor.moveToNext();
		}
		Log.i(DEBUG_TAG, "All categories Returned");
		cursor.close();
		return dataList;
	}

	public int deleteAllData() throws SQLException {
		int num = database.delete(MySQLiteHelper.TABLE_ACCDATA, "1", null);
		Log.i(DEBUG_TAG, num + " Data Deleted");
		return num;

	}
	
	public void deleteSessionById(int id){
		database.delete(MySQLiteHelper.TABLE_ACCDATA, MySQLiteHelper.ACCDATA_COLUMN_SESSION + " = "+ id, null);
	}

	public List<Integer> getAllSessions() throws SQLException {
		List<Integer> sessions = new ArrayList<Integer>();
		Cursor cursor = database.rawQuery("SELECT DISTINCT "
				+ MySQLiteHelper.ACCDATA_COLUMN_SESSION + " FROM "
				+ MySQLiteHelper.TABLE_ACCDATA, null);
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Integer data = cursor.getInt(0);
			sessions.add(data);
			cursor.moveToNext();
		}
		cursor.close();
		return sessions;
	}

	public List<AccData> getAllDataBySessionId(int sessionId) {
		List<AccData> dataList = new ArrayList<AccData>();
		Cursor cursor = database.query(MySQLiteHelper.TABLE_ACCDATA,
				allColumns, MySQLiteHelper.ACCDATA_COLUMN_SESSION + " = "
						+ sessionId, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			AccData data = makeData(cursor);
			dataList.add(data);
			cursor.moveToNext();
		}
		Log.i(DEBUG_TAG, "All categories Returned");
		cursor.close();
		return dataList;
	}

	public List<AccData> getAllData() {
		List<AccData> dataList = new ArrayList<AccData>();
		Cursor cursor = database.query(MySQLiteHelper.TABLE_ACCDATA,
				allColumns, null, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			AccData data = makeData(cursor);
			dataList.add(data);
			cursor.moveToNext();
		}
		Log.i(DEBUG_TAG, "All categories Returned");
		cursor.close();
		return dataList;
	}

	private AccData makeData(Cursor cursor) {
		AccData data = new AccData();
		data.setId(cursor.getInt(0));
		data.setSession(cursor.getInt(1));
		data.setTime(cursor.getLong(2));
		data.setX(cursor.getFloat(3));
		data.setY(cursor.getFloat(4));
		data.setZ(cursor.getFloat(5));
		data.setRate(cursor.getInt(6));
		return data;
	}
}
