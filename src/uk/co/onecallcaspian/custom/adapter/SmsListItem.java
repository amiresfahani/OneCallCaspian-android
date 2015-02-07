/*
SmsListItem.java
Copyright (C) 2015 Lassi Marttala, Maxpower Inc (http://maxp.fi)

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

import java.util.Calendar;
import java.util.Date;

import uk.co.onecallcaspian.LinphonePreferences;
import uk.co.onecallcaspian.R;
import uk.co.onecallcaspian.custom.database.SmsDb;
import uk.co.onecallcaspian.custom.rest.SmsHandler;
import uk.co.onecallcaspian.custom.rest.SmsRequestHandlerCallback;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SmsListItem extends LinearLayout {
	public SmsListItem(Context context) {
		super(context);
		init(context);
	}
	public SmsListItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	public SmsListItem(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}
	@TargetApi(Build.VERSION_CODES.L) 
	public SmsListItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context);
	}
	
	private void init(Context context) {
		this.context = context;
	}
	
	public void setData(long id, String to, String text, long deliveryTime) {
		this.id = id;
		this.to = to;
		this.text = text;
		this.deliveryTime = deliveryTime;
		
		TextView delivered = (TextView) findViewById(R.id.li_sms_time);
		TextView content = (TextView) findViewById(R.id.li_sms_content);
		ImageView status = (ImageView) findViewById(R.id.li_sms_status);
		
		content.setText(text);
		
		if(deliveryTime == -1) {
			delivered.setText(R.string.sms_status_sending); 
			status.setImageResource(R.drawable.chat_message_inprogress);
		} 
		else if(deliveryTime == -2) {
			delivered.setText(R.string.failed); 
			status.setImageResource(R.drawable.chat_message_not_delivered);
		} 
		else {
			Calendar yesterday = Calendar.getInstance();
			yesterday.add(Calendar.DATE, -1);
			
			Date date = new Date(deliveryTime);
			java.text.DateFormat dfmt = DateFormat.getDateFormat(context);
			java.text.DateFormat tfmt = DateFormat.getTimeFormat(context);
			status.setImageResource(R.drawable.chat_message_delivered);
			
			if(date.before(yesterday.getTime())) {
				delivered.setText(tfmt.format(date) + " " + dfmt.format(date));				
			} 
			else {
				delivered.setText(tfmt.format(date));				
			}
		}
	}
	
	public void retry() {
		if(deliveryTime > 0) {
			return;
		}
		
		SmsRequestHandlerCallback cb = new SmsRequestHandlerCallback(context, id);
		SmsHandler handler = new SmsHandler(cb);
		
		LinphonePreferences prefs = LinphonePreferences.instance();
		String number = prefs.getAccountUsername(prefs.getDefaultAccountIndex());
		String password = prefs.getAccountPassword(prefs.getDefaultAccountIndex());

		handler.setMyPhoneNumber(number);
		handler.setPassword(password);
		handler.setTo(to);
		handler.setText(text);
		handler.execute();
	}

	public void delete() {
		SmsDb.instance(context).delete(id);
		SmsListAdapter.updateCursorStatic();
	}

	private long id;
	private String to;
	private String text;
	private long deliveryTime;
	private Context context;
}
