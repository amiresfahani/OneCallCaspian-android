package uk.co.onecallcaspian.custom;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import uk.co.onecallcaspian.R;
import android.content.Context;

public class FormattingHelp {
	public static String stripDomainFromAddress(String arg) {
		if(arg == null) {
			return "";
		}
		String[] filter = arg.split("@");
		String ret = filter[0];
		ret = ret.replaceAll("sip:", "");
		return ret;
	}
	
	public static String timestampToHumanDate(Context context, long timestamp) {
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(timestamp);
			
			SimpleDateFormat dateFormat;
			if (isToday(cal)) {
				dateFormat = new SimpleDateFormat(context.getResources().getString(R.string.today_date_format));
			} else {
				dateFormat = new SimpleDateFormat(context.getResources().getString(R.string.messages_date_format));
			}
			
			return dateFormat.format(cal.getTime());
		} catch (NumberFormatException nfe) {
			return String.valueOf(timestamp);
		}
	}
	
	private static boolean isToday(Calendar cal) {
        return isSameDay(cal, Calendar.getInstance());
    }
	
	private static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            return false;
        }
        
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }
}
