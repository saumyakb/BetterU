package edu.cornell.info6130.betterU;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss z";
	
	public static final String TABLE_LOG = "priming_log";
	public static final String COLUMN_LOG_ID = "log_id";
	public static final String COLUMN_LOG_IMAGE = "image_name";
	public static final String COLUMN_LOG_TIME = "priming_time";
	public static final String COLUMN_LOG_NOTE = "note";
	
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "primingLog.db";
	
	private static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_LOG + " ("
													+ COLUMN_LOG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT "
													+ " , " + COLUMN_LOG_IMAGE + " TEXT "
													+ " , " + COLUMN_LOG_TIME + " DATE " 
													+ " , " + COLUMN_LOG_NOTE + " TEXT );";
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// creates table if it doesn't already exit
		db.execSQL(SQL_CREATE_TABLE);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(DatabaseHelper.class.getName(), "Upgrading database from " + oldVersion + " to " + newVersion);
		
		// NOTE: consider altering table instead of overwriting it...
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOG);
		onCreate(db);		
	}
}
