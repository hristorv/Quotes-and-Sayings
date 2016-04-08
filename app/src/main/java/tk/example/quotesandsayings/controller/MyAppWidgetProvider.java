package tk.example.quotesandsayings.controller;

import tk.example.quotesandsayings.R;
import tk.example.quotesandsayings.model.Constants;
import tk.example.quotesandsayings.model.Image;
import tk.example.quotesandsayings.model.Constants.Extra;
import tk.example.quotesandsayings.model.Constants.PreferencesKeys;
import tk.example.quotesandsayings.model.Constants.ServicesID;
import tk.example.quotesandsayings.view.activities.ImageActivity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

public class MyAppWidgetProvider extends AppWidgetProvider {
	private Context context;
	SharedPreferences prefs;
	private int imagePosition;
	private Image[] imageArray;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getIntExtra(Constants.Extra.IMAGE_POSITION, -1) != -1) {
			imagePosition = intent.getIntExtra(Constants.Extra.IMAGE_POSITION,
					-1);
		}
		if (intent.getParcelableArrayExtra(Constants.Extra.IMAGE_ARRAY) != null) {
			Parcelable[] ps = intent
					.getParcelableArrayExtra(Constants.Extra.IMAGE_ARRAY);
			imageArray = new Image[ps.length];
			System.arraycopy(ps, 0, imageArray, 0, ps.length);
		}
		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);

		final int N = appWidgetIds.length;

		// Perform this loop procedure for each App Widget that belongs to this
		// provider
		for (int i = 0; i < N; i++) {
			int appWidgetId = appWidgetIds[i];
			
			// Create an Intent to launch ImageActivity
			Intent intent = new Intent(context, ImageActivity.class);
			intent.putExtra(Constants.Extra.IMAGE_POSITION, imagePosition);
			intent.putExtra(Constants.Extra.IMAGE_ARRAY, imageArray);
			intent.putExtra(Constants.Extra.IS_FROM_WIDGET, true);

			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
					intent, PendingIntent.FLAG_UPDATE_CURRENT);

			// Get the layout for the App Widget and attach an on-click listener
			// to the image
			RemoteViews views = new RemoteViews(context.getPackageName(),
					R.layout.appwidget);
			views.setOnClickPendingIntent(R.id.widget_image, pendingIntent);
			
//			// Set the error message intent.
//			int ID = Constants.ServicesID.WIDGET_ALARM_ID;
//			Intent errorIntent = new Intent(context, AlarmReceiver.class);
//			errorIntent.putExtra(Constants.Extra.SERVICE_TAG_KEY, ID);
//			PendingIntent pintent = PendingIntent.getBroadcast(context, ID, errorIntent,
//					PendingIntent.FLAG_UPDATE_CURRENT);
//			views.setOnClickPendingIntent(R.id.widget_text_error, pintent);
			
			// Tell the AppWidgetManager to perform an update on the current app
			// widget
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}
	}

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
	}
	

	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		this.context = context;
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		AlarmController alarmController = new AlarmController();
		alarmController.cancelAlarmByID(context, Constants.ServicesID.WIDGET_ALARM_ID);
		alarmController.checkBroadCastReceiver(context);
		SharedPreferences prefs = context.getSharedPreferences(
				Constants.Extra.DEFAULT_PREFS_NAME, Context.MODE_PRIVATE);
		prefs.edit().putBoolean(Constants.PreferencesKeys.KEY_PREF_WIDGET_ACTIVE, false)
				.commit();
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {		
		clearPhantomWidgets(context);	
		super.onDeleted(context, appWidgetIds);
	}

	private void clearPhantomWidgets(Context context) {
		this.context = context;
		int visibleWidgetCount=0;
		prefs=PreferenceManager.getDefaultSharedPreferences(context);
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		int[] allAppWidgetIDs = appWidgetManager.getAppWidgetIds(new ComponentName(context, MyAppWidgetProvider.class));
		for (int i = 0; i < allAppWidgetIDs.length; i++) {
			int id = allAppWidgetIDs[i];
			String key = ("appwidget"+id+"_configured");
			if (prefs.getBoolean(key, false)) 
				visibleWidgetCount+=1;
		}
		if (visibleWidgetCount<1) {
			deleteWidgets();
		}
	}

	private void deleteWidgets() {
		ComponentName cn = new ComponentName(context, MyAppWidgetProvider.class);
		AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(context);
		int[] appWidgetIds1 = appWidgetManager.getAppWidgetIds(cn);
		AppWidgetHost host = new AppWidgetHost(context, 0);
		for (int appWidgetId : appWidgetIds1) {
			host.deleteAppWidgetId(appWidgetId);
		}
	}

}
