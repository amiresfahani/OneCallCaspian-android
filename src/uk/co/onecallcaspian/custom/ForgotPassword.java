package uk.co.onecallcaspian.custom;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.mediastream.Log;

import uk.co.onecallcaspian.LinphoneLauncherActivity;

import uk.co.onecallcaspian.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class ForgotPassword extends Activity{
	
	ImageView back,submit,reset;
	EditText ph_no;
	String countries_data,countryname,country_selected_code;
	String forgot_password_details,phone_number;
	Spinner countries_spinner;
	ArrayList<String> countries,country_codes;
	ProgressDialog pd;
	
	protected ConnectionDetector cd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.forgot_password);
		
		cd = new ConnectionDetector(ForgotPassword.this);
		
		back = (ImageView)findViewById(R.id.forgot_password_back_iv);
		submit = (ImageView)findViewById(R.id.forgot_password_submit_iv);
		reset = (ImageView)findViewById(R.id.forgot_password_reset_iv);
		
		ph_no = (EditText)findViewById(R.id.forgot_password_phone_number_et);
		
		countries_spinner = (Spinner)findViewById(R.id.forgot_password_country_spinner);
		
		countries = new ArrayList<String>();
		country_codes = new ArrayList<String>();
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				phone_number = ph_no.getText().toString();
				if(phone_number.equals("")){
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
							ForgotPassword.this);
					alertDialogBuilder
							.setMessage(
									"Please enter phone number")
							.setCancelable(false)
							.setPositiveButton("Ok",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											dialog.cancel();

										}
									});
					AlertDialog alertDialog = alertDialogBuilder.create();
					alertDialog.show();
				}else{
					
					if (!cd.isConnectingToInternet()) {
						Toast.makeText(ForgotPassword.this, ""+R.string.internet_connection_error, Toast.LENGTH_LONG).show();
						return;
					} else {
						new getforgotPassword().execute("");
					}
				}
			}
		});
		reset.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ph_no.setText("");
			}
		});

		if (!cd.isConnectingToInternet()) {
			Toast.makeText(ForgotPassword.this, ""+R.string.internet_connection_error, Toast.LENGTH_LONG).show();
			return;
		} else {
			new getCountriesList().execute("");
		}
	}
	
	public class getforgotPassword extends AsyncTask<String, Void, Void>{

		@Override
		protected Void doInBackground(String... params) {
			forgot_password_details = Services.getForgotPasswordDetails(country_selected_code,phone_number);
			Log.i("forgot_password_details","forgot_password_details is-> "+forgot_password_details);
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = ProgressDialog.show(ForgotPassword.this, "", "getting password...");
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			pd.dismiss();
			
			try {
				JSONObject jobj = new JSONObject(forgot_password_details);
				boolean errorstatus = jobj.getBoolean("error"); 
			//	String status = jobj.getString("status");
			//	String message = jobj.getString("message"); status.equals("fail")
				if(errorstatus != false){
					AlertDialog.Builder alert_dialog_builder = new AlertDialog.Builder(ForgotPassword.this);
					alert_dialog_builder.setMessage("Phone number does not exist, please try again.")
					.setCancelable(false)
					.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							
							ph_no.setText("");
							
						}
					});
					AlertDialog alert = alert_dialog_builder.create();
					alert.show();
				}else{
					AlertDialog.Builder alert_dialog_builder = new AlertDialog.Builder(ForgotPassword.this);
					alert_dialog_builder.setMessage("Password sent to your mobile. Please login again")
					.setCancelable(false)
					.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							Intent in = new Intent(ForgotPassword.this,LinphoneLauncherActivity.class);
							startActivity(in);
						}
					});
					AlertDialog alert = alert_dialog_builder.create();
					alert.show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	 public class getCountriesList extends AsyncTask<String, Void, Void>{

		@Override
		protected Void doInBackground(String... params) {
			countries_data = Services.getCountriesListDetails();
			//Log.i("countries_data","countries_data is: "+countries_data);
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//pd = ProgressDialog.show(ForgotPassword.this, "", "loading...");
			
			
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			//pd.dismiss();
			try {
				 JSONArray jarry = new JSONArray(countries_data);
				for(int i=0;i<jarry.length();i++){
					JSONArray jarry1 = jarry.getJSONArray(i);
						String cname = jarry1.getString(0);
						String ccode = jarry1.getString(1);
						//Log.i("cname","cname is: "+cname);
						//Log.i("ccode","ccode is: "+ccode);
						countries.add(cname);
						country_codes.add(ccode);
				}
				ArrayAdapter<String> country_adapter = new ArrayAdapter<String>(
						ForgotPassword.this, R.layout.countries_spinner_item,
						countries);
				countries_spinner.setAdapter(country_adapter);
				countries_spinner.setSelection(country_adapter
						.getPosition("United Kingdom"));
				countries_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int pos, long arg3) {
						countryname = (String) countries_spinner
								.getItemAtPosition(countries_spinner
										.getSelectedItemPosition());
						
						country_selected_code = country_codes.get(pos);
						Log.i("pos","pos is: "+pos);
						Log.i("countryname","countryname is: "+countryname);
						Log.i("country_selected_code","country_selected_code is: "+country_selected_code);
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {

					}

					
				});
			} catch (JSONException e) {
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
			}
		
		}
		 
	 }
}
