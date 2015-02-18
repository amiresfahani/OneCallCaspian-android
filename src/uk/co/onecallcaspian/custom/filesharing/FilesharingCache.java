/*
FilesharingCache.java
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.linphone.mediastream.Log;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.webkit.MimeTypeMap;

/**
 * @author fizzl
 * \brief Convenience class for accessing the file cache 
 * File sharing cache is a class to store and retrieve files by source url.
 * These files are either fetched from the server or returned from the local storage.
 */
public class FilesharingCache {
	public File getFileForUrl(URL url) throws IOException {
		if(!isCached(url)) {
			cache(url);
		}
		return getFileFromCache(url);
	}

	public File getFileFromCache(URL url) throws IOException {
		String file = getHashOfString(url.toExternalForm());
		String mime = getMimeFromFileParameter(url);
		String ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(mime);
		File ret = new File(getCacheDir().getAbsolutePath() + "/" + file + "." + ext);
		if(!ret.exists()) {
			throw new IOException("File for " + url.toExternalForm() + " was not found. \n" + ret.getAbsolutePath());
		}
		return ret;
	}

	private void cache(URL url) throws IOException {
		String dstFileName = getHashOfString(url.toExternalForm());
		String mime = getMimeFromFileParameter(url);
		String ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(mime);
		File dstFile = new File(getCacheDir().getAbsolutePath() + "/" + dstFileName + "." + ext);
		
		URLConnection conn = url.openConnection();
		InputStream is = conn.getInputStream();
		OutputStream os = new FileOutputStream(dstFile);
		
		byte[] buf = new byte[16 * 1024];
		int read = 0;
		while((read = is.read(buf)) > 0) {
			os.write(buf, 0, read);
		}
		os.close();
		is.close();
	}

	private String getMimeFromFileParameter(URL url) {
		Uri uri = Uri.parse(url.toExternalForm());
		String fn = uri.getQueryParameter("file");
		String parsed = Uri.encode(fn);
		String ext = MimeTypeMap.getFileExtensionFromUrl(parsed);
		if(ext != null) {
			return MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
		}
		return null;
	}

	public boolean isCached(URL url) {
		String surl = url.toExternalForm();
		String hash = getHashOfString(surl);
		String mime = getMimeFromFileParameter(url);
		String ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(mime);
		String fn = hash + "." + ext;
		
		File cache = getCacheDir();
		
		for(String s : cache.list()) {
			if(s.equals(fn)) {
				return true;
			}
		}
		return false;
	}
	
	private String getHashOfString(String str) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] digested = md5.digest(str.getBytes("UTF-8"));
			return bytesToHex(digested);
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	private File getCacheDir() {
		File f = Environment.getExternalStorageDirectory();
		String myCacheDir = f.getAbsolutePath() + "/OneCallCaspian/Cache/";
		File ret = new File(myCacheDir);
		if(ret.exists() && ret.isDirectory()) {
			return ret;
		}
		ret.mkdirs();
		return ret;
	}

	public static String getMimeTypeForFile(File file) {
		String ext = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).getEncodedPath());
		return MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
	}

	public static String getGeneralMimeTypeForFile(File file) {
		String mime = getMimeTypeForFile(file);
		if(mime.contains("/")) {
			int slash = mime.indexOf("/");
			mime = mime.substring(0, slash+1);
			mime += "*";
		}
		return mime;
	}
	
	// Singleton
	private FilesharingCache() {
	}
	public static FilesharingCache instance(Context context) {
		if(me == null) {
			me = new FilesharingCache();
		}
		me.context = context;
		return me;
	}
	private static FilesharingCache me;
	private Context context;
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

}
