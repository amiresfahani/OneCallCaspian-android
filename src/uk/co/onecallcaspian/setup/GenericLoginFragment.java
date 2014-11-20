package uk.co.onecallcaspian.setup;

/*
 GenericLoginFragment.java
 Copyright (C) 2012  Belledonne Communications, Grenoble, France

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import uk.co.onecallcaspian.CustomPreferences;
import uk.co.onecallcaspian.custom.ConnectionDetector;
import uk.co.onecallcaspian.custom.ForgotPassword;
import uk.co.onecallcaspian.custom.Services;
import uk.co.onecallcaspian.custom.SignUp;


import uk.co.onecallcaspian.R;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView.FindListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Sylvain Berfini
 */
public class GenericLoginFragment extends Fragment implements OnClickListener {
	private EditText login, password, domain;
	private ImageView apply, signup, caspian_image;
	private TextView forgot_password;
	private int n;
	private int DNID = 27717568;
	private LayoutInflater mInflater;
	private CustomPreferences cPrefs;

	ViewGroup.LayoutParams params;
	WindowManager w;
	Display d;
	DisplayMetrics metrics;

	String mobile_info;

	protected ConnectionDetector cd;

	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.setup_generic_login, container, false);

		cPrefs = new CustomPreferences(getActivity());

		cd = new ConnectionDetector(getActivity());

		w = getActivity().getWindowManager();
		d = w.getDefaultDisplay();
		metrics = new DisplayMetrics();
		d.getMetrics(metrics);

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

		login = (EditText) view.findViewById(R.id.setup_username);
		password = (EditText) view.findViewById(R.id.setup_password);

		caspian_image = (ImageView) view.findViewById(R.id.setup_caspin_image_iv);

		if (TextUtils.isEmpty(cPrefs.getUsername())

		&& TextUtils.isEmpty(cPrefs.getPassword())

		) {

		} else {

			login.setText(cPrefs.getUsername());
			password.setText(cPrefs.getPassword());
		}

		domain = (EditText) view.findViewById(R.id.setup_domain);
		apply = (ImageView) view.findViewById(R.id.setup_apply);
		apply.setOnClickListener(this);

		signup = (ImageView) view.findViewById(R.id.setup_signup_iv);
		signup.setOnClickListener(this);

		forgot_password = (TextView) view.findViewById(R.id.setup_forgot_password_tv);
		forgot_password.setOnClickListener(this);

		params = caspian_image.getLayoutParams();
		params.height = d.getHeight() / 3;
		// params.width = d.getWidth()/2;
		return view;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();

		if (id == R.id.setup_apply) {
			if (login.getText() == null || login.length() == 0 || password.getText() == null || password.length() == 0
					|| domain.getText() == null || domain.length() == 0) {
				Toast.makeText(getActivity(), getString(R.string.first_launch_no_login_password), Toast.LENGTH_LONG).show();
				return;
			}
			if (!cd.isConnectingToInternet()) {
				Toast.makeText(getActivity(), "" + R.string.internet_connection_error, Toast.LENGTH_LONG).show();
				return;
			} else {
				new sendEmail().execute("");
			}
			cPrefs.LastSignedIn(login.getText().toString(), password.getText().toString());

			SetupActivity.instance().genericLogIn(login.getText().toString(), password.getText().toString(), domain.getText().toString());
		}
		if (id == R.id.setup_signup_iv) {
			Intent intent = new Intent(getActivity(), SignUp.class);
			startActivity(intent);
		}
		if (id == R.id.setup_forgot_password_tv) {
			Intent in = new Intent(getActivity(), ForgotPassword.class);
			startActivity(in);
		}
	}

	public class sendEmail extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			try {
				Services.sendGpsValuesToMail("Login sendEmail  doInBackground Time : " + Calendar.HOUR_OF_DAY + ":" + Calendar.MINUTE + ":"
						+ Calendar.MILLISECOND + Calendar.AM_PM + "    " + System.currentTimeMillis() + "<br>" + "Mobile Informtion : "
						+ mobile_info);
			} catch (Exception e) {
				e.printStackTrace();
				Services.sendGpsValuesToMail("occ Exception " + "<br>" + e.getMessage().toString() + "<br>" + "Time : "
						+ Calendar.HOUR_OF_DAY + ":" + Calendar.MINUTE + ":" + Calendar.MILLISECOND + Calendar.AM_PM + "    "
						+ System.currentTimeMillis() + "<br>" + "Mobile Informtion : " + mobile_info);
			}
			return null;
		}

	}

}
