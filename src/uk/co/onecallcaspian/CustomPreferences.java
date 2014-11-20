package uk.co.onecallcaspian;



import android.content.Context;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.Toast;


public class CustomPreferences {
	// Shared Preferences
	SharedPreferences pref;
	
	// Editor for Shared preferences
	Editor editor;
	
	// Context
	Context _context;
	
	// Shared pref mode
	int PRIVATE_MODE = 0;
	
	// Sharedpref file name
	private static final String PREF_NAME = "OneCallCaspianPref";
	

	

	public static final String KEY_NAME = "username";
	

	public static final String KEY_PASSWORD = "password";
	
	// Constructor
	public CustomPreferences(Context context){
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
	
	}
	

	public void LastSignedIn(String name, String password){

		editor = pref.edit();

		editor.putString(KEY_NAME, name);
		

		editor.putString(KEY_PASSWORD, password);
		

		editor.commit();
		
		
	
		
	}	
	

	

	public String getUsername(){
	
			

		return pref.getString(KEY_NAME, null);
	}
	
	

	public String getPassword(){
	
		
		
		return pref.getString(KEY_PASSWORD, null);
	}
	

	public void ClearLastLoggedIn(){
		// Clearing all data from Shared Preferences
		editor.clear();
		editor.commit();
		

	}

}