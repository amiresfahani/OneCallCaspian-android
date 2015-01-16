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
import uk.co.onecallcaspian.custom.rest.data.ActivateSmsJsonData;

public class ActivateSmsHandler extends BaseHandler<ActivateSmsJsonData> {
	// Construction
	RequestHandlerCallback<ActivateSmsJsonData> callback;	
	public ActivateSmsHandler(RequestHandlerCallback<ActivateSmsJsonData> callback) {
		super(ActivateSmsJsonData.class);
		this.callback = callback;
	}
	
	public void execute() {
		if(myPhoneNumber == null) {
			throw new RuntimeException("Missing a parameter in ActivateSmsHandler.");
		}
		HandlerParams<ActivateSmsJsonData> hp = new HandlerParams<ActivateSmsJsonData>();
		hp.callback = this.callback;

		RequestParams reqParams = new RequestParams();
		reqParams.add("phone_number", myPhoneNumber);
		reqParams.add("password", password);
		
		reqParams.setMethod("GET");
		reqParams.setUri(WebApis.reg_sms);
		hp.requestParams = reqParams;

		super.execute(hp);
	}
	
	// Parameters
	private String myPhoneNumber;
	private String password;

	public String getMyPhoneNumber() {
		return myPhoneNumber;
	}

	public void setMyPhoneNumber(String myPhoneNumber) {
		this.myPhoneNumber = myPhoneNumber;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}

