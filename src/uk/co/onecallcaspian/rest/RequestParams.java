package uk.co.onecallcaspian.rest;

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
