package uk.co.onecallcaspian.custom;

import uk.co.onecallcaspian.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.GridView;

public class SmiliesDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	gridView = new GridView(getActivity());
    	
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton(R.string.caspian_ok, onOkClick);
        builder.setNegativeButton(R.string.caspian_cancel, onCancelClick);
        builder.setTitle(R.string.caspian_select_smilie);
        return builder.create();
    }
    
    private OnClickListener onOkClick = new OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			
		}
	};

	private OnClickListener onCancelClick = new OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			
		}
	};
	
	GridView gridView;
}
