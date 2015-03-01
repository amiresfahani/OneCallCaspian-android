/*
FileSharingBubbleChat.java
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
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.linphone.core.LinphoneChatMessage;
import org.linphone.core.LinphoneChatMessage.State;

import uk.co.onecallcaspian.LinphonePreferences;
import uk.co.onecallcaspian.R;
import uk.co.onecallcaspian.custom.FormattingHelp;
import uk.co.onecallcaspian.custom.adapter.SmiliesListItem;
import uk.co.onecallcaspian.custom.adapter.SmiliesManager;
import uk.co.onecallcaspian.custom.filesharing.FilesharingDownloadTask.DownloadTaskCallback;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

/**
 * @author Lassi Marttala
 * \brief Chat bubble to show media content 
 */
public class FileSharingBubbleChat extends RelativeLayout {
	// Initialization of the widget
	public FileSharingBubbleChat(Context context) {
		super(context);
		init(context);
	}
	public FileSharingBubbleChat(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	public FileSharingBubbleChat(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}
	
	@TargetApi(Build.VERSION_CODES.L) 
	public FileSharingBubbleChat(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context);
	}
	private void init(Context context) {
		this.context = context;
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		findViews();
		prepareViews();
	}

	// Get reference to all views
	private void findViews() {
		mMessage = (TextView) findViewById(R.id.message);
		mImage = (ImageView) findViewById(R.id.image);
		mProgressSpinner = (ProgressBar) findViewById(R.id.spinner);
		mStatusTime = (TextView) findViewById(R.id.time);
		mStatusIcon = (ImageView) findViewById(R.id.status);
		mSoundPlayer = (SoundPlayer) findViewById(R.id.sound_player);
		mVideoPlayer = (VideoView) findViewById(R.id.media);
		
	}
	
	// Do extra initialization for view
	private void prepareViews() {
	}

	// Public API
	// Set everything from a linphone message
	public void setData(LinphoneChatMessage msg) {
		if(mData != null && mData.equals(msg)) {
			return;
		}
		setDataInternal(msg);
	}
	
	// Skip the view change check
	private void setDataInternal(LinphoneChatMessage msg) {
		reset();
		mData = msg;
		setTag(msg.getStorageId());
		setBubble(msg.isOutgoing());
		if(isFileMessage(msg)) {
			showFile(msg.getExternalBodyUrl());
		}
		else {
			showText(msg.getText());
		}
		setStatus(msg.getStatus(), msg.getTime());
	}
	
	// Return the currently shown message
	public LinphoneChatMessage getData() {
		return mData;
	}
	
	// Internal working
	// Reset everything so that this view can be reused
	private void reset() {
		// Change to initilal visibilitys
		mSoundPlayer.reset();
		mMessage.setVisibility(View.GONE);
		mProgressSpinner.setVisibility(View.GONE);
		mImage.setVisibility(View.GONE);
		mStatusIcon.setVisibility(View.GONE);
		mStatusTime.setVisibility(View.GONE);
		mSoundPlayer.setVisibility(View.GONE);
		mVideoPlayer.setVisibility(View.GONE);
	}

	// Set status icon and message time
	private void setStatus(State status, long time) {
		mStatusTime.setText(FormattingHelp.timestampToHumanDate(getContext(), time));
		if (status == LinphoneChatMessage.State.Delivered) {
			mStatusIcon.setImageResource(R.drawable.chat_message_delivered);
		} else if (status == LinphoneChatMessage.State.NotDelivered) {
			mStatusIcon.setImageResource(R.drawable.chat_message_not_delivered);
		} else {
			mStatusIcon.setImageResource(R.drawable.chat_message_inprogress);
		}
	}

	private void showText(String text) {
		Spanned resultText = null;
    	if (context.getResources().getBoolean(R.bool.emoticons_in_messages)) {
    		resultText = getSmiledText(context, getTextWithHttpLinks(text));
    	} else {
    		resultText = getTextWithHttpLinks(text);
    	}
    	mMessage.setText(resultText);
    	mMessage.setVisibility(View.VISIBLE);
	}
	
	public static Spannable getSmiledText(Context context, Spanned spanned) {
		SpannableStringBuilder builder = new SpannableStringBuilder(spanned);
		String text = spanned.toString();

		for(SmiliesListItem item : SmiliesManager.instance().getList()) {
			for(String trigger : item.getTriggers()) {
				int indexOf = text.indexOf(trigger);
				while(indexOf >= 0) {
					int end = indexOf + trigger.length();
					builder.setSpan(new ImageSpan(context, item.getImageResource()), indexOf, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					indexOf = text.indexOf(trigger, end);
				}
			}
		}
		return builder;
	}
	
	public static Spanned getTextWithHttpLinks(String text) {
		if (text.contains("<")) {
			text = text.replace("<", "&lt;");
		}
		if (text.contains(">")) {
			text = text.replace(">", "&gt;");
		}
		if (text.contains("http://")) {
			int indexHttp = text.indexOf("http://");
			int indexFinHttp = text.indexOf(" ", indexHttp) == -1 ? text.length() : text.indexOf(" ", indexHttp);
			String link = text.substring(indexHttp, indexFinHttp);
			String linkWithoutScheme = link.replace("http://", "");
			text = text.replaceFirst(link, "<a href=\"" + link + "\">" + linkWithoutScheme + "</a>");
		}
		if (text.contains("https://")) {
			int indexHttp = text.indexOf("https://");
			int indexFinHttp = text.indexOf(" ", indexHttp) == -1 ? text.length() : text.indexOf(" ", indexHttp);
			String link = text.substring(indexHttp, indexFinHttp);
			String linkWithoutScheme = link.replace("https://", "");
			text = text.replaceFirst(link, "<a href=\"" + link + "\">" + linkWithoutScheme + "</a>");
		}
		
		return Html.fromHtml(text);
	}
	
	private void showFile(String url) {
		if(!url.startsWith("http")) {
			File f = new File(url);
			setFile(f);
			return;
		}

		try {
			URL realUrl = new URL(url);
			FilesharingCache cache = FilesharingCache.instance(getContext());
			
			if(cache.isCached(realUrl)) {
				setFile(cache.getFileFromCache(realUrl));
			}
			else {
				mProgressSpinner.setVisibility(View.VISIBLE);
				FilesharingDownloadTask task = new FilesharingDownloadTask(getContext(), downloadCallback);
				task.execute(realUrl);
			}
		} 
		catch(Exception e) {
			e.printStackTrace();
			Toast.makeText(getContext(), R.string.error_download_failed, Toast.LENGTH_LONG)
			.show();
		}		
	}
	
	private void setFile(File file) {
		String mime = FilesharingCache.getGeneralMimeTypeForFile(file);
		if(mime.startsWith("image")) {
			setImage(file);
		}
		else if(mime.startsWith("audio")) {
			setAudio(file);
		}
		else if(mime.startsWith("video")) {
			setVideo(file);
		}
		
	}
	private void setImage(File f) {
		Bitmap bm = BitmapFactory.decodeFile(f.getAbsolutePath());
		mImage.setImageBitmap(bm);
		mProgressSpinner.setVisibility(View.GONE);
		mImage.setVisibility(View.VISIBLE);
	}
	
	private void setAudio(File file) {
		try {
			mSoundPlayer.setSound(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mSoundPlayer.setVisibility(View.VISIBLE);
	}

	private void setVideo(File file) {
		// TODO Auto-generated method stub
		
	}
	private boolean isFileMessage(LinphoneChatMessage msg) {
		String url = msg.getExternalBodyUrl();
		if(url == null || url.length() < 1) {
			url = extractFileUrlOrNull(msg.getText());
			if(url != null) {
				msg.setExternalBodyUrl(url);
			}
		}
		return url != null;
	}
	
	private String extractFileUrlOrNull(String text) {
		String baseUrl = LinphonePreferences.instance().getSharingPictureServerUrl();

		if(text == null) {
			return null;
		}
		
		Pattern p = Pattern.compile(".*<file>(.*)</file>.*");
		Matcher m = p.matcher(text);
		if(!m.matches()) {
			return null;
		}
		if(m.groupCount() == 1) {
			String url = m.group(1);
			if(!url.startsWith(baseUrl)) {
				return null;
			}
			return url;
		}
		return null;
	}

	private void setBubble(boolean outgoing) {
		ViewGroup v = (ViewGroup) findViewById(R.id.chat_container);
		LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    	
    	if (outgoing) {
    		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
    		v.setBackgroundResource(R.drawable.chat_bubble_outgoing);
    	}
    	else {
    		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
    		v.setBackgroundResource(R.drawable.chat_bubble_incoming);
    	}
    	layoutParams.setMargins(10, 0, 10, 0);
    	v.setLayoutParams(layoutParams);
	}

	// Async handlers
	DownloadTaskCallback downloadCallback = new DownloadTaskCallback() {
		@Override
		public void success(File f) {
			setDataInternal(mData);
		}

		@Override
		public void fail(String reason) {
			Toast.makeText(getContext(), reason, Toast.LENGTH_LONG)
			.show();			
		}
	};
	
	// Data
	LinphoneChatMessage mData;
	
	// Environment information
	private Context context;
	
	// My views
	private TextView 		mMessage;
	private ImageView 		mImage;
	private ProgressBar 	mProgressSpinner;
	private TextView 		mStatusTime;
	private ImageView		mStatusIcon;
	private SoundPlayer		mSoundPlayer;
	private VideoView 		mVideoPlayer;
}
