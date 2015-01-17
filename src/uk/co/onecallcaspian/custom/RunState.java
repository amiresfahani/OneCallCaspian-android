/*
RunState.java
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

package uk.co.onecallcaspian.custom;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.widget.Toast;

/**
 * @author fizzl
 * 
 */
public class RunState {
	public static boolean isRunning(Activity activity) {
		ArrayList<String> runningactivities = new ArrayList<String>();
		ActivityManager activityManager = (ActivityManager) activity.getBaseContext().getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> services = activityManager.getRunningTasks(Integer.MAX_VALUE);

		for (int i1 = 0; i1 < services.size(); i1++) {
			runningactivities.add(0, services.get(i1).topActivity.toString());
		}

		if (runningactivities.contains("ComponentInfo{uk.co.onecallcaspian/uk.co.onecallcaspian.LinphoneActivity}") == true) {
			return true;
		}
		return false;
	}
}
