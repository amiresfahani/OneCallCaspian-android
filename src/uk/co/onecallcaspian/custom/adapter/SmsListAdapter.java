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

import java.util.Date;

import uk.co.onecallcaspian.R;
import uk.co.onecallcaspian.custom.database.SmsDb;
import android.content.Context;
import android.database.Cursor;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * @author fizzl
 * 
 */
public class SmsListAdapter extends CursorAdapter {
	public SmsListAdapter(Context context) {
		super(context, SmsDb.instance(context).getCursor(), 0);
		this.context = context;
	}
	
	public void updateCursor() {
		this.changeCursor(SmsDb.instance(context).getCursor());
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		ViewGroup v = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.list_item_sms, parent, false);
		bindView(v, context, cursor);
		return v;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView to = (TextView) view.findViewById(R.id.li_sms_to);
		TextView delivered = (TextView) view.findViewById(R.id.li_sms_delivered);
		TextView content = (TextView) view.findViewById(R.id.li_sms_content);
		
		to.setText(cursor.getString(1));
		content.setText(cursor.getString(3));
		
		long deliveryTime = cursor.getLong(2);
		if(deliveryTime < 0) {
			delivered.setText(R.string.failed); 
		} 
		else {
			Date date = new Date(deliveryTime);
			java.text.DateFormat dfmt = DateFormat.getDateFormat(context);
			java.text.DateFormat tfmt = DateFormat.getTimeFormat(context);
			delivered.setText(dfmt.format(date) + " - " + tfmt.format(date));
		}
	}

	Context context;
}
