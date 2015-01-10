/*
SignupHandler.java
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
package uk.co.onecallcaspian.custom.rest;

import uk.co.onecallcaspian.custom.WebApis;
import uk.co.onecallcaspian.custom.rest.data.SmsJsonData;

public class SmsHandler extends BaseHandler<SmsJsonData> {
	// Construction
	RequestHandlerCallback<SmsJsonData> callback;	
	public SmsHandler(RequestHandlerCallback<SmsJsonData> callback) {
		super(SmsJsonData.class);
		this.callback = callback;
	}
	
	public void execute() {
		if(myPhoneNumber == null || to == null || text == null) {
			throw new RuntimeException("Missing a parameter in SmsHandler.");
		}
		HandlerParams<SmsJsonData> hp = new HandlerParams<SmsJsonData>();
		hp.callback = this.callback;

		RequestParams reqParams = new RequestParams();
		reqParams.add("phone_number", myPhoneNumber);
		reqParams.add("password", password);
		reqParams.add("from", myPhoneNumber);
		reqParams.add("receiver", to);
		reqParams.add("text", text);
		
		reqParams.setMethod("POST");
		reqParams.setUri(WebApis.sms);
		hp.requestParams = reqParams;

		super.execute(hp);
	}
	
	// Parameters
	private String myPhoneNumber;
	private String password;
	private String to;
	private String text;

	public String getMyPhoneNumber() {
		return myPhoneNumber;
	}

	public void setMyPhoneNumber(String myPhoneNumber) {
		this.myPhoneNumber = myPhoneNumber;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}

