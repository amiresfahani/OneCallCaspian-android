package uk.co.onecallcaspian.custom.adapter;

import java.util.ArrayList;
import java.util.List;

public class SmiliesManager {
	private SmiliesManager() {
		smilies = new ArrayList<SmiliesListItem>();
		
		smilies.add(new SmiliesListItem()); 
	}
	
	private List<SmiliesListItem> smilies;
}
