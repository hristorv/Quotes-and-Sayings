package tk.example.quotesandsayings.controller;

import java.util.Calendar;
import tk.example.quotesandsayings.model.Constants;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

public class PreferencesListener implements
		SharedPreferences.OnSharedPreferenceChangeListener {
	private static final int STARTING_CATEGORY_INDEX = 0;
	SharedPreferences prefs;
	Context context;

	public PreferencesListener(Context context) {
		this.context = context;
		prefs = PreferenceManager.getDefaultSharedPreferences(context);

	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (checkActivePreference(sharedPreferences, key))
			return;
		if (checkFilterPreference(sharedPreferences, key))
			return;
		if (checkTimePreference(sharedPreferences, key))
			return;
	}

	private boolean checkTimePreference(SharedPreferences sharedPreferences,
			String key) {
		AlarmController alarmController = new AlarmController();
		if (key.equals(Constants.PreferencesKeys.KEY_PREF_NOTIFICATION_TIME)
				&& prefs.getBoolean(
						Constants.PreferencesKeys.KEY_PREF_NOTIFICATION_ACTIVE,
						false)) {
			alarmController.cancelAlarmByID(context,
					Constants.ServicesID.NOTIFICATION_ALARM_ID);
			alarmController
					.setupAlarmByID(
							context,
							Constants.ServicesID.NOTIFICATION_ALARM_ID,
							alarmController
									.getRepeatTimeInMillSec(
											context,
											Constants.PreferencesKeys.KEY_PREF_NOTIFICATION_TIME));
			return true;
		}
		if (key.equals(Constants.PreferencesKeys.KEY_PREF_WALLPAPER_TIME)
				&& prefs.getBoolean(
						Constants.PreferencesKeys.KEY_PREF_WALLPAPER_ACTIVE,
						false)) {
			alarmController.cancelAlarmByID(context,
					Constants.ServicesID.WALLPAPER_ALARM_ID);
			alarmController.setupAlarmByID(context,
					Constants.ServicesID.WALLPAPER_ALARM_ID,
					alarmController.getRepeatTimeInMillSec(context,
							Constants.PreferencesKeys.KEY_PREF_WALLPAPER_TIME));
			return true;
		}
		if (key.equals(Constants.PreferencesKeys.KEY_PREF_WIDGET_TIME)
				&& prefs.getBoolean(
						Constants.PreferencesKeys.KEY_PREF_WIDGET_ACTIVE, false)) {
			alarmController.cancelAlarmByID(context,
					Constants.ServicesID.WIDGET_ALARM_ID);
			alarmController.setupAlarmByID(context,
					Constants.ServicesID.WIDGET_ALARM_ID,
					alarmController.getRepeatTimeInMillSec(context,
							Constants.PreferencesKeys.KEY_PREF_WIDGET_TIME));
			return true;
		}
		return false;

	}

	private boolean checkFilterPreference(SharedPreferences sharedPreferences,
			String key) {

		if (key.equals(Constants.PreferencesKeys.KEY_PREF_WALLPAPER_FILTER)) {
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putInt(Constants.PreferencesKeys.CATEGORY_INDEX_WALLPAPER,
					STARTING_CATEGORY_INDEX);
			editor.putInt(Constants.PreferencesKeys.IMAGE_INDEX_WALLPAPER,
					STARTING_CATEGORY_INDEX);
			editor.commit();
			return true;
		}
		if (key.equals(Constants.PreferencesKeys.KEY_PREF_WIDGET_FILTER)) {
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putInt(Constants.PreferencesKeys.CATEGORY_INDEX_WIDGET,
					STARTING_CATEGORY_INDEX);
			editor.putInt(Constants.PreferencesKeys.IMAGE_INDEX_WIDGET,
					STARTING_CATEGORY_INDEX);
			editor.commit();
			return true;
		}
		if (key.equals(Constants.PreferencesKeys.KEY_PREF_NOTIFICATION_FILTER)) {
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putInt(
					Constants.PreferencesKeys.CATEGORY_INDEX_NOTIFICATION,
					STARTING_CATEGORY_INDEX);
			editor.putInt(Constants.PreferencesKeys.IMAGE_INDEX_NOTIFICATION,
					STARTING_CATEGORY_INDEX);
			editor.commit();
			return true;
		}
		return false;
	}

	private boolean checkActivePreference(SharedPreferences sharedPreferences,
			String key) {
		AlarmController alarmController = new AlarmController();
		if (key.equals(Constants.PreferencesKeys.KEY_PREF_WALLPAPER_ACTIVE)) {
			if (sharedPreferences.getBoolean(key, false)) {
				alarmController.setupBroadCastReceiver(context);
				alarmController
						.setupAlarmByID(
								context,
								Constants.ServicesID.WALLPAPER_ALARM_ID,
								alarmController
										.getRepeatTimeInMillSec(
												context,
												Constants.PreferencesKeys.KEY_PREF_WALLPAPER_TIME));
			} else {
				// Cancel any error notifications.
				NotificationManager mNotifyMgr = (NotificationManager) context
						.getSystemService(Context.NOTIFICATION_SERVICE);
				mNotifyMgr
						.cancel(Constants.NotificationIDs.NOTIFICATION_WALLPAPER_ERROR);
				alarmController.cancelAlarmByID(context,
						Constants.ServicesID.WALLPAPER_ALARM_ID);
				alarmController.checkBroadCastReceiver(context);
			}
			return true;
		}
		if (key.equals(Constants.PreferencesKeys.KEY_PREF_NOTIFICATION_ACTIVE)) {
			if (sharedPreferences.getBoolean(key, false)) {
				alarmController.setupBroadCastReceiver(context);
				alarmController
						.setupAlarmByID(
								context,
								Constants.ServicesID.NOTIFICATION_ALARM_ID,
								alarmController
										.getRepeatTimeInMillSec(
												context,
												Constants.PreferencesKeys.KEY_PREF_NOTIFICATION_TIME));
			} else {
				// Gets an instance of the NotificationManager service
				NotificationManager mNotifyMgr = (NotificationManager) context
						.getSystemService(Context.NOTIFICATION_SERVICE);
				mNotifyMgr.cancel(Constants.NotificationIDs.NOTIFICATION_NOTIF_ERROR);
				alarmController.cancelAlarmByID(context,
						Constants.ServicesID.NOTIFICATION_ALARM_ID);
				alarmController.checkBroadCastReceiver(context);
			}
			return true;
		}
		return false;
	}

}