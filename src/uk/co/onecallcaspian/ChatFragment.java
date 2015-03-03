package uk.co.onecallcaspian;
/*
ChatFragment.java
Copyright (C) 2012  Belledonne Communications, Grenoble, France

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
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.util.ByteArrayBuffer;
import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneChatMessage;
import org.linphone.core.LinphoneChatMessage.State;
import org.linphone.core.LinphoneChatRoom;
import org.linphone.core.LinphoneCore;
import org.linphone.mediastream.Log;

import uk.co.onecallcaspian.LinphoneManager.AddressType;
import uk.co.onecallcaspian.LinphoneSimpleListener.LinphoneOnComposingReceivedListener;
import uk.co.onecallcaspian.compatibility.Compatibility;
import uk.co.onecallcaspian.custom.FormattingHelp;
import uk.co.onecallcaspian.custom.filesharing.ChatMessageAdapter;
import uk.co.onecallcaspian.custom.filesharing.FileSharingBubbleChat;
import uk.co.onecallcaspian.custom.filesharing.FilesharingCache;
import uk.co.onecallcaspian.custom.filesharing.FilesharingUploadTask;
import uk.co.onecallcaspian.custom.filesharing.OnPauseListener;
import uk.co.onecallcaspian.custom.filesharing.FilesharingUploadTask.UploadTaskCallback;
import uk.co.onecallcaspian.custom.fragment.SmiliesDialogFragment;
import uk.co.onecallcaspian.custom.fragment.SmiliesDialogFragment.SmilieDialogListener;
import uk.co.onecallcaspian.ui.AvatarWithShadow;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Sylvain Berfini
 */
public class ChatFragment extends Fragment implements OnClickListener, LinphoneChatMessage.StateListener, LinphoneOnComposingReceivedListener, SmilieDialogListener {
	private static final int ADD_PHOTO = 9;
	private static final int ADD_AUDIO = 10;
	private static final int ADD_VIDEO = 11;
	private static final int MENU_DELETE_MESSAGE = 0;
	private static final int MENU_SAVE_PICTURE = 1;
	private static final int MENU_SHOW_FILE = 8;
	private static final int DEBUG_MENU_DO_NOTHING = 2001;
	private static final int MENU_FILE_IMAGE = 2;
	private static final int MENU_FILE_AUDIO = 3;
	private static final int MENU_FILE_VIDEO = 4;
	private static final int MENU_COPY_TEXT = 6;
	private static final int MENU_RESEND_MESSAGE = 7;
	private static final int COMPRESSOR_QUALITY = 100;
	private static final int SIZE_SMALL = 500;
	private static final int SIZE_MEDIUM = 1000;
	private static final int SIZE_LARGE = 1500;

	private LinphoneChatRoom chatRoom;
	private View view;
	private String sipUri;
	private EditText message;
	private ImageView cancelUpload, callButton;
	private TextView sendImage, selectSmiley, sendMessage, contactName, remoteComposing;
	private AvatarWithShadow contactPicture;
	private RelativeLayout uploadLayout, textLayout;
	private Handler mHandler = new Handler();
	private boolean useLinphoneMessageStorage;
	private ListView messagesList;

	private ProgressBar progressBar;
	private String fileToUploadPath;
	private Bitmap imageToUpload;
	private Uri imageToUploadUri;
	private TextWatcher textWatcher;
	private OnGlobalLayoutListener keyboardListener;
	private ChatMessageAdapter adapter;
	private static ChatFragment me;

	public static ChatFragment instance() {
		return me;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		me = this;
		sipUri = getArguments().getString("SipUri");
		String displayName = getArguments().getString("DisplayName");
		String pictureUri = getArguments().getString("PictureUri");

        view = inflater.inflate(R.layout.chat, container, false);

        callButton = (ImageView) view.findViewById(R.id.call);

		callButton.setOnClickListener(onCall);
        useLinphoneMessageStorage = getResources().getBoolean(R.bool.use_linphone_chat_storage);

        contactName = (TextView) view.findViewById(R.id.contactName);
        contactPicture = (AvatarWithShadow) view.findViewById(R.id.contactPicture);

        sendMessage = (TextView) view.findViewById(R.id.sendMessage);
        sendMessage.setOnClickListener(this);

        selectSmiley = (TextView) view.findViewById(R.id.selectSmiley);
        selectSmiley.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectSmiley();
			}
		});
        
        remoteComposing = (TextView) view.findViewById(R.id.remoteComposing);
        remoteComposing.setVisibility(View.GONE);
        
        messagesList = (ListView) view.findViewById(R.id.chatMessageList);
        registerForContextMenu(messagesList);

		messagesList.setOnItemClickListener(onMessageClick);
        messagesList.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        messagesList.setStackFromBottom(true);

        
        message = (EditText) view.findViewById(R.id.message);
        if (!getActivity().getResources().getBoolean(R.bool.allow_chat_multiline)) {
        	message.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE);
        	message.setMaxLines(1);
        }
        
        uploadLayout = (RelativeLayout) view.findViewById(R.id.uploadLayout);
        uploadLayout.setVisibility(View.GONE);
        textLayout = (RelativeLayout) view.findViewById(R.id.messageLayout);
        
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        sendImage = (TextView) view.findViewById(R.id.sendPicture);
        if (!getResources().getBoolean(R.bool.disable_chat_send_file)) {
	        registerForContextMenu(sendImage);
	        sendImage.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					sendImage.showContextMenu();
				}
			});
        } else {
        	sendImage.setEnabled(false);
        }
  
        cancelUpload = (ImageView) view.findViewById(R.id.cancelUpload);
        cancelUpload.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Cancel task
				uploadLayout.setVisibility(View.GONE);
				textLayout.setVisibility(View.VISIBLE);
				progressBar.setProgress(0);
			}
		});

        LinphoneCore lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
		if (lc != null) {
			chatRoom = lc.getOrCreateChatRoom(sipUri);
			//Only works if using liblinphone storage
			chatRoom.markAsRead();
		}
		
        displayChatHeader(displayName, pictureUri);

        textWatcher = new TextWatcher() {
			public void afterTextChanged(Editable arg0) {

			}

			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}

			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
			{
				if (message.getText().toString().equals("")) {
					sendMessage.setEnabled(false);
				} else {
					if (chatRoom != null)
						chatRoom.compose();
					sendMessage.setEnabled(true);
				}
			}
		};

		// Force hide keyboard
		if (LinphoneActivity.isInstanciated()) {
			InputMethodManager imm = (InputMethodManager) LinphoneActivity.instance().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(),0);
		}

		// Workaround for SGS3 issue
		if (savedInstanceState != null) {
			fileToUploadPath = savedInstanceState.getString("fileToUploadPath");
			imageToUpload = savedInstanceState.getParcelable("imageToUpload");
		}
		if (fileToUploadPath != null || imageToUpload != null) {
			sendImage.post(new Runnable() {
				@Override
				public void run() {
					sendImage.showContextMenu();
				}
			});
		}

		return view;
    }

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putString("fileToUploadPath", fileToUploadPath);
		outState.putParcelable("imageToUpload", imageToUpload);
		outState.putString("messageDraft", message.getText().toString());
		super.onSaveInstanceState(outState);
	}

	private void addVirtualKeyboardVisiblityListener() {
		keyboardListener = new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
			    Rect visibleArea = new Rect();
			    view.getWindowVisibleDisplayFrame(visibleArea);

			    int heightDiff = view.getRootView().getHeight() - (visibleArea.bottom - visibleArea.top);
			    if (heightDiff > 200) {
			    	showKeyboardVisibleMode();
			    } else {
			    	hideKeyboardVisibleMode();
			    }
			}
		};
		view.getViewTreeObserver().addOnGlobalLayoutListener(keyboardListener);
	}

	private void removeVirtualKeyboardVisiblityListener() {
		Compatibility.removeGlobalLayoutListener(view.getViewTreeObserver(), keyboardListener);
	}

	public void showKeyboardVisibleMode() {
		LinphoneActivity.instance().hideMenu(true);
		contactPicture.setVisibility(View.GONE);
		scrollToEnd();
	}

	public void hideKeyboardVisibleMode() {
		LinphoneActivity.instance().hideMenu(false);
		contactPicture.setVisibility(View.VISIBLE);
		//scrollToEnd();
	}
	
	
	public View getBubble(int position){
		return messagesList.getChildAt(position);
	}
	
	public void dispayMessageList(){
		 adapter = new ChatMessageAdapter(this.getActivity());
		 adapter.refreshHistory(chatRoom);
		 messagesList.setAdapter(adapter);  
	}

	private void displayChatHeader(String displayName, String pictureUri) {
		if (displayName == null && getResources().getBoolean(R.bool.only_display_username_if_unknown) && LinphoneUtils.isSipAddress(sipUri)) {
        	contactName.setText(LinphoneUtils.getUsernameFromAddress(sipUri));
		} else if (displayName == null) {
			contactName.setText(sipUri);
		} else {
			contactName.setText(displayName);
		}

        if (pictureUri != null) {
        	LinphoneUtils.setImagePictureFromUri(view.getContext(), contactPicture.getView(), Uri.parse(pictureUri), R.drawable.unknown_small);
        } else {
        	contactPicture.setImageResource(R.drawable.unknown_small);
        }
	}


	public void changeDisplayedChat(String newSipUri, String displayName, String pictureUri) {
		if (!message.getText().toString().equals("") && LinphoneActivity.isInstanciated()) {
			ChatStorage chatStorage = LinphoneActivity.instance().getChatStorage();
			if (chatStorage.getDraft(sipUri) == null) {
				chatStorage.saveDraft(sipUri, message.getText().toString());
			} else {
				chatStorage.updateDraft(sipUri, message.getText().toString());
			}
		} else if (LinphoneActivity.isInstanciated()) {
			LinphoneActivity.instance().getChatStorage().deleteDraft(sipUri);
		}

		sipUri = newSipUri;
		if (LinphoneActivity.isInstanciated()) {
			String draft = LinphoneActivity.instance().getChatStorage().getDraft(sipUri);
			if (draft == null)
				draft = "";
			message.setText(draft);
		}

		displayChatHeader(displayName, pictureUri);
		displayMessages();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.sendPicture) {/*
			menu.add(0, DEBUG_MENU_DO_NOTHING, 0, getString(R.string.no));
			menu.add(0, DEBUG_MENU_DO_NOTHING, 0, getString(R.string.no));
			menu.add(0, DEBUG_MENU_DO_NOTHING, 0, getString(R.string.no));
			menu.add(0, DEBUG_MENU_DO_NOTHING, 0, getString(R.string.no));
			menu.add(0, DEBUG_MENU_DO_NOTHING, 0, getString(R.string.no));*/
			menu.add(0, MENU_FILE_IMAGE, 0, getString(R.string.share_file_image));
			menu.add(0, MENU_FILE_AUDIO, 0, getString(R.string.share_file_audio));
			menu.add(0, MENU_FILE_VIDEO, 0, getString(R.string.share_file_video));
		} else {
			 AdapterView.AdapterContextMenuInfo info =
			            (AdapterView.AdapterContextMenuInfo) menuInfo;
			FileSharingBubbleChat item = (FileSharingBubbleChat) info.targetView;
			LinphoneChatMessage msg = item.getData().getMessage(); 
			int id = msg.getStorageId();
			menu.add(id, MENU_DELETE_MESSAGE, 0, getString(R.string.delete));
			ImageView iv = (ImageView) item.findViewById(R.id.image);
			if (iv != null && iv.getVisibility() == View.VISIBLE) {
				if (!useLinphoneMessageStorage) {
					menu.add(id, MENU_SAVE_PICTURE, 0, getString(R.string.save_picture));
				}
				menu.add(id, MENU_SHOW_FILE, 0, getString(R.string.show_image));
			} else {
				menu.add(id, MENU_COPY_TEXT, 0, getString(R.string.copy_text));
			}

			if (msg != null && msg.getStatus() == LinphoneChatMessage.State.NotDelivered) {
				menu.add(id, MENU_RESEND_MESSAGE, 0, getString(R.string.retry));
			}
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_DELETE_MESSAGE:
			LinphoneActivity.instance().getChatStorage().deleteMessage(chatRoom, item.getGroupId());
			hideMessageBubble(item.getGroupId());
			break;
		case MENU_SAVE_PICTURE:
			saveImage(item.getGroupId());
			break;
		case MENU_SHOW_FILE:
			showFile(item.getGroupId());
			break;
		case MENU_COPY_TEXT:
			copyTextMessageToClipboard(item.getGroupId());
			break;
		case MENU_FILE_IMAGE:
			selectImageToSend();
			break;
		case MENU_FILE_AUDIO:
			selectAudioToSend();
			break;
		case MENU_FILE_VIDEO:
			selectVideoToSend();
			break;
		case MENU_RESEND_MESSAGE:
			resendMessage(item.getGroupId());
			break;
		}
		return true;
	}

	private void selectImageToSend() {
	    final List<Intent> cameraIntents = new ArrayList<Intent>();
	    final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    File file = new File(Environment.getExternalStorageDirectory(), getString(R.string.temp_photo_name));
	    imageToUploadUri = Uri.fromFile(file);
    	captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageToUploadUri);
	    cameraIntents.add(captureIntent);

	    final Intent galleryIntent = new Intent();
	    galleryIntent.setType("image/*");
	    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

	    final Intent chooserIntent = Intent.createChooser(galleryIntent, getString(R.string.image_picker_title));
	    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));

	    startActivityForResult(chooserIntent, ADD_PHOTO);
	}
	
	private void selectAudioToSend() {
	    final List<Intent> recordIntents = new ArrayList<Intent>();
	    final Intent recordIntent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
	    
    	recordIntent.putExtra(MediaStore.Audio.Media.EXTRA_MAX_BYTES, 1024*1024*4);
	    recordIntents.add(recordIntent);

	    final Intent audioIntent = new Intent();
	    audioIntent.setType("audio/*");
	    audioIntent.setAction(Intent.ACTION_GET_CONTENT);

	    final Intent chooserIntent = Intent.createChooser(audioIntent, getString(R.string.audio_picker_title));
	    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, recordIntents.toArray(new Parcelable[]{}));

	    startActivityForResult(chooserIntent, ADD_AUDIO);
	}
	
	private void selectVideoToSend() {
		
	}
	
	/**
	 * Hack to receive onPause to audioplayer components
	 * TODO: Implement a cleaner way to do this.
	 */
	List<OnPauseListener> pausables = new ArrayList<OnPauseListener>();
	public void registerOnPauseListener(OnPauseListener p) {
		pausables.add(p);
	}
	
	@Override
	public void onPause() {
		for(OnPauseListener p : pausables) {
			p.onPause();
		}
		pausables.clear();
		
		message.removeTextChangedListener(textWatcher);
		removeVirtualKeyboardVisiblityListener();
	
		LinphoneService.instance().removeMessageNotification();

		if (LinphoneManager.isInstanciated())
			LinphoneManager.getInstance().setOnComposingReceivedListener(null);

		super.onPause();
		
		onSaveInstanceState(getArguments());
	}

	@SuppressLint("UseSparseArrays")
	@Override
	public void onResume() {
		message.addTextChangedListener(textWatcher);
		addVirtualKeyboardVisiblityListener();

		if (LinphoneManager.isInstanciated())
			LinphoneManager.getInstance().setOnComposingReceivedListener(this);

		super.onResume();

		if (LinphoneActivity.isInstanciated()) {
			LinphoneActivity.instance().selectMenu(FragmentsAvailable.CHAT);
			LinphoneActivity.instance().updateChatFragment(this);

			if (getResources().getBoolean(R.bool.show_statusbar_only_on_dialer)) {
				LinphoneActivity.instance().hideStatusBar();
			}
		}

		String draft = getArguments().getString("messageDraft");
		message.setText(draft);

		remoteComposing.setVisibility(chatRoom.isRemoteComposing() ? View.VISIBLE : View.GONE);

		displayMessages();
	}

	@Override
	public void onClick(View v) {
		sendTextMessage();
	}

	private void displayMessages() {
		dispayMessageList();
	}

	private void sendTextMessage() {
		String txt = message.getText().toString().trim();
		sendTextMessage(txt);
		message.setText("");
	}

	private void sendTextMessage(String messageToSend) {
		LinphoneCore lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
		boolean isNetworkReachable = lc == null ? false : lc.isNetworkReachable();

		if (chatRoom != null && messageToSend != null && messageToSend.length() > 0 && isNetworkReachable) {
			LinphoneChatMessage chatMessage = chatRoom.createLinphoneChatMessage(messageToSend);
			chatRoom.sendMessage(chatMessage, this);

			if (LinphoneActivity.isInstanciated()) {
				LinphoneActivity.instance().onMessageSent(sipUri, messageToSend);	
			}

			adapter.refreshHistory(chatRoom);
			adapter.notifyDataSetChanged();
			
			Log.i("Sent message current status: " + chatMessage.getStatus());
			scrollToEnd();
		} else if (!isNetworkReachable && LinphoneActivity.isInstanciated()) {
			LinphoneActivity.instance().displayCustomToast(getString(R.string.error_network_unreachable), Toast.LENGTH_LONG);
		}
	}

	private void sendFileMessage(String url) {
		String msgContent = String.format("<file>%s</file>", url);
		sendTextMessage(msgContent);
	}
	
	private void sendImageMessage(String url, Bitmap bitmap) {
		LinphoneCore lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
		boolean isNetworkReachable = lc == null ? false : lc.isNetworkReachable();

		if (chatRoom != null && url != null && url.length() > 0 && isNetworkReachable) {
			String msgContent = String.format("<file>%s</file>", url);
			LinphoneChatMessage chatMessage = chatRoom.createLinphoneChatMessage(msgContent);
			chatRoom.sendMessage(chatMessage, this);

			int newId = -1;
			if (LinphoneActivity.isInstanciated()) {
				newId = LinphoneActivity.instance().onMessageSent(sipUri, bitmap, url);
			}
			newId = chatMessage.getStorageId();

			if (useLinphoneMessageStorage)
				url = saveImage(bitmap, newId, chatMessage);
			
			if(adapter != null) {
				adapter.refreshHistory(chatRoom);
				adapter.notifyDataSetChanged();
			}
			
			scrollToEnd();
		} else if (!isNetworkReachable && LinphoneActivity.isInstanciated()) {
			LinphoneActivity.instance().displayCustomToast(getString(R.string.error_network_unreachable), Toast.LENGTH_LONG);
		}
	}
	
	private LinphoneChatMessage getMessageForId(int id) {
		LinphoneChatMessage msg = null;
		try {
			msg = LinphoneActivity.instance().getChatStorage().getMessage(chatRoom, id);
		} catch (Exception e) {}

		if (msg == null) {
			for(LinphoneChatMessage m : chatRoom.getHistory()) {
				if(m.getStorageId() == id) {
					msg = m;
				}
			}
		}

		return msg;
	}

	private void hideMessageBubble(int id) {
		adapter.refreshHistory(chatRoom);
		adapter.notifyDataSetChanged();
	}

	private void resendMessage(int id) {
		LinphoneChatMessage message = getMessageForId(id);
		if (message == null)
			return;

		LinphoneActivity.instance().getChatStorage().deleteMessage(chatRoom, id);
		hideMessageBubble(id);

		if (message.getText() != null && message.getText().length() > 0) {
			sendTextMessage(message.getText());
		} else {
			sendImageMessage(message.getExternalBodyUrl(), null);
		}
	}

	private void scrollToEnd() {
		// USing Android transcript mode. See onCreate how.
		// messagesList.smoothScrollToPosition(messagesList.getCount());
		chatRoom.markAsRead();
	}

	private void copyTextMessageToClipboard(int id) {
		String msg = LinphoneActivity.instance().getChatStorage().getTextMessageForId(chatRoom, id);
		if (msg != null) {
			Compatibility.copyTextToClipboard(getActivity(), msg);
			LinphoneActivity.instance().displayCustomToast(getString(R.string.text_copied_to_clipboard), Toast.LENGTH_SHORT);
		}
	}

	public void onMessageReceived(final int id, LinphoneAddress from, final LinphoneChatMessage message) {
		if (from.asStringUriOnly().equals(sipUri))  {
			if (message.getText() != null) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						adapter.refreshHistory(chatRoom);
						adapter.notifyDataSetChanged();
					}
				});
			} else if (message.getExternalBodyUrl() != null) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						adapter.refreshHistory(chatRoom);
						adapter.notifyDataSetChanged();
					}
				});
			}
			scrollToEnd();
		}
	}

	@Override
	public synchronized void onLinphoneChatMessageStateChanged(LinphoneChatMessage msg, State state) {
		final LinphoneChatMessage finalMessage = msg;
		final State finalState=state;
		if (LinphoneActivity.isInstanciated() && state != State.InProgress) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					if (finalMessage != null && !finalMessage.equals("")) {
						LinphoneActivity.instance().onMessageStateChanged(sipUri, finalMessage.getText(), finalState.toInt());
					} 
					adapter.notifyDataSetChanged();
				}
				
			});
		}
	}

	public String getSipUri() {
		return sipUri;
	}


	public static Bitmap downloadImage(String stringUrl) {
		URL url;
		Bitmap bm = null;
		try {
			url = new URL(stringUrl);
			URLConnection ucon = url.openConnection();
	        InputStream is = ucon.getInputStream();
	        BufferedInputStream bis = new BufferedInputStream(is);

	        ByteArrayBuffer baf = new ByteArrayBuffer(50);
	        int current = 0;
	        while ((current = bis.read()) != -1) {
	                baf.append((byte) current);
	        }

	        byte[] rawImage = baf.toByteArray();
	        bm = BitmapFactory.decodeByteArray(rawImage, 0, rawImage.length);
			bis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bm;
	}

	private void saveImage(int id) {
		byte[] rawImage = LinphoneActivity.instance().getChatStorage().getRawImageFromMessage(id);
		Bitmap bm = BitmapFactory.decodeByteArray(rawImage, 0, rawImage.length);
		if (saveImage(bm, id, null) != null) {
			Toast.makeText(getActivity(), getString(R.string.image_saved), Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getActivity(), getString(R.string.image_not_saved), Toast.LENGTH_LONG).show();
		}
	}

	private void showFile(int id) {
		LinphoneChatMessage msg = adapter.getItemForId(id).getMessage();
		File file = null;
		String path = msg.getExternalBodyUrl();

		if(path == null) {
			return;
		}

		if(path.startsWith("http")) {
			try {
				URL url = new URL(path);
				FilesharingCache cache = FilesharingCache.instance(getActivity());
				if(cache.isCached(url)) {
					file = cache.getFileForUrl(url);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		} 
		else {
			file = new File(path);
		}
		if(file == null) return;
		
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.fromFile(file), FilesharingCache.getGeneralMimeTypeForFile(file));
		startActivity(i);
	}
	
	private String saveImage(Bitmap bm, int id, LinphoneChatMessage message) {
		try {
			String path = Environment.getExternalStorageDirectory().toString();
			if (!path.endsWith("/"))
				path += "/";
			path += "Pictures/";
			File directory = new File(path);
			directory.mkdirs();

			String filename = getString(R.string.picture_name_format).replace("%s", String.valueOf(id));
			File file = new File(path, filename);

			OutputStream fOut = null;
			fOut = new FileOutputStream(file);

			bm.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
			fOut.flush();
			fOut.close();

			if (useLinphoneMessageStorage) {
				//Update url path in liblinphone database
				if (message == null) {
					LinphoneChatMessage[] history = chatRoom.getHistory();
					for (LinphoneChatMessage msg : history) {
						if (msg.getStorageId() == id) {
							message = msg;
							break;
						}
					}
				}
				message.setExternalBodyUrl(path + filename);
				chatRoom.updateUrl(message);
			}

			MediaStore.Images.Media.insertImage(getActivity().getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());
			return file.getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getRealPathFromURI(Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
	    CursorLoader loader = new CursorLoader(getActivity(), contentUri, proj, null, null, null);
	    Cursor cursor = loader.loadInBackground();
	    if (cursor != null && cursor.moveToFirst()) {
		    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		    String result = cursor.getString(column_index);
		    cursor.close();
		    return result;
	    }
	    return null;
    }

	private void skipPopupMenuAskingImageSize(final String filePath, final Bitmap image) {
		fileToUploadPath = filePath;
		imageToUpload = image;
		uploadAndSendImage(fileToUploadPath, imageToUpload, ImageSize.LARGE);
	}
	
	private void uploadAndSendImage(final String filePath, final Bitmap image, final ImageSize size) {
		uploadLayout.setVisibility(View.VISIBLE);
    	textLayout.setVisibility(View.GONE);

		Bitmap bm = null;

		if (filePath != null) {
            bm = BitmapFactory.decodeFile(filePath);
            if (bm != null && size != ImageSize.REAL) {
            	int pixelsMax = size == ImageSize.SMALL ? SIZE_SMALL : size == ImageSize.MEDIUM ? SIZE_MEDIUM : SIZE_LARGE;
                if (bm.getWidth() > bm.getHeight() && bm.getWidth() > pixelsMax) {
                	bm = Bitmap.createScaledBitmap(bm, pixelsMax, (pixelsMax * bm.getHeight()) / bm.getWidth(), false);
                } else if (bm.getHeight() > bm.getWidth() && bm.getHeight() > pixelsMax) {
                	bm = Bitmap.createScaledBitmap(bm, (pixelsMax * bm.getWidth()) / bm.getHeight(), pixelsMax, false);
                }
            }
		} else if (image != null) {
			bm = image;
		}

		try {
			if (filePath != null) {
				ExifInterface exif = new ExifInterface(filePath);
				int pictureOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
				Matrix matrix = new Matrix();
				if (pictureOrientation == 6) {
					matrix.postRotate(90);
				} else if (pictureOrientation == 3) {
					matrix.postRotate(180);
				} else if (pictureOrientation == 8) {
					matrix.postRotate(270);
				}
				bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		final Bitmap tempBmToSave = bm;
		
        UploadTaskCallback uploadCallback = new UploadTaskCallback() {
			
			@Override
			public void success(URL url) {
				uploadLayout.setVisibility(View.GONE);
				textLayout.setVisibility(View.VISIBLE);
				if(url == null || tempBmToSave == null) return;
					sendFileMessage(url.toExternalForm());
			}
			
			@Override
			public void fail(String reason) {
				uploadLayout.setVisibility(View.GONE);
				textLayout.setVisibility(View.VISIBLE);
        		Toast.makeText(getActivity(), reason, Toast.LENGTH_LONG).show();
			}
		};

		if (bm != null) {
        	try {
        		File f = new File(filePath);
            	File compressed = new File(getActivity().getCacheDir().getAbsolutePath() + "/" + f.getName());
            	FileOutputStream out = new FileOutputStream(compressed); 
                if (bm != null) {
                	bm.compress(CompressFormat.JPEG, COMPRESSOR_QUALITY, out);
                }
                out.flush();
                out.close();
                FilesharingUploadTask task = new FilesharingUploadTask(getActivity(), uploadCallback, progressBar);
                task.execute(compressed);
        	}
        	catch(IOException e) {
        		e.printStackTrace();
        	}
        }
	}
	
	private void uploadAndSendAudioMessage(String filePath) {
		uploadLayout.setVisibility(View.VISIBLE);
    	textLayout.setVisibility(View.GONE);

        UploadTaskCallback uploadCallback = new UploadTaskCallback() {
			
			@Override
			public void success(URL url) {
				uploadLayout.setVisibility(View.GONE);
				textLayout.setVisibility(View.VISIBLE);
				if(url == null ) return;
				sendFileMessage(url.toExternalForm());
			}
			
			@Override
			public void fail(String reason) {
				uploadLayout.setVisibility(View.GONE);
				textLayout.setVisibility(View.VISIBLE);
        		Toast.makeText(getActivity(), reason, Toast.LENGTH_LONG).show();
			}
		};

		FilesharingUploadTask task = new FilesharingUploadTask(getActivity(), uploadCallback, progressBar);
		task.execute(new File(filePath));
	}

	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
        	if(requestCode == ADD_PHOTO) {
        		addPhotoReceived(data);
        	}
        	else if(requestCode == ADD_AUDIO) {
        		addAudioReceived(data);
        	}
        	else if(requestCode == ADD_VIDEO) {
        		addVideoReceived(data);
        	}
		}
       else {
			super.onActivityResult(requestCode, resultCode, data);
		}
    }

	private void addPhotoReceived(Intent data) {
    	if (data != null && data.getExtras() != null && data.getExtras().get("data") != null) {
    		Bitmap bm = (Bitmap) data.getExtras().get("data");
    		skipPopupMenuAskingImageSize(null, bm);
    	}
    	else if (data != null && data.getData() != null) {
    		String filePath = getRealPathFromURI(data.getData());
        	skipPopupMenuAskingImageSize(filePath, null);
    	}
    	else if (imageToUploadUri != null) {
    		String filePath = imageToUploadUri.getPath();
    		skipPopupMenuAskingImageSize(filePath, null);
    	}
    	else {
    		File file = new File(Environment.getExternalStorageDirectory(), getString(R.string.temp_photo_name));
    		if (file.exists()) {
        	    imageToUploadUri = Uri.fromFile(file);
        	    String filePath = imageToUploadUri.getPath();
        		skipPopupMenuAskingImageSize(filePath, null);
    		}
    	}		
	}
	

	private void addAudioReceived(Intent data) {
		if (data != null && data.getData() != null) {
    		String filePath = getRealPathFromURI(data.getData());
        	uploadAndSendAudioMessage(filePath);
    	}
	}

	private void addVideoReceived(Intent data) {
	}

	class ProgressOutputStream extends OutputStream {
		OutputStream outputStream;
		private OutputStreamListener listener;

		public ProgressOutputStream(OutputStream stream) {
			outputStream = stream;
		}

		public void setListener(OutputStreamListener listener) {
			this.listener = listener;
		}

		@Override
		public void write(int oneByte) throws IOException {
			outputStream.write(oneByte);
		}

		@Override
		public void write(byte[] buffer, int offset, int count)
				throws IOException {
			listener.onBytesWrite(count);
			outputStream.write(buffer, offset, count);
		}
	}

	interface OutputStreamListener {
		public void onBytesWrite(int count);
	}

	enum ImageSize {
		SMALL,
		MEDIUM,
		LARGE,
		REAL;
	}

	@Override
	public void onComposingReceived(LinphoneChatRoom room) {
		if (chatRoom != null && room != null && chatRoom.getPeerAddress().asStringUriOnly().equals(room.getPeerAddress().asStringUriOnly())) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					remoteComposing.setVisibility(chatRoom.isRemoteComposing() ? View.VISIBLE : View.GONE);
				}
			});
		}
	}
	

	private void selectSmiley() {
		SmiliesDialogFragment dlg = new SmiliesDialogFragment(this);
		dlg.show(getFragmentManager(), "SmiliesDialog");
	}

	@Override
	public void insertSmiley(String txt) {
		message.append(" " + txt + " ");		
	}
	
    OnClickListener onCall = new OnClickListener() {
		@Override
		public void onClick(View v) {
			LinphoneManager.getInstance().newOutgoingCall(sipUri, FormattingHelp.stripDomainFromAddress(sipUri));
		}
	};
	
    OnItemClickListener onMessageClick = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			FileSharingBubbleChat item = (FileSharingBubbleChat) view;
			if(item.findViewById(R.id.image).getVisibility() == View.VISIBLE) {
				showFile(item.getData().getMessage().getStorageId());
			}
		}
	};
}
