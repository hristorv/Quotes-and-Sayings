package tk.example.quotesandsayings.controller;

import java.util.Calendar;
import tk.example.quotesandsayings.model.Constants;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

public class AlarmController {
	
	private static final int MILL_SEC_FACTOR = 3600000;
	private static final String TIME_DEFAULT_VALUE = "1";

	public void cancelAlarmByID(Context context, int ID) {
		Intent intent = new Intent(context, AlarmReceiver.class);
		PendingIntent pintent = PendingIntent.getBroadcast(context, ID, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarm = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarm.cancel(pintent);
	}

	public void setupAlarmByID(Context context, int ID, long timeInterval) {
		Calendar cal = Calendar.getInstance();
		Intent intent = new Intent(context, AlarmReceiver.class);
		intent.putExtra(Constants.Extra.SERVICE_TAG_KEY, ID);
		PendingIntent pintent = PendingIntent.getBroadcast(context, ID, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarm = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarm.setRepeating(AlarmManager.RTC, cal.getTimeInMillis(),
				timeInterval, pintent);
	}
	
	public void checkBroadCastReceiver(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		boolean wallpaperActive = prefs.getBoolean(
				Constants.PreferencesKeys.KEY_PREF_WALLPAPER_ACTIVE, false);
		boolean widgetActive = prefs.getBoolean(
	    			Constants.PreferencesKeys.KEY_PREF_WIDGET_ACTIVE, false);
		boolean notificationActive = prefs.getBoolean(
				Constants.PreferencesKeys.KEY_PREF_NOTIFICATION_ACTIVE, false);
		if (!wallpaperActive && !widgetActive && !notificationActive) {
			ComponentName receiver = new ComponentName(context,
					AlarmReceiver.class);
			PackageManager pm = context.getPackageManager();

			pm.setComponentEnabledSetting(receiver,
					PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
					PackageManager.DONT_KILL_APP);
		}
	}

	public void setupBroadCastReceiver(Context context) {
		ComponentName receiver = new ComponentName(context, AlarmReceiver.class);
		PackageManager pm = context.getPackageManager();
		pm.setComponentEnabledSetting(receiver,
				PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
				PackageManager.DONT_KILL_APP);
	}

	public long getRepeatTimeInMillSec(Context context,String key) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		int time = Integer.parseInt(prefs.getString(key, TIME_DEFAULT_VALUE));
		return time * MILL_SEC_FACTOR;
	}

}
