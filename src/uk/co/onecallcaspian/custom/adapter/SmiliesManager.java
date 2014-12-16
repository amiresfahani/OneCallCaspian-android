package uk.co.onecallcaspian.custom.adapter;

import java.util.ArrayList;
import java.util.List;

import uk.co.onecallcaspian.LinphonePreferences;
import uk.co.onecallcaspian.R;


public class SmiliesManager {
	// Singleton
	private static SmiliesManager me;
	public static SmiliesManager instance() {
		if(me == null) 
			me = new SmiliesManager();
		return me;
	}
	
	// Get Nth smilie
	public SmiliesListItem getSmilie(int which, boolean useSticker) {
		return getList(useSticker).get(which);
	}
	public SmiliesListItem getSmilie(int which) {
		return getSmilie(which, false);
	}
	
	// Get actual smilie resource ID
	public SmiliesListItem getSmilieForTrigger(String what, boolean useSticker) {
		for(SmiliesListItem item : getList(useSticker)) {
			if(item.hasTrigger(what)) {
				return item;
			}
		}
		return null;
	}

	public SmiliesListItem getSmilieForTrigger(String what) {
		return getSmilieForTrigger(what, false);
	}

	public List<SmiliesListItem> getList(boolean useStickers) {
		if(!useStickers) {
			return getList();
		}
		return stickers;
	}
	// Current list to use
	public List<SmiliesListItem> getList() {
		List<SmiliesListItem> useList = smallSmilies;
		LinphonePreferences prefs = LinphonePreferences.instance();
		if(prefs.isBigSmilies()) {
			useList = bigSmilies;
		}
		return useList;
	}
	
	public int getCount() {
		return getList().size();
	}
	
	// Construction. Add smilies to map.
	private SmiliesManager() {
		smallSmilies = new ArrayList<SmiliesListItem>();
		bigSmilies = new ArrayList<SmiliesListItem>();
		stickers = new ArrayList<SmiliesListItem>();

		smallSmilies.add(new SmiliesListItem("O:)",			R.drawable.smilie_64_angel, 
				":angel:", "O:-)")); 
		smallSmilies.add(new SmiliesListItem(":(", 			R.drawable.smilie_64_angry, 
				":angry:", ":-(", ":[", ":-[")); 
		smallSmilies.add(new SmiliesListItem(":-/", 		R.drawable.smilie_64_ashamed, 
				":ashamed:", ":/", ":|", ":-|")); 
		smallSmilies.add(new SmiliesListItem("O~", 			R.drawable.smilie_64_balloons, 
				":balloons:")); 
		smallSmilies.add(new SmiliesListItem(":$)", 		R.drawable.smilie_64_blush, 
				":blush:")); 
		smallSmilies.add(new SmiliesListItem("(||)", 		R.drawable.smilie_64_burger, 
				":burger:")); 
		smallSmilies.add(new SmiliesListItem("<|", 			R.drawable.smilie_64_cake, 
				":cake:")); 
		smallSmilies.add(new SmiliesListItem(":coffee:",	R.drawable.smilie_64_coffee, 
				":coffee:")); 
		smallSmilies.add(new SmiliesListItem("@}-;-", 		R.drawable.smilie_64_flower, 
				":flower:")); 
		smallSmilies.add(new SmiliesListItem("¤", 			R.drawable.smilie_64_football, 
				":football:")); 
		smallSmilies.add(new SmiliesListItem("(..)", 		R.drawable.smilie_64_glasses, 
				":glasses:")); 
		smallSmilies.add(new SmiliesListItem(":o)", 		R.drawable.smilie_64_goofy, 
				":goofy:")); 
		smallSmilies.add(new SmiliesListItem(":D", 			R.drawable.smilie_64_grin, 
				":grin:", ":-D", "XD", "X-D")); 
		smallSmilies.add(new SmiliesListItem(":)", 			R.drawable.smilie_64_happy, 
				":happy:", ":-)")); 
		smallSmilies.add(new SmiliesListItem("<3", 			R.drawable.smilie_64_heart, 
				":heart:")); 
		smallSmilies.add(new SmiliesListItem(":drink:", 	R.drawable.smilie_64_icetea, 
				":drink:")); 
		smallSmilies.add(new SmiliesListItem(":*", 			R.drawable.smilie_64_kiss, 
				":kiss:", ":-*")); 
		smallSmilies.add(new SmiliesListItem(":kisses:", 	R.drawable.smilie_64_kisses, 
				":kisses:")); 
		smallSmilies.add(new SmiliesListItem("^o|", 		R.drawable.smilie_64_penguin, 
				":penguin:")); 
		smallSmilies.add(new SmiliesListItem(":present:", 	R.drawable.smilie_64_present, 
				":present:")); 
		smallSmilies.add(new SmiliesListItem(":O", 			R.drawable.smilie_64_sleepy, 
				":sleepy:")); 
		smallSmilies.add(new SmiliesListItem(":stopclock:", R.drawable.smilie_64_stopclock, 
				":stopclock:")); 
		smallSmilies.add(new SmiliesListItem(":sun:", 		R.drawable.smilie_64_sun, 
				":sun:")); 
		smallSmilies.add(new SmiliesListItem("O_o", 		R.drawable.smilie_64_surprised, 
				":surprised:")); 
		smallSmilies.add(new SmiliesListItem(":whistling:", R.drawable.smilie_64_whistling, 
				":whistling:")); 
		
		bigSmilies.add(new SmiliesListItem("O:)",			R.drawable.smilie_128_angel, 
				":angel:", "O:-)")); 
		bigSmilies.add(new SmiliesListItem(":(", 			R.drawable.smilie_128_angry, 
				":angry:", ":-(", ":[", ":-[")); 
		bigSmilies.add(new SmiliesListItem(":-/", 			R.drawable.smilie_128_ashamed, 
				":ashamed:", ":/", ":|", ":-|")); 
		bigSmilies.add(new SmiliesListItem("O~", 			R.drawable.smilie_128_balloons, 
				":balloons:")); 
		bigSmilies.add(new SmiliesListItem(":$)", 			R.drawable.smilie_128_blush, 
				":blush:")); 
		bigSmilies.add(new SmiliesListItem("(||)", 			R.drawable.smilie_128_burger, 
				":burger:")); 
		bigSmilies.add(new SmiliesListItem("<|", 			R.drawable.smilie_128_cake, 
				":cake:")); 
		bigSmilies.add(new SmiliesListItem(":coffee:",		R.drawable.smilie_128_coffee, 
				":coffee:")); 
		bigSmilies.add(new SmiliesListItem("@}-;-", 		R.drawable.smilie_128_flower, 
				":flower:")); 
		bigSmilies.add(new SmiliesListItem("¤", 			R.drawable.smilie_128_football, 
				":football:")); 
		bigSmilies.add(new SmiliesListItem("(..)", 			R.drawable.smilie_128_glasses, 
				":glasses:")); 
		bigSmilies.add(new SmiliesListItem(":o)", 			R.drawable.smilie_128_goofy, 
				":goofy:")); 
		bigSmilies.add(new SmiliesListItem(":D", 			R.drawable.smilie_128_grin, 
				":grin:", ":-D", "XD", "X-D")); 
		bigSmilies.add(new SmiliesListItem(":)", 			R.drawable.smilie_128_happy, 
				":happy:", ":-)")); 
		bigSmilies.add(new SmiliesListItem("<3", 			R.drawable.smilie_128_heart, 
				":heart:")); 
		bigSmilies.add(new SmiliesListItem(":drink:", 		R.drawable.smilie_128_icetea, 
				":drink:")); 
		bigSmilies.add(new SmiliesListItem(":*", 			R.drawable.smilie_128_kiss, 
				":kiss:", ":-*")); 
		bigSmilies.add(new SmiliesListItem(":kisses:", 		R.drawable.smilie_128_kisses, 
				":kisses:")); 
		bigSmilies.add(new SmiliesListItem("^o|", 			R.drawable.smilie_128_penguin, 
				":penguin:")); 
		bigSmilies.add(new SmiliesListItem(":present:", 	R.drawable.smilie_128_present, 
				":present:")); 
		bigSmilies.add(new SmiliesListItem(":O", 			R.drawable.smilie_128_sleepy, 
				":sleepy:")); 
		bigSmilies.add(new SmiliesListItem(":stopclock:", 	R.drawable.smilie_128_stopclock, 
				":stopclock:")); 
		bigSmilies.add(new SmiliesListItem(":sun:", 		R.drawable.smilie_128_sun, 
				":sun:")); 
		bigSmilies.add(new SmiliesListItem("O_o", 			R.drawable.smilie_128_surprised, 
				":surprised:")); 
		bigSmilies.add(new SmiliesListItem(":whistling:", 	R.drawable.smilie_128_whistling, 
				":whistling:")); 
		
		
		stickers.add(new SmiliesListItem("O:)",			R.drawable.smilie_256_angel, 
				":angel:", "O:-)")); 
		stickers.add(new SmiliesListItem(":(", 			R.drawable.smilie_256_angry, 
				":angry:", ":-(", ":[", ":-[")); 
		stickers.add(new SmiliesListItem(":-/", 		R.drawable.smilie_256_ashamed, 
				":ashamed:", ":/", ":|", ":-|")); 
		stickers.add(new SmiliesListItem("O~", 			R.drawable.smilie_256_balloons, 
				":balloons:")); 
		stickers.add(new SmiliesListItem(":$)", 		R.drawable.smilie_256_blush, 
				":blush:")); 
		stickers.add(new SmiliesListItem("(||)", 		R.drawable.smilie_256_burger, 
				":burger:")); 
		stickers.add(new SmiliesListItem("<|", 			R.drawable.smilie_256_cake, 
				":cake:")); 
		stickers.add(new SmiliesListItem(":coffee:",	R.drawable.smilie_256_coffee, 
				":coffee:")); 
		stickers.add(new SmiliesListItem("@}-;-", 		R.drawable.smilie_256_flower, 
				":flower:")); 
		stickers.add(new SmiliesListItem("¤", 			R.drawable.smilie_256_football, 
				":football:")); 
		stickers.add(new SmiliesListItem("(..)", 		R.drawable.smilie_256_glasses, 
				":glasses:")); 
		stickers.add(new SmiliesListItem(":o)", 		R.drawable.smilie_256_goofy, 
				":goofy:")); 
		stickers.add(new SmiliesListItem(":D", 			R.drawable.smilie_256_grin, 
				":grin:", ":-D", "XD", "X-D")); 
		stickers.add(new SmiliesListItem(":)", 			R.drawable.smilie_256_happy, 
				":happy:", ":-)")); 
		stickers.add(new SmiliesListItem("<3", 			R.drawable.smilie_256_heart, 
				":heart:")); 
		stickers.add(new SmiliesListItem(":drink:", 	R.drawable.smilie_256_icetea, 
				":drink:")); 
		stickers.add(new SmiliesListItem(":*", 			R.drawable.smilie_256_kiss, 
				":kiss:", ":-*")); 
		stickers.add(new SmiliesListItem(":kisses:", 	R.drawable.smilie_256_kisses, 
				":kisses:")); 
		stickers.add(new SmiliesListItem("^o|", 		R.drawable.smilie_256_penguin, 
				":penguin:")); 
		stickers.add(new SmiliesListItem(":present:", 	R.drawable.smilie_256_present, 
				":present:")); 
		stickers.add(new SmiliesListItem(":O", 			R.drawable.smilie_256_sleepy, 
				":sleepy:")); 
		stickers.add(new SmiliesListItem(":stopclock:", R.drawable.smilie_256_stopclock, 
				":stopclock:")); 
		stickers.add(new SmiliesListItem(":sun:", 		R.drawable.smilie_256_sun, 
				":sun:")); 
		stickers.add(new SmiliesListItem("O_o", 		R.drawable.smilie_256_surprised, 
				":surprised:")); 
		stickers.add(new SmiliesListItem(":whistling:", R.drawable.smilie_256_whistling, 
				":whistling:"));
		
		if(!(smallSmilies.size() == bigSmilies.size() && bigSmilies.size() == stickers.size())) {
			throw new RuntimeException("SmilieManager: List sizes must be equal."); 
		}
	}
	
	private List<SmiliesListItem> smallSmilies;
	private List<SmiliesListItem> bigSmilies;
	private List<SmiliesListItem> stickers;
}
