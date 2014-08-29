package com.flyme.sqlite;

import com.flyme.sqlite.SqliteHelper.CreateDataBaseListener;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class SqliteManager {

	private SqliteManager(){}
	
    private static final Object sPoolSync = new Object();
    private static SqliteManager sPool;
    private SqliteManager next;
    private static int sPoolSize = 0;
    private static final int MAX_POOL_SIZE = 10;
	
	public static SqliteManager obtain(){
		synchronized (sPoolSync) {
            if (sPool != null) {
                SqliteManager m = sPool;
                sPool = m.next;
                m.next = null;
                sPoolSize--;
                return m;
            }
        }
		return new SqliteManager();
	}
	
	public void recycle() {
        synchronized (sPoolSync) {
            if (sPoolSize < MAX_POOL_SIZE) {
                next = sPool;
                sPool = this;
                sPoolSize++;
            }
        }
    }
	
	private Context context;
	private  String DATABASE_NAME = "";
	private final static String DEFULT_DATABASE_NAME = "data.db";
	private  int VERSION = 1;
	private final static int DEFULT_VESION = 1;
	private SqliteHelper helper ; 
	
	public void createDataBase(Context context,final String sql){
		this.context = context;
		if (context != null) {
			CreateDataBaseListener createDataBaseListener = new CreateDataBaseListener() {
				@Override
				public void oncreate(SQLiteDatabase db) {
					db.execSQL(sql);
				}
				@Override
				public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
					
				}
			};
			if (DATABASE_NAME .equals("")) {
				helper = new SqliteHelper(context,DEFULT_DATABASE_NAME,null,DEFULT_VESION,createDataBaseListener);
			}else {
				helper = new SqliteHelper(context,DATABASE_NAME,null,VERSION,createDataBaseListener);
			}
		}
		SQLiteDatabase database = helper.getReadableDatabase();
		database.close();
	}

	public void setDATABASE_NAME(String dATABASE_NAME , int version) {
		DATABASE_NAME = dATABASE_NAME;
		VERSION = version;
	}
	
	private SQLiteDatabase db ;
	
	
	public boolean inserteData(String table , String nullColumnHack , ContentValues values){
		initDB();
		long insert = db.insert(table, nullColumnHack, values );
		if (insert > 0) {
			return true;
		}else {
			return false;
		}
	}
	
	public boolean deletedData(String table,String where ,String... whereArgs ){
		initDB();
		int delete = db.delete(table, where, whereArgs);
		if (delete > 0) {
			return true;
		}else {
			return false;
		}
	}
	
	
	public boolean alterData(String table, ContentValues values, String whereClause, String[] whereArgs){
		initDB();
		int update = db.update(table, values, whereClause, whereArgs);
		if (update > 0) {
			return true;
		}else {
			return false;
		}
	}
	
	
	public Cursor querySingleData(String table, String[] columns, String selection, String[] selectionArgs){
		initDB();
		Cursor query = db.query(table, columns, selection, selectionArgs, null, null, null);
		return query;
	}
	
	
	public void close (){
		if (db != null && db.isOpen()) {
			db.close();
		}
	}
	
	private void initDB () {
		if (db == null) {
			db = helper.getWritableDatabase();
		}
	}
}
