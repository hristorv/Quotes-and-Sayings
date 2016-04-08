package tk.example.quotesandsayings.services;

import tk.example.quotesandsayings.R;
import tk.example.quotesandsayings.controller.AlarmReceiver;
import tk.example.quotesandsayings.controller.MyAppWidgetProvider;
import tk.example.quotesandsayings.model.Constants;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;

public class ServiceController {
	public static final int NOTIFICATION = 0;
	public static final int WALLPAPER = 1;

	public void addErrorMessage(Context context) {
		RemoteViews views = new RemoteViews(context.getPackageName(),
				R.layout.appwidget);
		views.setImageViewBitmap(R.id.widget_image, null);
		views.setViewVisibility(R.id.widget_text_error, View.VISIBLE);
		// Set the error message intent.
		int ID = Constants.ServicesID.WIDGET_ALARM_ID;
		Intent errorIntent = new Intent(context, AlarmReceiver.class);
		errorIntent.putExtra(Constants.Extra.SERVICE_TAG_KEY, ID);
		PendingIntent pintent = PendingIntent.getBroadcast(context, ID,
				errorIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setOnClickPendingIntent(R.id.widget_text_error, pintent);
		ComponentName thisWidget = new ComponentName(context,
				MyAppWidgetProvider.class);
		AppWidgetManager manager = AppWidgetManager.getInstance(context);
		manager.updateAppWidget(thisWidget, views);
	}

	public void removeErrorMessage(Context context) {
		RemoteViews views = new RemoteViews(context.getPackageName(),
				R.layout.appwidget);
		views.setViewVisibility(R.id.widget_text_error, View.GONE);
		ComponentName thisWidget = new ComponentName(context,
				MyAppWidgetProvider.class);
		AppWidgetManager manager = AppWidgetManager.getInstance(context);
		manager.updateAppWidget(thisWidget, views);
	}

	public void removeErrorNotification(Context context, int ID) {
		// Gets an instance of the NotificationManager service
		NotificationManager mNotifyMgr = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotifyMgr.cancel(ID);
	}

	public void makeErrorNotification(Context context, int serviceType) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				context)
				.setLargeIcon(
						BitmapFactory.decodeResource(context.getResources(),
								R.drawable.ic_launcher))
				.setColor(
						context.getResources().getColor(R.color.primary_color))
				.setOnlyAlertOnce(true)
				.setSmallIcon(R.drawable.ic_notification)
				.setContentTitle(
						context.getString(R.string.notification_conn_error_title))
				.setTicker(
						context.getString(R.string.notification_conn_error_title))
				.setContentText(
						context.getString(R.string.notification_conn_error_subtitle));
		int ID;
		if (serviceType == WALLPAPER) {
			ID = Constants.ServicesID.WALLPAPER_ALARM_ID;
		} else {
			ID = Constants.ServicesID.NOTIFICATION_ALARM_ID;
		}
		// Set intent thats called when you click on the message.
		Intent intent = new Intent(context, AlarmReceiver.class);
		intent.putExtra(Constants.Extra.SERVICE_TAG_KEY, ID);
		PendingIntent pintent = PendingIntent.getBroadcast(context, ID, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(pintent);

		// Sets an ID for the notification
		int mNotificationId;
		if (serviceType == WALLPAPER) {
			mNotificationId = Constants.NotificationIDs.NOTIFICATION_WALLPAPER_ERROR;
		} else {
			mNotificationId = Constants.NotificationIDs.NOTIFICATION_NOTIF_ERROR;
		}
		// Gets an instance of the NotificationManager service
		NotificationManager mNotifyMgr = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		// Builds the notification and issues it.
		mNotifyMgr.notify(mNotificationId, mBuilder.build());
	}

}
