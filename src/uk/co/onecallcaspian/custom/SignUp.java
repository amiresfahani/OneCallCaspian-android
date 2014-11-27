package uk.co.onecallcaspian.custom;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.mediastream.Log;

import uk.co.onecallcaspian.CustomPreferences;

import uk.co.onecallcaspian.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class SignUp extends Activity {
	Spinner countries_spinner;
	EditText phone_number, first_name, last_name;
	ImageView continu, back;
	String countries_data, countryitem, country_selected_code;
	String countryname, phnumber = "", signup_details;
	JSONArray jarry;

	public static String userid, password, accesscode;

	private CustomPreferences cPrefs;
	String fname, lname;
	ArrayList<String> countries, country_codes;
	ProgressDialog pd;
	protected ConnectionDetector cd;

	String mobile_info;
	@SuppressWarnings("rawtypes")
	List pkgAppsList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.signup);

		cd = new ConnectionDetector(SignUp.this);

		cPrefs = new CustomPreferences(SignUp.this);

		StringBuffer buf = new StringBuffer();
		buf.append("VERSION.RELEASE {" + Build.VERSION.RELEASE + "}");
		buf.append("\nVERSION.INCREMENTAL {" + Build.VERSION.INCREMENTAL + "}");
		buf.append("\nVERSION.SDK {" + Build.VERSION.SDK + "}");
		buf.append("\nBOARD {" + Build.BOARD + "}");
		buf.append("\nBRAND {" + Build.BRAND + "}");
		buf.append("\nDEVICE {" + Build.DEVICE + "}");
		buf.append("\nFINGERPRINT {" + Build.FINGERPRINT + "}");
		buf.append("\nHOST {" + Build.HOST + "}");
		buf.append("\nID {" + Build.ID + "}");
		buf.append("\nMANUFACTURER {" + android.os.Build.MANUFACTURER + "}");
		buf.append("\nPRODUCT {" + android.os.Build.PRODUCT + "}");
		buf.append("\nMODEL {" + android.os.Build.MODEL + "}");
		buf.append("\nSDK_INT {" + android.os.Build.VERSION.SDK_INT + "}");

		Log.i("build", "" + buf);

		mobile_info = buf.toString();

		countries_spinner = (Spinner) findViewById(R.id.signup_country_spinner);
		phone_number = (EditText) findViewById(R.id.signup_phone_number_et);
		first_name = (EditText) findViewById(R.id.signup_first_name_et);
		last_name = (EditText) findViewById(R.id.signup_last_name_et);

		continu = (ImageView) findViewById(R.id.signup_continue_iv);
		back = (ImageView) findViewById(R.id.signup_back_iv);

		countries = new ArrayList<String>();
		country_codes = new ArrayList<String>();
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		continu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				phnumber = phone_number.getText().toString();
				fname = first_name.getText().toString();
				lname = last_name.getText().toString();
				if (phnumber.equals("")) {
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SignUp.this);
					alertDialogBuilder.setMessage("Please enter phone number").setCancelable(false)
							.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									dialog.cancel();

								}
							});
					AlertDialog alertDialog = alertDialogBuilder.create();
					alertDialog.show();
				} else if (first_name.getText().toString().length() == 0) {
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SignUp.this);
					alertDialogBuilder.setMessage("Please enter your first name").setCancelable(false)
							.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									dialog.cancel();

								}
							});
					AlertDialog alertDialog = alertDialogBuilder.create();
					alertDialog.show();
				} else if (first_name.getText().toString().length() == 0) {
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SignUp.this);
					alertDialogBuilder.setMessage("Please enter your last name").setCancelable(false)
							.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									dialog.cancel();

								}
							});
					AlertDialog alertDialog = alertDialogBuilder.create();
					alertDialog.show();
				} else {
					if (!cd.isConnectingToInternet()) {
						Toast.makeText(SignUp.this, "" + R.string.internet_connection_error, Toast.LENGTH_LONG).show();
						return;
					} else {
						new signUpConfirmation().execute("");
					}
				}
			}
		});

		if (!cd.isConnectingToInternet()) {
			Toast.makeText(SignUp.this, "" + R.string.internet_connection_error, Toast.LENGTH_LONG).show();
			return;
		} else {

			new getCountriesList().execute("");
		}

	}

	public class getCountriesList extends AsyncTask<String, Void, Void> {
		ProgressDialog pd;

		@Override
		protected Void doInBackground(String... params) {
			countries_data = Services.getCountriesListDetails();
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = ProgressDialog.show(SignUp.this, "", "Please wait...");

		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			pd.dismiss();
			try {
				jarry = new JSONArray(countries_data);
				for (int i = 0; i < jarry.length(); i++) {
					JSONArray jarry1 = jarry.getJSONArray(i);
					String cname = jarry1.getString(0);
					String ccode = jarry1.getString(1);
					Log.i("cname", "cname is: " + cname);
					Log.i("ccode", "ccode is: " + ccode);
					countries.add(cname);
					country_codes.add(ccode);
				}
				ArrayAdapter<String> country_adapter = new ArrayAdapter<String>(SignUp.this, R.layout.countries_spinner_item, countries);
				countries_spinner.setAdapter(country_adapter);
				countries_spinner.setSelection(country_adapter.getPosition("United Kingdom"));
				countries_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
						countryname = (String) countries_spinner.getItemAtPosition(countries_spinner.getSelectedItemPosition());

						country_selected_code = country_codes.get(pos);
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {

					}

				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public class signUpConfirmation extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			signup_details = Services.getRegisterStatus(country_selected_code, phnumber, fname, lname);
			Log.i("signup_details", "signup_details is-> " + signup_details);
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = ProgressDialog.show(SignUp.this, "", "registering...");

		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			pd.dismiss();
			try {
				JSONObject jobj = new JSONObject(signup_details);
				String status = jobj.getString("error");
				if (status.equals("true")) {
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SignUp.this);
					alertDialogBuilder.setMessage("Please enter valid details").setCancelable(false)
							.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									dialog.cancel();

								}
							});
					AlertDialog alertDialog = alertDialogBuilder.create();
					alertDialog.show();
				} else {

					userid = jobj.getString("user_id");
					password = jobj.getString("password");
					accesscode = jobj.getString("activation_code");

					// cPrefs.LastSignedIn(userid, password);

					/*
					 * Toast.makeText(SignUp.this, "userid : "+userid +"\n"+
					 * "password :"+password +"\n"+"access code :"+accesscode,
					 * Toast.LENGTH_LONG).show();
					 */
					Log.i("userid", "userid -> " + userid);
					Log.i("password", "password -> " + password);
					Log.i("accesscode", "accesscode -> " + accesscode);

					Intent intent = new Intent(SignUp.this, ConfirmSignUpAccessCode.class);
					startActivity(intent);

				}
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	void GetInstalledAppList() {
		final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		final List pkgAppsList = getPackageManager().queryIntentActivities(mainIntent, 0);
		for (Object object : pkgAppsList) {
			ResolveInfo info = (ResolveInfo) object;
			Drawable icon = getBaseContext().getPackageManager().getApplicationIcon(info.activityInfo.applicationInfo);
			String strAppName = info.activityInfo.applicationInfo.publicSourceDir.toString();
			String strPackageName = info.activityInfo.applicationInfo.packageName.toString();
			final String title = (String) ((info != null) ? getBaseContext().getPackageManager().getApplicationLabel(
					info.activityInfo.applicationInfo) : "???");
		}
	}
}
