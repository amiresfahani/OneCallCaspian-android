package uk.co.onecallcaspian.custom;

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
}
