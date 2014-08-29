package com.flyme.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SqliteHelper extends SQLiteOpenHelper {

	public SqliteHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}
	
	public SqliteHelper(Context context, String name, CursorFactory factory,
			int version,CreateDataBaseListener createDataBaseListener) {
		super(context, name, factory, version);
		this.createDataBaseListener = createDataBaseListener;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		if (createDataBaseListener != null) {
			createDataBaseListener.oncreate(db);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (createDataBaseListener != null) {
			createDataBaseListener.onUpgrade(db, oldVersion, newVersion);
		}
	}
	private CreateDataBaseListener createDataBaseListener ;
	
	
	interface CreateDataBaseListener {
		public void oncreate (SQLiteDatabase db);
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) ;
	}
	
}
