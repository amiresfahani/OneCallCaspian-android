/*
SmsDb.java
Copyright (C) 2014  Lassi Marttala, Maxpower Inc (http://maxp.fi)

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/
package uk.co.onecallcaspian.custom.database;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class SmsDb {
	// Public API
	public long insert(String to, long delivered, String content) {
		long ret = -1;
		db.beginTransaction();
		try {
			stmtInsertSms.bindString(1, to);
			stmtInsertSms.bindLong(2, delivered);
			stmtInsertSms.bindString(3, content);
			ret = stmtInsertSms.executeInsert();
			db.setTransactionSuccessful();
		} 
		catch(Exception e) {
			Log.e("SmsDb", "insert", e);
		}
		db.endTransaction();
		return ret;
	}
	
	public void update(int id, String to, long delivered, String content) {
		db.beginTransaction();
		try {
			stmtUpdateSms.bindString(1, to);
			stmtUpdateSms.bindLong(2, delivered);
			stmtUpdateSms.bindString(3, content);
			stmtUpdateSms.bindLong(4, id);
			stmtUpdateSms.executeUpdateDelete();
			db.setTransactionSuccessful();
		}
		catch(Exception e) {
			Log.e("SmsDb", "update", e);
		}
		db.endTransaction();
	}
	
	public void delete(int id) {
		db.beginTransaction();
		try {
			stmtDeleteSms.bindLong(1, id);
			stmtUpdateSms.executeUpdateDelete();
			db.setTransactionSuccessful();
		}
		catch(Exception e) {
			Log.e("SmsDb", "delete", e);
		}
		db.endTransaction();
	}
	public void close() {
		db.close();
	}
	
	public void open() {
		if(db == null || !db.isOpen()) {
			SmsDbOpenHelper helper = new SmsDbOpenHelper(context);
			db = helper.getWritableDatabase();
			prepareStatements();
		}
	}

	public Cursor getCursor() {
		return db.rawQuery(sqlSelectEverything, null);
	}

	// Under the hood
	private void prepareStatements() {
		stmtInsertSms = db.compileStatement(sqlInsertSms);
		stmtUpdateSms = db.compileStatement(sqlUpdateSms);
		stmtDeleteSms = db.compileStatement(sqlDeleteSms);
		stmtSelectEverything = db.compileStatement(sqlSelectEverything);
	}
	
	private SQLiteDatabase db;

	// SQL Queries
	private static String sqlInsertSms = "INSERT INTO sms('to', 'delivered', 'content') VALUES(?, ?, ?);";
	private static String sqlUpdateSms = "UPDATE sms set 'to'=?, 'delivered'=?, 'content'=? WHERE _id = ?;";
	private static String sqlDeleteSms = "DELETE FROM sms WHERE _id = ?";
	private static String sqlSelectEverything = "SELECT * FROM sms;";
	
	// Compiled statements
	private SQLiteStatement stmtInsertSms, stmtUpdateSms, stmtDeleteSms, stmtSelectEverything;
	
	
	// Singleton 
	private SmsDb() {
	}
	
	
	private SmsDb(Context context) {
		this.context = context;
	}

	public static SmsDb instance(Context context) {
		if(me == null) {
			me = new SmsDb(context);
		}
		me.context = context;
		me.open();
		return me;
	}
	
	private static SmsDb me;
	private Context context;
}

