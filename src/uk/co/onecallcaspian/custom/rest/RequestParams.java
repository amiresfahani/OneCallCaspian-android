/*
RequestParams.java
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

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class RequestParams {
	private String Uri = "";
	private List<NameValuePair> params = new ArrayList<NameValuePair>();
	private String body = "";
	private String method = "GET";
	
	public void add(String key, String value) {
		NameValuePair newPair = new BasicNameValuePair(key, value);
		params.add(newPair);
	}
	
	public List<NameValuePair> getParams() {
		return params;
	}
	public void setParams(List<NameValuePair> params) {
		this.params = params;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getUri() {
		return Uri;
	}
	public void setUri(String uri) {
		Uri = uri;
	}
}
