/*
SmsRequestHandlerCallback.java
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

package uk.co.onecallcaspian.custom.rest;

import uk.co.onecallcaspian.custom.adapter.SmsListAdapter;
import uk.co.onecallcaspian.custom.database.SmsDb;
import uk.co.onecallcaspian.custom.rest.data.SmsJsonData;
import android.content.Context;
import android.widget.Toast;

public class SmsRequestHandlerCallback implements RequestHandlerCallback<SmsJsonData> {
	public SmsRequestHandlerCallback(Context context, long id) {
		this.context = context;
		this.databaseId = id;
	}

	@Override
	public void requestHandlerDone(SmsJsonData data) {
		if(data.error) {
			requestHandlerError(data.mgs);
		}
		else {
			SmsDb.instance().updateDelivery(databaseId, System.currentTimeMillis());
		}
		SmsListAdapter.updateCursorStatic();
	}

	@Override
	public void requestHandlerError(String reason) {
		Toast.makeText(context, reason, Toast.LENGTH_LONG)
		.show();
		SmsDb.instance().updateDelivery(databaseId, -2);
		SmsListAdapter.updateCursorStatic();
	}

	public long getDatabaseId() {
		return databaseId;
	}

	public void setDatabaseId(long databaseId) {
		this.databaseId = databaseId;
	}

	private long databaseId;
	private Context context;
}
