/*
ActivateSmsDialogFragment.java
Copyright (C) 2015 Lassi Marttala, Maxpower Inc (http://maxp.fi)

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

package uk.co.onecallcaspian.custom.dialog;

import uk.co.onecallcaspian.LinphonePreferences;
import uk.co.onecallcaspian.R;
import uk.co.onecallcaspian.custom.rest.RequestHandlerCallback;
import uk.co.onecallcaspian.custom.rest.VerifySmsHandler;
import uk.co.onecallcaspian.custom.rest.data.VerifySmsJsonData;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @author fizzl
 *
 */
public class VerifySmsDialog implements RequestHandlerCallback<VerifySmsJsonData> {
    public static void verify(Activity activity) {
    	VerifySmsDialog dlg = new VerifySmsDialog();
    	dlg.activate(activity);
    }
    
    private void activate(Activity activity) {
    	this.activity = activity;
        input = new EditText(activity);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setCancelable(false);
    	builder.setTitle(R.string.activate_title);
    	builder.setMessage(R.string.verify_prompt);
    	builder.setView(input);
    	builder.setPositiveButton(R.string.activate, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialogInterface = dialog;
				
				VerifySmsHandler handler = new VerifySmsHandler(VerifySmsDialog.this);
				
				LinphonePreferences prefs = LinphonePreferences.instance();
				String number = prefs.getAccountUsername(prefs.getDefaultAccountIndex());
				String password = prefs.getAccountPassword(prefs.getDefaultAccountIndex());
				
				handler.setMyPhoneNumber(number);
				handler.setPassword(password);
				handler.setCode(input.getText().toString());
				handler.execute();

			}
		});
    	
    	builder.setNegativeButton(R.string.CancelButtonText, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
    	
    	builder.create().show();
    }
    private DialogInterface dialogInterface;
    private Activity activity;
    private EditText input;
    
    @Override
	public void requestHandlerDone(VerifySmsJsonData data) {
		LinphonePreferences.instance().activateSms();
		Toast.makeText(activity, R.string.activate_success, Toast.LENGTH_LONG)
		.show();
    	dialogInterface.dismiss();
	}

	@Override
	public void requestHandlerError(String reason) {
		Toast.makeText(activity, R.string.activate_error, Toast.LENGTH_LONG)
		.show();
		dialogInterface.dismiss();
	}
}
