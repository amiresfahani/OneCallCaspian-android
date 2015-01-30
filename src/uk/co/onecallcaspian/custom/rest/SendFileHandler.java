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

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import uk.co.onecallcaspian.custom.WebApis;
import uk.co.onecallcaspian.custom.rest.data.SendFileJsonData;
import android.os.AsyncTask;

import com.google.gson.Gson;

public class SendFileHandler  {
	// Construction
	RequestHandlerCallback<SendFileJsonData> callback;	
	RequestProgressCallback progressCallback;
	
	public SendFileHandler(RequestHandlerCallback<SendFileJsonData> callback, RequestProgressCallback progressCallback) {
		this.callback = callback;
		this.progressCallback = progressCallback;
	}
	
	public void execute() {
		if(userName == null || file == null) {
			throw new RuntimeException("Missing a parameter in SendFileHandler.");
		}
		SendFileTask task = new SendFileTask();
		task.execute();
	}
	
	// AsyncTask for background handling
	private class SendFileTask extends AsyncTask<Void, Integer, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			String uri = WebApis.sharing_server;
			HttpPost post = new HttpPost(uri);

			MultipartEntity multiPart = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			try {
				multiPart.addPart("username", new StringBody(getUserName()));
			} catch (UnsupportedEncodingException e) {
				error = e.getLocalizedMessage();
				return null;
			}
			multiPart.addPart("file", new FileBody(getFile()));
			
			HttpParams httpParameters = new BasicHttpParams();
			HttpProtocolParams.setContentCharset(httpParameters, HTTP.UTF_8);
			HttpProtocolParams.setHttpElementCharset(httpParameters, HTTP.UTF_8);
			HttpClient client = new DefaultHttpClient(httpParameters);
			
			try {
				post.setEntity(multiPart);
			
				HttpResponse response = client.execute(post);
				if(response.getStatusLine().getStatusCode() != 200) {
					this.error = "Error while communicating with the server:\n"
							+ response.getStatusLine().getReasonPhrase();
					return null;
				}
				
				InputStream is = response.getEntity().getContent();
				String content = IOUtils.toString(is);
				Gson gson = new Gson();
				returnData = gson.fromJson(content, SendFileJsonData.class);

			} catch (Exception e) {
				e.printStackTrace();
				error = e.getLocalizedMessage();
				return null;
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if(this.error != null) {
				callback.requestHandlerError(this.error);
			}
			else if(this.returnData != null) {
				callback.requestHandlerDone(returnData);
			}
			else {
				throw new RuntimeException("Both error and returnData null in BaseHandler");
			}
		}
		
		// State
		private String error;
		private SendFileJsonData returnData;
	}
	
	// Parameters
	private String userName;
	private File file;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
}

