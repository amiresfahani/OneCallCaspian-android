package uk.co.onecallcaspian.custom;

import uk.co.onecallcaspian.R;
import uk.co.onecallcaspian.custom.adapter.SmiliesListAdapter;
import uk.co.onecallcaspian.custom.adapter.SmiliesListItem;
import uk.co.onecallcaspian.custom.adapter.SmiliesManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class SmiliesDialogFragment extends DialogFragment implements OnItemClickListener {
	public SmiliesDialogFragment(SmilieDialogListener listener) {
		this.listener = listener;
		this.selectedSmilie = -1;
	}
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	gridView = new GridView(getActivity());
    	gridView.setAdapter(new SmiliesListAdapter(getActivity()));
    	gridView.setNumColumns(4);
    	//gridView.setStretchMode(3);
    	gridView.setOnItemClickListener(this);
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton(R.string.caspian_ok, onOkClick);
        builder.setNegativeButton(R.string.caspian_cancel, onCancelClick);
        builder.setTitle(null);
        builder.setView(gridView);
        return builder.create();
    }
    
    private OnClickListener onOkClick = new OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if(selectedSmilie > -1) {
				SmiliesListItem item = SmiliesManager.instance().getSmilie(selectedSmilie);
				String smilieTrigger = item.getPrimaryTrigger();
				listener.insertSmiley(smilieTrigger);
			}
			dialog.dismiss();
		}
	};

	private OnClickListener onCancelClick = new OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
		}
	};
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		selectedSmilie = position;
	}

	public interface SmilieDialogListener {
		public void insertSmiley(String txt);
	}
	
	int selectedSmilie;
	SmilieDialogListener listener;
	GridView gridView;
}
