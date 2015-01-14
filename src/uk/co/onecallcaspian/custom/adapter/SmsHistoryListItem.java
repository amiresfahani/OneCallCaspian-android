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

import uk.co.onecallcaspian.LinphoneActivity;
import uk.co.onecallcaspian.R;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SmsHistoryListItem extends LinearLayout {
	public SmsHistoryListItem(Context context) {
		super(context);
		init(context);
	}
	public SmsHistoryListItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	public SmsHistoryListItem(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}
	@TargetApi(Build.VERSION_CODES.L) 
	public SmsHistoryListItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context);
	}
	
	private void init(Context context) {
		this.context = context;
	}
	
	public void setData(String from, String lastText) {
		this.from = from;
		
		TextView textFrom = (TextView) findViewById(R.id.sms_history_from);
		TextView textLast = (TextView) findViewById(R.id.sms_history_last_text);

		textFrom.setText(from);
		textLast.setText(lastText);
		
		setOnClickListener(onClick);
	}
	
	private Context context;
	private String from;

	private OnClickListener onClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if(LinphoneActivity.isInstanciated()) {
				if(from.length() > 0) {
					LinphoneActivity.instance().displaySms(from);
				}
			}			
		}
	};
}
