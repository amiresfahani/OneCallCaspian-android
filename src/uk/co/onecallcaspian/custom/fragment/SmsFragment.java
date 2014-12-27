package uk.co.onecallcaspian.custom.fragment;
/*
ChatListFragment.java
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
import uk.co.onecallcaspian.R;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SmsFragment extends Fragment {
	private LayoutInflater mInflater;
	private EditText mSmsText, mSmsTo;
	private Button mSmsSend;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mInflater = inflater;
		View view = mInflater.inflate(R.layout.sms, container, false);
		mSmsText = (EditText) view.findViewById(R.id.sms_text);
		mSmsTo = (EditText) view.findViewById(R.id.sms_to);
		mSmsSend = (Button) view.findViewById(R.id.sms_send);
		
		mSmsSend.setOnClickListener(onSmsSend);
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		String phoneNumber = getArguments().getString("phone_number");
		mSmsTo.setText(phoneNumber);
	}

	OnClickListener onSmsSend = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Toast.makeText(getActivity(), "SMS Not implemented yet.", Toast.LENGTH_LONG)
			.show();
		}
	};
}


