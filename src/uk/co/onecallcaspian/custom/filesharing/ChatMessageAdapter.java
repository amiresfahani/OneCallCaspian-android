/*
ChatMessageAdapter.java
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

import org.linphone.core.LinphoneChatMessage;
import org.linphone.core.LinphoneChatRoom;

import uk.co.onecallcaspian.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * @author fizzl
 * \brief Load and show chat messages in a chat room
 */
public class ChatMessageAdapter extends BaseAdapter {
	public ChatMessageAdapter(Context context) {
		this.context = context;
	}
	
	public LinphoneChatMessage getItemForId(int id) {
		for(LinphoneChatMessage msg : history) {
			if(msg.getStorageId() == id) {
				return msg;
			}
		}
		return null;
	}
	
	@Override
	public int getCount() {
		return history.length;
	}

	@Override
	public Object getItem(int position) {
		return history[position];
	}

	@Override
	public long getItemId(int position) {
		return history[position].getStorageId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		FileSharingBubbleChat bubble = (FileSharingBubbleChat) convertView;
		if(bubble == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
			bubble = (FileSharingBubbleChat) inflater.inflate(R.layout.filesharing_chat_bubble, parent, false);
		}
		bubble.setData(history[position]);
		return bubble;
	}
	
	public void refreshHistory(LinphoneChatRoom room){
		this.history = room.getHistory();
	}
	
	private LinphoneChatMessage[] history = new LinphoneChatMessage[0];
	private Context context;
}
