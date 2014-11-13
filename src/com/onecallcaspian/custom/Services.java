package com.onecallcaspian.custom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.util.Log;

public class Services {

	public static String sendGpsValuesToMail(String message) {
		String jsonresponce = null;
		InputStream ist;
		StringBuilder s;
		String wresponse = null;
		try {
			HttpClient client = new DefaultHttpClient();
			// http://devserver.krify.com/salesnoffers/smtp/testmail.php?message=this%20%3Cbr%3Eis%20test%20%3Cbr%3Eemail
			HttpPost post = new HttpPost("http://devserver.krify.com/salesnoffers/smtp/testmail.php?");
			Log.i("Report", "Report Message is:" + message);
			MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			HttpResponse response = null;

			// Log.i("Services sendGpsValuesToMail", "sendGpsValuesToMail :"
			// +message);
			// Log.i("message", "message :"+message);

			reqEntity.addPart("message", new StringBody(message));

			post.setEntity(reqEntity);
			response = client.execute(post);

			if (response.getStatusLine().getStatusCode() != 200) {
				// Log.d("MyApp", "Server encountered an error");

			}

			HttpEntity resEntity = response.getEntity();
			// Log.d("", "Uploading Response : " + response.getStatusLine());

			ist = resEntity.getContent();

			BufferedReader reader;

			reader = new BufferedReader(new InputStreamReader(

			ist, "UTF-8"));

			String sResponse;
			s = new StringBuilder();

			while ((sResponse = reader.readLine()) != null) {

				s = s.append(sResponse);

			}

			// Log.d("", "Uploading Response : " + response.getStatusLine());

			wresponse = s.toString();

			/*
			 * JSONObject obj = new JSONObject(wresponse);
			 * 
			 * String status = obj.getString("status");
			 * 
			 * if (status.equals("success")) {
			 * 
			 * jsonresponce = "success";
			 * 
			 * } else { jsonresponce = obj.getString("message"); ; }
			 */
			// String url = Webapis.sendsnap_url+"sender=" +
			// senderid+"&receiver="+receiverid+"&text="+text+"&udata="+udata;
			// String data = getDataFromURL(url);

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return wresponse;
	}

	public static String getRegisterStatus(String ph_code, String ph_number, String fname, String lname) {

		String jsonresponce = null;
		InputStream ist;
		StringBuilder s;
		String wresponse = null;
		try {
			HttpClient client = new DefaultHttpClient();

			HttpPost post = new HttpPost(WebApis.signup);

			MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			HttpResponse response = null;
			// http://onecallcaspian.co.uk/mobile/create?phone_code=84&phone_number=979548774&country=us
			// onecallcaspian.co.uk/mobile/create?phone_code=84&phone_number=979548774&firstname=abc&lastname=abc
			reqEntity.addPart("phone_code", new StringBody(ph_code));
			reqEntity.addPart("phone_number", new StringBody(ph_number));
			reqEntity.addPart("firstname", new StringBody(fname));
			reqEntity.addPart("lastname", new StringBody(lname));

			post.setEntity(reqEntity);
			response = client.execute(post);

			if (response.getStatusLine().getStatusCode() != 200) {
				Log.d("MyApp", "Server encountered an error");
			}

			HttpEntity resEntity = response.getEntity();
			// Log.d("", "Uploading Response : " + response.getStatusLine());

			ist = resEntity.getContent();

			BufferedReader reader;

			reader = new BufferedReader(new InputStreamReader(

			ist, "UTF-8"));

			String sResponse;
			s = new StringBuilder();

			while ((sResponse = reader.readLine()) != null) {

				s = s.append(sResponse);

			}

			Log.d("", "Uploading Response : " + response.getStatusLine());
			wresponse = s.toString();

			JSONObject obj = new JSONObject(wresponse);

			String status = obj.getString("status");

			if (status.equals("success")) {

				jsonresponce = "success";

			} else {
				jsonresponce = obj.getString("message");

			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return wresponse;
	}

	public static String getCountriesListDetails() {

		String jsonresponce = null;
		InputStream ist;
		StringBuilder s;
		String wresponse = null;
		try {
			HttpClient client = new DefaultHttpClient();

			HttpPost post = new HttpPost(WebApis.countries);

			MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			HttpResponse response = null;
			// http://onecallcaspian.co.uk/mobile/create?phone_code=84&phone_number=979548774&country=us

			post.setEntity(reqEntity);
			response = client.execute(post);

			if (response.getStatusLine().getStatusCode() != 200) {
				Log.d("MyApp", "Server encountered an error");
			}

			HttpEntity resEntity = response.getEntity();
			// Log.d("", "Uploading Response : " + response.getStatusLine());

			ist = resEntity.getContent();

			BufferedReader reader;

			reader = new BufferedReader(new InputStreamReader(

			ist, "UTF-8"));

			String sResponse;
			s = new StringBuilder();

			while ((sResponse = reader.readLine()) != null) {

				s = s.append(sResponse);

			}

			Log.d("", "Uploading Response : " + response.getStatusLine());
			wresponse = s.toString();

			JSONObject obj = new JSONObject(wresponse);

			String status = obj.getString("status");

			if (status.equals("success")) {

				jsonresponce = "success";

			} else {
				jsonresponce = obj.getString("message");

			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return wresponse;
	}

	public static String getSignUpCodeConfirmation(String access_code) {

		String jsonresponce = null;
		InputStream ist;
		StringBuilder s;
		String wresponse = null;
		try {
			HttpClient client = new DefaultHttpClient();

			HttpPost post = new HttpPost(WebApis.access_code_confirmation);

			MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			HttpResponse response = null;
			// http://onecallcaspian.co.uk/mobile/confirm?code=250585

			reqEntity.addPart("code", new StringBody(access_code));
			post.setEntity(reqEntity);
			response = client.execute(post);

			if (response.getStatusLine().getStatusCode() != 200) {
				Log.d("MyApp", "Server encountered an error");
			}

			HttpEntity resEntity = response.getEntity();
			// Log.d("", "Uploading Response : " + response.getStatusLine());

			ist = resEntity.getContent();

			BufferedReader reader;

			reader = new BufferedReader(new InputStreamReader(

			ist, "UTF-8"));

			String sResponse;
			s = new StringBuilder();

			while ((sResponse = reader.readLine()) != null) {

				s = s.append(sResponse);

			}

			Log.d("", "Uploading Response : " + response.getStatusLine());
			wresponse = s.toString();

			JSONObject obj = new JSONObject(wresponse);

			String status = obj.getString("status");

			if (status.equals("success")) {

				jsonresponce = "success";

			} else {
				jsonresponce = obj.getString("message");

			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return wresponse;
	}

	public static String getForgotPasswordDetails(String country_code, String ph_no) {

		String jsonresponce = null;
		InputStream ist;
		StringBuilder s;
		String wresponse = null;
		try {
			HttpClient client = new DefaultHttpClient();

			HttpPost post = new HttpPost(WebApis.forgot_password);

			MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			HttpResponse response = null;
			// http://onecallcaspian.co.uk/mobile/forgotPassword?phone_code=91&phone_number=9643258963
			// Log.i("country_code","country_code is: "+country_code);
			// Log.i("ph_no","ph_no is: "+ph_no);
			reqEntity.addPart("phone_code", new StringBody(country_code));
			reqEntity.addPart("phone_number", new StringBody(ph_no));
			post.setEntity(reqEntity);
			response = client.execute(post);

			if (response.getStatusLine().getStatusCode() != 200) {
				Log.d("MyApp", "Server encountered an error");
			}

			HttpEntity resEntity = response.getEntity();
			// Log.d("", "Uploading Response : " + response.getStatusLine());

			ist = resEntity.getContent();

			BufferedReader reader;

			reader = new BufferedReader(new InputStreamReader(

			ist, "UTF-8"));

			String sResponse;
			s = new StringBuilder();

			while ((sResponse = reader.readLine()) != null) {

				s = s.append(sResponse);

			}

			Log.d("", "Uploading Response : " + response.getStatusLine());
			wresponse = s.toString();

			JSONObject obj = new JSONObject(wresponse);

			String status = obj.getString("status");

			if (status.equals("success")) {

				jsonresponce = "success";

			} else {
				jsonresponce = obj.getString("message");

			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return wresponse;
	}

	public static String getBalanceDetails(String username, String password) {

		String jsonresponce = null;
		InputStream ist;
		StringBuilder s;
		String wresponse = null;
		try {
			HttpClient client = new DefaultHttpClient();

			HttpPost post = new HttpPost(WebApis.balance);

			MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			HttpResponse response = null;

			// http://onecallcaspian.co.uk/mobile/credit?phone_number=989193655637&password=123456
			reqEntity.addPart("phone_number", new StringBody(username));
			reqEntity.addPart("password", new StringBody(password));

			post.setEntity(reqEntity);
			response = client.execute(post);

			if (response.getStatusLine().getStatusCode() != 200) {
				Log.d("MyApp", "Server encountered an error");
			}

			HttpEntity resEntity = response.getEntity();
			// Log.d("", "Uploading Response : " + response.getStatusLine());

			ist = resEntity.getContent();

			BufferedReader reader;

			reader = new BufferedReader(new InputStreamReader(

			ist, "UTF-8"));

			String sResponse;
			s = new StringBuilder();

			while ((sResponse = reader.readLine()) != null) {

				s = s.append(sResponse);

			}

			Log.d("", "Uploading Response : " + response.getStatusLine());
			wresponse = s.toString();

			JSONObject obj = new JSONObject(wresponse);

			String status = obj.getString("status");

			if (status.equals("success")) {

				jsonresponce = "success";

			} else {
				jsonresponce = obj.getString("message");

			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return wresponse;
	}
}
