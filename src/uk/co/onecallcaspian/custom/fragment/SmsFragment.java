/*
SmsFragment.java
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
package uk.co.onecallcaspian.custom.fragment;
import uk.co.onecallcaspian.LinphoneActivity;
import uk.co.onecallcaspian.LinphonePreferences;
import uk.co.onecallcaspian.R;
import uk.co.onecallcaspian.custom.adapter.SmsHistoryListItem;
import uk.co.onecallcaspian.custom.adapter.SmsListAdapter;
import uk.co.onecallcaspian.custom.adapter.SmsListItem;
import uk.co.onecallcaspian.custom.database.SmsDb;
import uk.co.onecallcaspian.custom.dialog.ActivateSmsDialog;
import uk.co.onecallcaspian.custom.dialog.VerifySmsDialog;
import uk.co.onecallcaspian.custom.rest.SmsHandler;
import uk.co.onecallcaspian.custom.rest.SmsRequestHandlerCallback;
import android.app.Fragment;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class SmsFragment extends Fragment {
	private LayoutInflater mInflater;
	private TextView mSmsText, mSmsTo;
	private ListView mSmsList;
	private SmsListAdapter mSmsAdapter;
	private TextView mSmsSend;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mInflater = inflater;
		View view = mInflater.inflate(R.layout.sms, container, false);
		mSmsText = (EditText) view.findViewById(R.id.sms_text);
		mSmsTo = (TextView) view.findViewById(R.id.sms_to);
		mSmsSend = (TextView) view.findViewById(R.id.sms_send);
		mSmsList = (ListView) view.findViewById(R.id.sms_list);
		registerForContextMenu(mSmsList);
		mSmsList.setOnItemClickListener(onItemClick);
		mSmsList.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
		mSmsList.setStackFromBottom(true);
        
		mSmsSend.setOnClickListener(onSmsSend);
		
		return view;
	}
	
	OnItemClickListener onItemClick = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			SmsListItem item = (SmsListItem) view;
			item.retry();			
		}
	};

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, v.getId(), 0, getString(R.string.delete));
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		if (info == null || info.targetView == null) {
			return false;
		}
		SmsListItem listItem =  (SmsListItem) info.targetView;
		listItem.delete();
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		String phoneNumber = getArguments().getString("phone_number");
		mSmsTo.setText(phoneNumber);
		mSmsAdapter = new SmsListAdapter(getActivity(), phoneNumber);
		mSmsList.setAdapter(mSmsAdapter);
	}

	OnClickListener onSmsSend = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// Show immediate status
			String to = mSmsTo.getText().toString();
			String content = mSmsText.getText().toString();
			long delivered = -1;
			long id = SmsDb.instance(getActivity()).insert(to, delivered, content);
			mSmsAdapter.updateCursor();
			mSmsText.setText("");
			
			// Try sending in background
			SmsRequestHandlerCallback cb = new SmsRequestHandlerCallback(getActivity(), id);
			SmsHandler sms = new SmsHandler(cb);
			LinphonePreferences prefs = LinphonePreferences.instance();

			String number = prefs.getAccountUsername(prefs.getDefaultAccountIndex());
			String password = prefs.getAccountPassword(prefs.getDefaultAccountIndex());
			
			sms.setMyPhoneNumber(number);
			sms.setPassword(password);
			sms.setTo(to);
			sms.setText(content);
			
			sms.execute();
		}
	};
}


