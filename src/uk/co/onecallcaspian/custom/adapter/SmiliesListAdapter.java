/*
SmiliesAdapter.java
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
package uk.co.onecallcaspian.custom.adapter;

import uk.co.onecallcaspian.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class SmiliesListAdapter extends BaseAdapter {

	public SmiliesListAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return SmiliesManager.instance().getCount();
	}

	@Override
	public Object getItem(int position) {
		return SmiliesManager.instance().getSmilie(position, true);
	}

	@Override
	public long getItemId(int position) {
		return -1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		ViewGroup v = (ViewGroup) convertView;
		if(v == null) {
			v = (ViewGroup) inflater.inflate(R.layout.list_item_smilie, parent, false);
		}
		ImageView btn = (ImageView) v.findViewById(R.id.img_smilie);
		SmiliesListItem si = SmiliesManager.instance().getSmilie(position, true);
		btn.setImageResource(si.getImageResource());
		return v;
	}
	
	Context context;
}
