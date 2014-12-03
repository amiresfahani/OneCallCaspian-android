package uk.co.onecallcaspian.custom;

import org.json.JSONException;
import org.json.JSONObject;

import uk.co.onecallcaspian.CustomPreferences;
import uk.co.onecallcaspian.LinphoneActivity;

import uk.co.onecallcaspian.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class ConfirmSignUpAccessCode extends Activity {
	EditText access_code, domain;
	ImageView enter, back;
	String code, code_confirmation_details;
	ProgressDialog pd;

	String phone_number, password;
	private CustomPreferences cPrefs;
	protected ConnectionDetector cd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.signup_code_confirmation);

		cd = new ConnectionDetector(ConfirmSignUpAccessCode.this);

		cPrefs = new CustomPreferences(ConfirmSignUpAccessCode.this);

		access_code = (EditText) findViewById(R.id.signup_code_access_code_et);
		domain = (EditText) findViewById(R.id.signup_code_domain_et);

		enter = (ImageView) findViewById(R.id.signup_code_enter_iv);
		back = (ImageView) findViewById(R.id.signup_code_back_iv);

		String tempCode = getIntent().getExtras().getString("activation_code");
		access_code.setText(tempCode);
		// access_code.setText(SignUp.accesscode.toString());

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		enter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				code = access_code.getText().toString();

				if (code.equals("")) {
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ConfirmSignUpAccessCode.this);
					alertDialogBuilder.setMessage("Please enter the access code you received").setCancelable(false)
							.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									dialog.cancel();

								}
							});
					AlertDialog alertDialog = alertDialogBuilder.create();
					alertDialog.show();
				} else {
					if (!cd.isConnectingToInternet()) {
						Toast.makeText(ConfirmSignUpAccessCode.this, "" + R.string.internet_connection_error, Toast.LENGTH_LONG).show();
						return;
					} else {
						new codeConfirmation().execute("");
					}
				}

			}
		});

	}

	public class login extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = ProgressDialog.show(ConfirmSignUpAccessCode.this, "", "signing...");
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			pd.dismiss();
			Intent intent = new Intent(ConfirmSignUpAccessCode.this, Password.class);
			startActivity(intent);
		}

	}

	public class codeConfirmation extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			code_confirmation_details = Services.getSignUpCodeConfirmation(code);
			Log.i("code_confirmation_details", "code_confirmation_details -> " + code_confirmation_details);
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = ProgressDialog.show(ConfirmSignUpAccessCode.this, "", "authenticating...");

		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			pd.dismiss();
			try {
				JSONObject jobj = new JSONObject(code_confirmation_details);
				boolean status = jobj.getBoolean("error");

				if (status == true) {
					String message = jobj.getString("message");
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ConfirmSignUpAccessCode.this);
					alertDialogBuilder.setMessage("" + message).setCancelable(false)
							.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									dialog.cancel();
									Intent intent = new Intent(ConfirmSignUpAccessCode.this, Password.class);
									startActivity(intent);
								}
							});
					AlertDialog alertDialog = alertDialogBuilder.create();
					alertDialog.show();
				} else {

					String msg = jobj.getString("message");
					String user_id = jobj.getString("user_id");
					String balance = jobj.getString("balance");
					password = jobj.getString("password");
					String username = jobj.getString("username");
					phone_number = jobj.getString("phone_number");

					Log.i("phone_number", "phone_number is-> " + phone_number);
					Log.i("password", "password is-> " + password);

					// Log.i("Registration success","Registration success");
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ConfirmSignUpAccessCode.this);
					alertDialogBuilder.setMessage("" + msg).setCancelable(false)
							.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									dialog.cancel();
									cPrefs.LastSignedIn(phone_number, password);
									// new login().execute("");
									Intent intent = new Intent(ConfirmSignUpAccessCode.this, LinphoneActivity.class);
									intent.setData(getIntent().getData());
									startActivity(intent);
								}
							});
					AlertDialog alertDialog = alertDialogBuilder.create();
					alertDialog.show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
