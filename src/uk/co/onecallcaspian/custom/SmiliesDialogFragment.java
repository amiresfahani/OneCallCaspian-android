package uk.co.onecallcaspian.custom;

import org.linphone.mediastream.Version;

import uk.co.onecallcaspian.R;
import uk.co.onecallcaspian.custom.adapter.SmiliesListAdapter;
import uk.co.onecallcaspian.custom.adapter.SmiliesListItem;
import uk.co.onecallcaspian.custom.adapter.SmiliesManager;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Build;
import android.os.Bundle;
import android.app.DialogFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class SmiliesDialogFragment extends DialogFragment implements OnItemClickListener {
	public SmiliesDialogFragment(SmilieDialogListener listener) {
		this.listener = listener;
		this.selectedSmilie = -1;
	}
	
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setChoiceModeCompat() {
    	if(Version.sdkAboveOrEqual(Version.API11_HONEYCOMB_30)) 
    		gridView.setChoiceMode(GridView.CHOICE_MODE_SINGLE);		
	}
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	gridView = new GridView(getActivity());
    	gridView.setAdapter(new SmiliesListAdapter(getActivity()));
    	gridView.setNumColumns(4);
    	setChoiceModeCompat();
    	gridView.setOnItemClickListener(this);
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setNegativeButton(R.string.caspian_cancel, onCancelClick);
        builder.setTitle(null);
        builder.setView(gridView);
        Dialog ret = builder.create();
        myDialog = ret;
        return ret;
    }
    
	private OnClickListener onCancelClick = new OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
		}
	};
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		selectedSmilie = position;
		if(selectedSmilie > -1) {
			SmiliesListItem item = SmiliesManager.instance().getSmilie(selectedSmilie);
			String smilieTrigger = item.getPrimaryTrigger();
			listener.insertSmiley(smilieTrigger);
		}
		myDialog.dismiss();
	}

	public interface SmilieDialogListener {
		public void insertSmiley(String txt);
	}
	
	int selectedSmilie;
	SmilieDialogListener listener;
	GridView gridView;
	DialogInterface myDialog;
}
