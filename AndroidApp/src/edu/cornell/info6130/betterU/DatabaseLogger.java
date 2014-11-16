package edu.cornell.info6130.betterU;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DatabaseLogger {
	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;
	
	public DatabaseLogger(Context context) {
		dbHelper = new DatabaseHelper(context);
	}
	
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}
	
	public void close() {
		dbHelper.close();
	}
	
	@SuppressLint("SimpleDateFormat")
	public long add(DatabaseLog log) {
		// prepare to insert into database
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.COLUMN_LOG_IMAGE, log.getImageName());
		// convert datetime to string value for storage
		SimpleDateFormat dateformat = new SimpleDateFormat(DatabaseHelper.DATE_TIME_FORMAT);		
		values.put(DatabaseHelper.COLUMN_LOG_TIME, dateformat.format(log.getPrimingTime()));
		values.put(DatabaseHelper.COLUMN_LOG_NOTE, log.getNote());
		
		long logID = database.insert(DatabaseHelper.TABLE_LOG,  null,  values);
		
		return logID;
	}
	
	public void deleteAll() throws SQLException {
		// deletes everything from specified database table
		database.execSQL("DELETE FROM " + DatabaseHelper.TABLE_LOG + ";");
		return;
	}
	
	public List<DatabaseLog> getAll() {		
		List<DatabaseLog> logs = new ArrayList<DatabaseLog>();
		// query table
		Cursor cursor = database.query(DatabaseHelper.TABLE_LOG 
										, 	new String[] {DatabaseHelper.COLUMN_LOG_ID
														, DatabaseHelper.COLUMN_LOG_IMAGE
														, DatabaseHelper.COLUMN_LOG_TIME
														, DatabaseHelper.COLUMN_LOG_NOTE}
										, null, null, null, null, null);
							
		// loop thru results
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			DatabaseLog log = cursorToLog(cursor);
			logs.add(log);
			cursor.moveToNext();
		}
		
		// close our cursor
		cursor.close();
		
		return logs;
	}
	
	@SuppressLint("SimpleDateFormat")
	private DatabaseLog cursorToLog(Cursor cursor) {
		DatabaseLog log = new DatabaseLog();
		
		log.setId(cursor.getLong(0));
		log.setImageName(cursor.getString(1));
		
		SimpleDateFormat sdf = new SimpleDateFormat(DatabaseHelper.DATE_TIME_FORMAT);
		try {
			log.setPrimingTime(sdf.parse(cursor.getString(2)));
		} catch (Exception ex) {
			if (BuildConfig.DEBUG) { 
				Log.e(DatabaseLogger.class.getName() + ".cursorToLog", ex.toString(), ex);
			}
		}		
		log.setNote(cursor.getString(3));
		
		return log;
	}
}
