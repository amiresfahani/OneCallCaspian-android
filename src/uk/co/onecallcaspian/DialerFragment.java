package uk.co.onecallcaspian;
/*
DialerFragment.java
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
import org.linphone.core.LinphoneCore;
import org.linphone.mediastream.Log;

import uk.co.onecallcaspian.ui.AddressAware;
import uk.co.onecallcaspian.ui.AddressText;
import uk.co.onecallcaspian.ui.CallButton;
import uk.co.onecallcaspian.ui.EraseButton;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import uk.co.onecallcaspian.R;

/**
 * @author Sylvain Berfini
 */
public class DialerFragment extends Fragment {
	private static DialerFragment instance;
	private static boolean isCallTransferOngoing = false;
	
	public boolean mVisible;
	private Handler mHandler = new Handler();
	private AddressText mAddress;
	private CallButton mCall;
	private ImageView mAddContact;
	private ImageButton chat;
	private TextView missedChats;
	private OnClickListener addContactListener, cancelListener, transferListener;
	private boolean shouldEmptyAddressField = true;
	View rootView;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {
		if(rootView != null) {
			return rootView;
		}
		instance = this;
        rootView = inflater.inflate(R.layout.dialer, container, false);
		
		mAddress = (AddressText) rootView.findViewById(R.id.Adress); 
		mAddress.setDialerFragment(this);
		
		EraseButton erase = (EraseButton) rootView.findViewById(R.id.Erase);
		erase.setAddressWidget(mAddress);
		
		mCall = (CallButton) rootView.findViewById(R.id.Call);
		mCall.setAddressWidget(mAddress);
		if (LinphoneActivity.isInstanciated() && LinphoneManager.getLc().getCallsNb() > 0) {
			if (isCallTransferOngoing) {
				mCall.setImageResource(R.drawable.transfer_call);
			} else {
				mCall.setImageResource(R.drawable.add_call);
			}
		} else {
			mCall.setImageResource(R.drawable.call);
		}
		
		AddressAware numpad = (AddressAware) rootView.findViewById(R.id.Dialer);
		if (numpad != null) {
			numpad.setAddressWidget(mAddress);
		}
		
		chat = (ImageButton) rootView.findViewById(R.id.Chat);
		chat.setOnClickListener(onChatClick);

		missedChats = (TextView) rootView.findViewById(R.id.missedChats);

		mAddContact = (ImageView) rootView.findViewById(R.id.addContact);
		
		addContactListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				// LinphoneActivity.instance().displayContactsForEdition(mAddress.getText().toString());
				LinphoneActivity.instance().addContact(null, mAddress.getText().toString());
			}
		};
		cancelListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				LinphoneActivity.instance().resetClassicMenuLayoutAndGoBackToCallIfStillRunning();
			}
		};
		transferListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				LinphoneCore lc = LinphoneManager.getLc();
				if (lc.getCurrentCall() == null) {
					return;
				}
				lc.transferCall(lc.getCurrentCall(), mAddress.getText().toString());
				isCallTransferOngoing = false;
				LinphoneActivity.instance().resetClassicMenuLayoutAndGoBackToCallIfStillRunning();
			}
		};
		
		mAddContact.setEnabled(!(LinphoneActivity.isInstanciated() && LinphoneManager.getLc().getCallsNb() > 0));
		resetLayout(isCallTransferOngoing);
		
		if (getArguments() != null) {
			shouldEmptyAddressField = false;
			String number = getArguments().getString("SipUri");
			String displayName = getArguments().getString("DisplayName");
			String photo = getArguments().getString("PhotoUri");
			mAddress.setText(number);
			if (displayName != null) {
				mAddress.setDisplayedName(displayName);
			}
			if (photo != null) {
				mAddress.setPictureUri(Uri.parse(photo));
			}
		}
				
		return rootView;
    }
	
	/**
	 * @return null if not ready yet
	 */
	public static DialerFragment instance() { 
		return instance;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		if (LinphoneActivity.isInstanciated()) {
			LinphoneActivity.instance().selectMenu(FragmentsAvailable.DIALER);
			LinphoneActivity.instance().updateDialerFragment(this);
			LinphoneActivity.instance().showStatusBar();
		}
		
		if (shouldEmptyAddressField) {
			mAddress.setText("");
		} else {
			shouldEmptyAddressField = true;
		}
		resetLayout(isCallTransferOngoing);
	}
	
	public void resetLayout(boolean callTransfer) {
		isCallTransferOngoing = callTransfer;
		LinphoneCore lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
		if (lc == null) {
			return;
		}
		
		if (lc.getCallsNb() > 0) {
			if (isCallTransferOngoing) {
				mCall.setImageResource(R.drawable.transfer_call);
				mCall.setExternalClickListener(transferListener);
			} else {
				mCall.setImageResource(R.drawable.add_call);
				mCall.resetClickListener();
			}
			mAddContact.setEnabled(true);
			mAddContact.setImageResource(R.drawable.cancel);
			mAddContact.setOnClickListener(cancelListener);
			//chat.setEnabled(false);
		} else {
			mCall.setImageResource(R.drawable.call);
			mAddContact.setEnabled(true);
			mAddContact.setImageResource(R.drawable.add_contact);
			mAddContact.setOnClickListener(addContactListener);
			//chat.setEnabled(true);
			enableDisableAddContact();
		}
	}
	
	public void enableDisableAddContact() {
		mAddContact.setEnabled(LinphoneManager.getLc().getCallsNb() > 0 || !mAddress.getText().toString().equals(""));	
	}
	
	public void displayTextInAddressBar(String numberOrSipAddress) {
		shouldEmptyAddressField = false;
		mAddress.setText(numberOrSipAddress);
	}
	
	public void newOutgoingCall(String numberOrSipAddress) {
		displayTextInAddressBar(numberOrSipAddress);
		LinphoneManager.getInstance().newOutgoingCall(mAddress);
	}
	
	public void newOutgoingCall(Intent intent) {
		if (intent != null && intent.getData() != null) {
			String scheme = intent.getData().getScheme();
			if (scheme.startsWith("imto")) {
				mAddress.setText("sip:" + intent.getData().getLastPathSegment());
			} else if (scheme.startsWith("call") || scheme.startsWith("sip")) {
				mAddress.setText(intent.getData().getSchemeSpecificPart());
			} else {
				Log.e("Unknown scheme: ",scheme);
				mAddress.setText(intent.getData().getSchemeSpecificPart());
			}
	
			mAddress.clearDisplayedName();
			intent.setData(null);
	
			LinphoneManager.getInstance().newOutgoingCall(mAddress);
		}
	}
	
	OnClickListener onChatClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if(LinphoneActivity.isInstanciated()) {
				LinphoneActivity.instance()
				.changeCurrentFragment(FragmentsAvailable.CHATLIST, null);
			}
		}
	};
	
	protected void displayMissedChats(final int missedChatCount) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if (missedChatCount > 0) {
					missedChats.setText(missedChatCount + "");
					if (missedChatCount > 99) {
						missedChats.setTextSize(12);
					} else {
						missedChats.setTextSize(20);
					}
					missedChats.setVisibility(View.VISIBLE);
					missedChats.startAnimation(AnimationUtils.loadAnimation(LinphoneActivity.instance(), R.anim.bounce));
				} else {
					missedChats.clearAnimation();
					missedChats.setVisibility(View.GONE);
				}
			}
		});
	}
}