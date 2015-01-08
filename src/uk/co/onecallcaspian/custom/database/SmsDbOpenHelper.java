/*
SmsDbOpenHelper.java
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

import java.io.IOException;

import org.linphone.mediastream.Log;

import uk.co.onecallcaspian.R;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SmsDbOpenHelper extends SQLiteOpenHelper {
	public SmsDbOpenHelper(Context context) {
		this(context, context.getResources().getString(R.string.sms_db_name), null, 
				Integer.parseInt(context.getResources().getString(R.string.sms_db_version)));
	}

	public SmsDbOpenHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		this.context = context;
		dbName = context.getResources().getString(R.string.sms_db_name);
		dbVersion = Integer.parseInt(context.getResources().getString(R.string.sms_db_version));
		sqlFileName = "database/" +  
				dbName + "-" + Integer.toString(dbVersion) + ".sql";
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			DbUtils.executeSqlScript(context, db, sqlFileName);
		} catch (IOException e) {
			Log.e(e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Implement needed upgrade logic here when new database version is
		// needed

	}

	private Context context;
	private String sqlFileName;
	private String dbName; 
	private int dbVersion;
}
