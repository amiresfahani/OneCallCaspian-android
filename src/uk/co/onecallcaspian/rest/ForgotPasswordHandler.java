package uk.co.onecallcaspian.rest;

import uk.co.onecallcaspian.custom.WebApis;
import uk.co.onecallcaspian.data.ForgotPasswordJsonData;

public class ForgotPasswordHandler extends BaseHandler<ForgotPasswordJsonData> {
	// Construction
	RequestHandlerCallback<ForgotPasswordJsonData> callback;	
	public ForgotPasswordHandler(RequestHandlerCallback<ForgotPasswordJsonData> callback) {
		super(ForgotPasswordJsonData.class);
		this.callback = callback;
	}
	
	public void execute() {
		if(countryCode == null || phoneNumber == null) {
			throw new RuntimeException("Missing a parameter in ForgotPasswordHandler.");
		}
		HandlerParams<ForgotPasswordJsonData> hp = new HandlerParams<ForgotPasswordJsonData>();
		hp.callback = this.callback;

		RequestParams reqParams = new RequestParams();
		reqParams.add("phone_code", countryCode);
		reqParams.add("phone_number", phoneNumber);
		
		reqParams.setMethod("GET");
		reqParams.setUri(WebApis.forgot_password);
		hp.requestParams = reqParams;

		super.execute(hp);
	}
	
	// Parameters
	private String countryCode;
	private String phoneNumber;
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
}
