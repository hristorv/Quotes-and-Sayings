package tk.example.quotesandsayings.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;

import tk.example.quotesandsayings.R;
import tk.example.quotesandsayings.model.Album;
import tk.example.quotesandsayings.model.AlbumsData;
import tk.example.quotesandsayings.model.Constants;
import tk.example.quotesandsayings.model.Image;
import tk.example.quotesandsayings.utils.ScaleBitmapProcessor;
import tk.example.quotesandsayings.view.activities.ImageActivity;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class NotificationService extends BaseService {
	private static final String serviceTag = "Notification Service";
	public static final String KEY_PREF_NOTIFICATION_FILTER = "pref_notification_filter";
	int categoryIndex;
	String curCategory;
	int imageIndex;
	SharedPreferences prefs;
	String[] filterArray;
	private Context context;

	public NotificationService() {
		super(serviceTag);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		this.context = this;
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		// Get the category index from the preferences
		categoryIndex = prefs.getInt(
				Constants.PreferencesKeys.CATEGORY_INDEX_NOTIFICATION,
				INDEX_DEFAULT_VALUE);
		imageIndex = prefs.getInt(
				Constants.PreferencesKeys.IMAGE_INDEX_NOTIFICATION,
				INDEX_DEFAULT_VALUE);

		Set<String> filterSet = prefs.getStringSet(
				KEY_PREF_NOTIFICATION_FILTER, null);
		filterArray = filterSet.toArray(new String[filterSet.size()]);
		if (filterArray.length > 0) {
			curCategory = filterArray[categoryIndex];
			new GetCategoryJson(this, curCategory).execute();
		}
	}

	private void updateIndexes(int categoryArraySize) {
		if (imageIndex == categoryArraySize - 1) {
			SharedPreferences.Editor editor = prefs.edit();
			editor.putInt(Constants.PreferencesKeys.IMAGE_INDEX_NOTIFICATION,
					INDEX_DEFAULT_VALUE);
			editor.commit();
			int nextCategoryIndex;
			if (categoryIndex != filterArray.length - 1) {
				nextCategoryIndex = categoryIndex + 1;
			} else {
				nextCategoryIndex = INDEX_DEFAULT_VALUE;
			}
			editor.putInt(
					Constants.PreferencesKeys.CATEGORY_INDEX_NOTIFICATION,
					nextCategoryIndex);
			editor.commit();
		} else {
			SharedPreferences.Editor editor = prefs.edit();
			editor.putInt(Constants.PreferencesKeys.IMAGE_INDEX_NOTIFICATION,
					imageIndex + 1);
			editor.commit();
		}

	}

	private void setImageNotification(Image[] categoryArray, Bitmap loadedBitmap) {
		if (loadedBitmap != null) {
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					context)
					.setLargeIcon(
							BitmapFactory.decodeResource(
									context.getResources(),
									R.drawable.ic_launcher))
					.setAutoCancel(true)
					.setColor(getResources().getColor(R.color.primary_color))
					.setSmallIcon(R.drawable.ic_notification)
					.setContentTitle(
							context.getString(R.string.notification_text))
					.setTicker(context.getString(R.string.notification_ticker))
					.setSubText(
							context.getString(R.string.notification_sub_text))
					.setStyle(
							new NotificationCompat.BigPictureStyle()
									.bigPicture(loadedBitmap));
			// Set the intent for launching the full screen image activity.
			Intent intent = new Intent(context, ImageActivity.class);
			intent.putExtra(Constants.Extra.IMAGE_POSITION, imageIndex);
			intent.putExtra(Constants.Extra.IMAGE_ARRAY, categoryArray);
			intent.putExtra(Constants.Extra.IS_FROM_WIDGET, true);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
					intent, PendingIntent.FLAG_UPDATE_CURRENT);
			mBuilder.setContentIntent(pendingIntent);
			// Sets an ID for the notification
			int mNotificationId = Constants.NotificationIDs.NOTIFICATION_IMAGE;
			// Gets an instance of the NotificationManager service
			NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			// Builds the notification and issues it.
			mNotifyMgr.notify(mNotificationId, mBuilder.build());

		}
	}

	@Override
	void getCurImage(final Image[] categoryArray) {
		if (categoryArray != null && categoryArray.length > 0) {
			initImageLoader();
			initializeDisplayOptions();
			// Get the HQ image URL
			StringBuilder hq_url = new StringBuilder(
					categoryArray[imageIndex].getUrl());
			hq_url.delete(hq_url.length() - 7, hq_url.length() - 4);
			String curImageUrl = hq_url.toString();
			ImageLoader.getInstance().loadImage(curImageUrl, options,
					new SimpleImageLoadingListener() {

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
							new ServiceController().makeErrorNotification(context, ServiceController.NOTIFICATION);
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							if (loadedImage != null) {
								setImageNotification(categoryArray, loadedImage);
								updateIndexes(categoryArray.length);
							} else {
								new ServiceController().makeErrorNotification(context, ServiceController.NOTIFICATION);
							}
						}
					});
		}
	}

}
