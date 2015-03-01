/*
FilesharingDownloadTask.java
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

package uk.co.onecallcaspian.custom.filesharing;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import uk.co.onecallcaspian.LinphonePreferences;
import uk.co.onecallcaspian.R;
import uk.co.onecallcaspian.custom.rest.data.SendFileJsonData;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.ProgressBar;

import com.google.gson.Gson;

/**
 * @author fizzl
 * \brief Upload a file to the sharing server 
 */
public class FilesharingUploadTask extends AsyncTask<File, Long, URL> {
	public FilesharingUploadTask(Context context, UploadTaskCallback callback, ProgressBar progressBar) {
		super();
		this.context = context;
		this.callback = callback;
		this.progressBar = progressBar;
		progressBar.setProgress(0);
		progressBar.setMax(100);
	}

	@Override
	protected URL doInBackground(File... params) {
		File file = params[0];
		totalSize = file.length();
		String uploadServerUri = context.getResources().getString(R.string.file_sharing_server); 
		
		try {
			HttpPost post = new HttpPost(uploadServerUri);
			CustomMultipartEntity multiPart = new CustomMultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, progressListener);
			LinphonePreferences prefs = LinphonePreferences.instance();
			String username = prefs.getAccountUsername(prefs.getDefaultAccountIndex());
			multiPart.addPart("username", new StringBody(username));
			
			String mime = FilesharingCache.getMimeTypeForFile(file);
			FileBody fb = new FileBody(file, file.getName(), mime, "UTF-8");
			multiPart.addPart("file", fb);
			
			HttpParams httpParameters = new BasicHttpParams();
			HttpProtocolParams.setContentCharset(httpParameters, HTTP.UTF_8);
			HttpProtocolParams.setHttpElementCharset(httpParameters, HTTP.UTF_8);
			HttpClient client = new DefaultHttpClient(httpParameters);
			post.setEntity(multiPart);
			HttpResponse res = client.execute(post);
			if(res.getStatusLine().getStatusCode() != 200) {
				return null;
			}
			InputStream is = res.getEntity().getContent();
			String content = IOUtils.toString(is);
			Gson gson = new Gson();
			SendFileJsonData returnData = gson.fromJson(content, SendFileJsonData.class);
			if(returnData.code < 0) {
				return null;
			}
			String retFn = FilenameUtils.getName(returnData.file);
			String response = uploadServerUri + "?username="+username+"&file="+URLEncoder.encode(retFn, "UTF-8");
			URL ret = new URL(response);
			FilesharingCache.instance(context).cacheLocal(ret, file);
			return ret;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	

	@Override
	protected void onProgressUpdate(Long... values) {
		int percent = (int) (values[0] / (float)totalSize * 100);
		progressBar.setProgress(percent);
	}

	@Override
	protected void onPostExecute(URL result) {
		if(result == null) {
			callback.fail(context.getResources().getString(R.string.error_download_failed));
			return;
		}
		callback.success(result);
	}
	
	private Context context;
	private UploadTaskCallback callback;
	private ProgressBar progressBar;
	private long totalSize;
	
	public interface UploadTaskCallback {
		public void success(URL url);
		public void fail(String reason);
	}
	
	CustomMultipartEntity.ProgressListener progressListener = new CustomMultipartEntity.ProgressListener() {
		@Override
		public void transferred(long num) {
			publishProgress(num);
		}
	};
}
