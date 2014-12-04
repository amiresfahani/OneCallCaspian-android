package uk.co.onecallcaspian.custom.rest;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

import com.google.gson.Gson;

public class BaseHandler<T> {	
	// Run this from superclass to start the transaction
	protected void execute(HandlerParams<T> hp) {
		HandlerTask task = new HandlerTask();
		task.execute(hp);
	}
	
	// Construction. Type for GSON parser. Raw type trickery
	private Class<T> type;
	protected BaseHandler(Class<T> returnClassType) {
		this();
		this.type = returnClassType;
	}
	private BaseHandler() {
		
	}
	
	// AsyncTask to do network comms in background
	private class HandlerTask extends AsyncTask<HandlerParams<T>, Void, RequestHandlerCallback<T>> {
		T returnData = null;
		String error = null;
		
		@Override
		protected RequestHandlerCallback<T> doInBackground(HandlerParams<T>... params) {
			HandlerParams<T> p = params[0];
			if(p == null) {
				return null;
			}
			RequestParams requestParams = p.requestParams;
			RequestHandlerCallback<T> callback = p.callback;
			
			if(requestParams.getMethod().equals("GET")) {
				httpGet(requestParams, callback);
			}
			else if(requestParams.getMethod().equals("POST")) {
				httpPost(requestParams, callback);
			}
			else {
				throw new RuntimeException("Unsupported method in BaseHandler");
			}
			return callback;
		}
		
		private void httpGet(RequestParams requestParams, RequestHandlerCallback<T> callback) {
			String uri = requestParams.getUri();
			List<NameValuePair> pairs = requestParams.getParams();
			String toAppend = null;
			if(!pairs.isEmpty()) {
				toAppend = URLEncodedUtils.format(pairs, "utf-8");
			}
			if(toAppend != null) {
				if(!uri.endsWith("?")) {
					uri += "?";
				}
				uri += toAppend;
			}
			HttpGet get = new HttpGet(uri);
			
			HttpClient client = new DefaultHttpClient();
			
			try {
				HttpResponse response = client.execute(get);
				if(response.getStatusLine().getStatusCode() != 200) {
					this.error = "Error while communicating with the server:\n"
							+ response.getStatusLine().getReasonPhrase();
					return;
				}
				
				InputStream is = response.getEntity().getContent();
				String content = IOUtils.toString(is);
				Gson gson = new Gson();
				returnData = gson.fromJson(content, type);
				
			} catch (Exception e) {
				e.printStackTrace();
				error = e.getLocalizedMessage();
				return;
			} 		
		}

		private void httpPost(RequestParams requestParams, RequestHandlerCallback<T> callback) {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void onPostExecute(RequestHandlerCallback<T> result) {
			super.onPostExecute(result);
			if(this.error != null) {
				result.requestHandlerError(this.error);
			}
			else if(this.returnData != null) {
				result.requestHandlerDone(returnData);
			}
			else {
				throw new RuntimeException("Both error and returnData null in BaseHandler");
			}
		}
	}
	
	protected class HandlerParams<E> {
		protected RequestParams requestParams;
		protected RequestHandlerCallback<E> callback;
		
	}
}
