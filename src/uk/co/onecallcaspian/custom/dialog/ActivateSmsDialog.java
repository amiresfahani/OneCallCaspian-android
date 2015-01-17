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
import uk.co.onecallcaspian.custom.rest.ActivateSmsHandler;
import uk.co.onecallcaspian.custom.rest.RequestHandlerCallback;
import uk.co.onecallcaspian.custom.rest.data.ActivateSmsJsonData;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * @author fizzl
 *
 */
public class ActivateSmsDialog implements RequestHandlerCallback<ActivateSmsJsonData> {
    public static void activateIfFirstRun(Activity activity) {
    	if(LinphonePreferences.instance().isSmsActivated()) {
    		return;
    	}
    	activateSms(activity);
    }
    
    public static void activateSms(Activity activity) {
    	ActivateSmsDialog dlg = new ActivateSmsDialog();
    	dlg.activate(activity);
    }

    private void activate(Activity activity) {
    	this.activity = activity;
    	AlertDialog.Builder builder = new AlertDialog.Builder(activity);

    	final ProgressBar progress = new ProgressBar(activity);
    	progress.setVisibility(View.GONE);
    	
    	builder.setCancelable(false);
    	builder.setView(progress);
    	builder.setTitle(R.string.activate_title);
    	builder.setMessage(R.string.activate_prompt);
    	builder.setPositiveButton(R.string.yes, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialogInterface = dialog;
				progress.setVisibility(View.VISIBLE);
				
				ActivateSmsHandler handler = new ActivateSmsHandler(ActivateSmsDialog.this);
				
				LinphonePreferences prefs = LinphonePreferences.instance();
				String number = prefs.getAccountUsername(prefs.getDefaultAccountIndex());
				String password = prefs.getAccountPassword(prefs.getDefaultAccountIndex());
				
				handler.setMyPhoneNumber(number);
				handler.setPassword(password);
				handler.execute();
			}
		});
    	
    	builder.setNegativeButton(R.string.no, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				LinphonePreferences.instance().activateSms();
				dialog.dismiss();
			}
		});
    	
    	builder.create().show();
    }
    
    private DialogInterface dialogInterface;
    private Activity activity;
    
    @Override
	public void requestHandlerDone(ActivateSmsJsonData data) {
    	dialogInterface.dismiss();
    	VerifySmsDialog.verify(activity);
	}

	@Override
	public void requestHandlerError(String reason) {
		Toast.makeText(activity, R.string.activate_error, Toast.LENGTH_LONG)
		.show();
		dialogInterface.dismiss();
	}
}
