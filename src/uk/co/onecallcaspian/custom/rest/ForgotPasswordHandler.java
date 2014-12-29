/*
ForgotPasswordHandler.java
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
import uk.co.onecallcaspian.custom.rest.data.ForgotPasswordJsonData;

public class ForgotPasswordHandler extends BaseHandler<ForgotPasswordJsonData> {
	// Construction
	RequestHandlerCallback<ForgotPasswordJsonData> callback;	
	public ForgotPasswordHandler(RequestHandlerCallback<ForgotPasswordJsonData> callback) {
		super(ForgotPasswordJsonData.class);
		this.callback = callback;
	}
	
	public void execute() {
		if(countryCode == null || phoneNumber == null) {
			throw new RuntimeException("Missing a parameter in ForgotPasswordHandler.");
		}
		HandlerParams<ForgotPasswordJsonData> hp = new HandlerParams<ForgotPasswordJsonData>();
		hp.callback = this.callback;

		RequestParams reqParams = new RequestParams();
		reqParams.add("phone_code", countryCode);
		reqParams.add("phone_number", phoneNumber);
		
		reqParams.setMethod("GET");
		reqParams.setUri(WebApis.forgot_password);
		hp.requestParams = reqParams;

		super.execute(hp);
	}
	
	// Parameters
	private String countryCode;
	private String phoneNumber;
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
}
