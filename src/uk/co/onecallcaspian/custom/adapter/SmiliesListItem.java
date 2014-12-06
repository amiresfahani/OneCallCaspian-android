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
