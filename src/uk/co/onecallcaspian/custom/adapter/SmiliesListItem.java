/*
SmiliesListItem.java
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

import java.util.ArrayList;
import java.util.List;

public class SmiliesListItem {
	private String primaryTrigger;
	private List<String> triggers;
	private int imageResource;
	
	public SmiliesListItem() {
		triggers = new ArrayList<String>();
		primaryTrigger = "";
		imageResource = -1;
	}
	
	public SmiliesListItem(String primaryTrigger, int resourceId, String...additionalTriggers) {
		this();
		setPrimaryTrigger(primaryTrigger);
		setImageResource(resourceId);
		for(String t : additionalTriggers) {
			addTrigger(t);
		}
	}
	
	public String getPrimaryTrigger() {
		return primaryTrigger;
	}
	public void setPrimaryTrigger(String primaryTrigger) {
		this.primaryTrigger = primaryTrigger;
		addTrigger(primaryTrigger);
	}
	public List<String> getTriggers() {
		return triggers;
	}
	public void setTriggers(List<String> triggers) {
		this.triggers = triggers;
	}
	public int getImageResource() {
		return imageResource;
	}
	public void setImageResource(int imageResource) {
		this.imageResource = imageResource;
	}
	public void addTrigger(String newTrigger) {
		if(!triggers.contains(newTrigger)) {
			triggers.add(newTrigger);
		}
	}
	
	// Helper methods
	public boolean hasTrigger(String what) {
		return triggers.contains(what);
	}
}
