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
import java.io.IOException;
import java.net.URL;

import uk.co.onecallcaspian.R;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * @author fizzl
 * \brief Use FilesharingCache to get a file
 * Use FilesharingCache asynchronously. This will either download the file
 * or lod it from the cache.
 */
public class FilesharingDownloadTask extends AsyncTask<URL, Void, File> {
	public FilesharingDownloadTask(Context context, DownloadTaskCallback callback) {
		super();
		this.context = context;
		this.callback = callback;
	}

	@Override
	protected File doInBackground(URL... params) {
		try {
			return FilesharingCache.instance(context).getFileForUrl(params[0]);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(File result) {
		if(result == null) {
			callback.fail(context.getResources().getString(R.string.error_download_failed));
			return;
		}
		callback.success(result);
	}
	
	private Context context;
	private DownloadTaskCallback callback;
	
	public interface DownloadTaskCallback {
		public void success(File f);
		public void fail(String reason);
	}
}
