/*
SmsListAdapter.java
Copyright (C) 2014 Lassi Marttala, Maxpower Inc (http://maxp.fi)

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

package uk.co.onecallcaspian.custom.adapter;

import uk.co.onecallcaspian.R;
import uk.co.onecallcaspian.custom.database.SmsDb;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

public class SmsHistoryListAdapter extends CursorAdapter {
	public SmsHistoryListAdapter(Context context) {
		super(context, SmsDb.instance(context).getHistoryListCursor(), 0);
		this.context = context;
		lastInstance = this;
	}
	
	public void updateCursor() {
		this.changeCursor(SmsDb.instance(context).getHistoryListCursor());
	}

	public static void updateCursorStatic() {
		if(lastInstance != null) {
			lastInstance.changeCursor(SmsDb.instance().getHistoryListCursor());
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		ViewGroup v = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.list_item_sms_history, parent, false);
		bindView(v, context, cursor);
		return v;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		SmsHistoryListItem item = (SmsHistoryListItem) view;
		item.setData(cursor.getString(1),
				cursor.getString(3));
	}

	Context context;
	private static SmsHistoryListAdapter lastInstance;
}
