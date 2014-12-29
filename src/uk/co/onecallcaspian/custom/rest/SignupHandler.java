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
import uk.co.onecallcaspian.custom.rest.data.SignupJsonData;

public class SignupHandler extends BaseHandler<SignupJsonData> {
	// Construction
	RequestHandlerCallback<SignupJsonData> callback;	
	public SignupHandler(RequestHandlerCallback<SignupJsonData> callback) {
		super(SignupJsonData.class);
		this.callback = callback;
	}
	
	public void execute() {
		if(country == null || countryCode == null || phoneNumber == null) {
			throw new RuntimeException("Missing a parameter in SignupHandler.");
		}
		HandlerParams<SignupJsonData> hp = new HandlerParams<SignupJsonData>();
		hp.callback = this.callback;

		RequestParams reqParams = new RequestParams();
		reqParams.add("phone_code", countryCode);
		reqParams.add("phone_number", phoneNumber);
		reqParams.add("country", country);
		
		reqParams.setMethod("GET");
		reqParams.setUri(WebApis.signup);
		hp.requestParams = reqParams;

		super.execute(hp);
	}
	
	// Parameters
	private String countryCode;
	private String phoneNumber;
	private String country;
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
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

}
