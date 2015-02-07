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
import uk.co.onecallcaspian.R;
import uk.co.onecallcaspian.custom.adapter.SmsHistoryListAdapter;
import uk.co.onecallcaspian.custom.adapter.SmsHistoryListItem;
import uk.co.onecallcaspian.custom.database.DbUtils;
import uk.co.onecallcaspian.custom.database.SmsDb;
import android.app.Fragment;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class SmsHistoryFragment extends Fragment {	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup root,
			Bundle savedInstanceState) {
		View ret = inflater.inflate(R.layout.sms_history, root, false);
		ListView lv = (ListView) ret.findViewById(R.id.sms_history_list);
		registerForContextMenu(lv);
		lv.setOnItemClickListener(onItemClick);
		adapter = new SmsHistoryListAdapter(getActivity());
		lv.setAdapter(adapter);
		return ret;
	}

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
		SmsHistoryListItem historyItem = (SmsHistoryListItem) info.targetView;
		String to = historyItem.getFrom();
		
		SmsDb.instance(getActivity()).deleteTo(to);
		adapter.changeCursor(SmsDb.instance(getActivity()).getHistoryListCursor());
		
		return true;
	}
	
	OnItemClickListener onItemClick = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			SmsHistoryListItem item = (SmsHistoryListItem) view;
			String from = item.getFrom();
			LinphoneActivity.instance().displaySms(from);
		}
	};
	
	private SmsHistoryListAdapter adapter;
}


