package uk.co.onecallcaspian.custom;

public class WebApis {
	
	
	// https://onecallcaspian.co.uk/mobile/create?phone_code=84&phone_number=979548774&country=us
	public static String signup = "https://onecallcaspian.co.uk/mobile/create?";
	
	//http://onecallcaspian.co.uk/mobile/country 
	public static String countries = "https://onecallcaspian.co.uk/mobile/country";
	
	// https://onecallcaspian.co.uk/mobile/confirm?code=250585
	public static String access_code_confirmation = "https://onecallcaspian.co.uk/mobile/confirm?";

	//https://onecallcaspian.co.uk/mobile/forgotPassword?phone_code=91&phone_number=9643258963
	public static String forgot_password = "https://onecallcaspian.co.uk/mobile/forgotPassword?"; 
	
	// https://onecallcaspian.co.uk/mobile/credit?phone_number=1234567890&password=123456
	public static String balance = "https://onecallcaspian.co.uk/mobile/credit?";

	// https://onecallcaspian.co.uk/mobile/sms?phone_number=1234567890&password=123456&from=1234567890&text=SMSTEST&receiver=2134567890
	public static String sms = "https://onecallcaspian.co.uk/mobile/sms?";
}
