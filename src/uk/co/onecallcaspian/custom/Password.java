package uk.co.onecallcaspian.custom;

import uk.co.onecallcaspian.CustomPreferences;
import uk.co.onecallcaspian.setup.SetupActivity;

import uk.co.onecallcaspian.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Password extends Activity{
	TextView password;
	ImageView continue_iv,back;
	EditText domain;
	String pwd ;
	private CustomPreferences cPrefs;
	ProgressDialog pd;
	
	protected ConnectionDetector cd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.password);
		
		cd = new ConnectionDetector(Password.this);
		
		cPrefs = new CustomPreferences(Password.this);
		
		password = (TextView)findViewById(R.id.password_pwd_tv);
		continue_iv = (ImageView)findViewById(R.id.password_continue_iv);
		back = (ImageView)findViewById(R.id.password_back_iv);
		
		domain = (EditText)findViewById(R.id.password_domain_et);
		
		pwd = SignUp.password.toString();
		
		password.setText(pwd);
		
		continue_iv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (!cd.isConnectingToInternet()) {
					Toast.makeText(Password.this, ""+R.string.internet_connection_error, Toast.LENGTH_LONG).show();
					return;
				} else {
					new login().execute("");
				}
			}
		});
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
	}
	
	public class login extends AsyncTask<String, Void, Void>{

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
			pd = ProgressDialog.show(Password.this, "", "signing...");
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			pd.dismiss();
			Toast.makeText(Password.this, "Please wait while signing...", Toast.LENGTH_SHORT).show();
		
			SetupActivity.instance().genericLogIn(cPrefs.getUsername().toString(),cPrefs.getPassword().toString(), domain.getText().toString());
		}
		
	}

}
