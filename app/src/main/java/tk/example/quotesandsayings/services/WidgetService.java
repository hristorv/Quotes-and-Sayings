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
import tk.example.quotesandsayings.controller.MyAppWidgetProvider;
import tk.example.quotesandsayings.model.Album;
import tk.example.quotesandsayings.model.AlbumsData;
import tk.example.quotesandsayings.model.Constants;
import tk.example.quotesandsayings.model.Image;
import tk.example.quotesandsayings.utils.ScaleBitmapProcessor;
import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class WidgetService extends BaseService {
	private static final String serviceTag = "Widget Service";
	public static final String KEY_PREF_WIDGET_FILTER = "pref_widget_filter";
	int categoryIndex;
	String curCategory;
	int imageIndex;
	SharedPreferences prefs;
	String[] filterArray;
	private Context context;
	private String curLikes;

	public WidgetService() {
		super(serviceTag);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		this.context = this;
		// SharedPreferences sharedPref = this.getSharedPreferences(
		// DEFAULT_PREFS_NAME, Context.MODE_PRIVATE);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		// Get the category index from the preferences
		categoryIndex = prefs.getInt(
				Constants.PreferencesKeys.CATEGORY_INDEX_WIDGET,
				INDEX_DEFAULT_VALUE);
		imageIndex = prefs.getInt(Constants.PreferencesKeys.IMAGE_INDEX_WIDGET,
				INDEX_DEFAULT_VALUE);

		Set<String> filterSet = prefs
				.getStringSet(KEY_PREF_WIDGET_FILTER, null);
		filterArray = filterSet.toArray(new String[filterSet.size()]);
		if (filterArray.length > 0) {
			curCategory = filterArray[categoryIndex];
			new GetCategoryJson(this, curCategory).execute();
		}

	}

	private void updateIndexes(int categoryArraySize) {
		if (imageIndex == categoryArraySize - 1) {
			SharedPreferences.Editor editor = prefs.edit();
			editor.putInt(Constants.PreferencesKeys.IMAGE_INDEX_WIDGET,
					INDEX_DEFAULT_VALUE);
			editor.commit();
			int nextCategoryIndex;
			if (categoryIndex != filterArray.length - 1) {
				nextCategoryIndex = categoryIndex + 1;
			} else {
				nextCategoryIndex = INDEX_DEFAULT_VALUE;
			}
			editor.putInt(Constants.PreferencesKeys.CATEGORY_INDEX_WIDGET,
					nextCategoryIndex);
			editor.commit();
		} else {
			SharedPreferences.Editor editor = prefs.edit();
			editor.putInt(Constants.PreferencesKeys.IMAGE_INDEX_WIDGET,
					imageIndex + 1);
			editor.commit();
		}

	}

	private void setImageWidget(Bitmap loadedBitmap, Image[] categoryArray) {

		if (loadedBitmap != null) {
			/* get the handle on your widget */
			RemoteViews views = new RemoteViews(getPackageName(),
					R.layout.appwidget);
			/* replace the image */
			views.setImageViewBitmap(R.id.widget_image, loadedBitmap);
			views.setTextViewText(R.id.widget_likesBar, curLikes);
			// Send image array and image index
			Intent intent = new Intent(context, MyAppWidgetProvider.class);
			intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			intent.putExtra(Constants.Extra.IMAGE_POSITION, imageIndex);
			intent.putExtra(Constants.Extra.IMAGE_ARRAY, categoryArray);
			int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(
					new ComponentName(context, MyAppWidgetProvider.class));
			if (ids != null && ids.length > 0) {
				intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
				context.sendBroadcast(intent);
			}
			/* update your widget */
			ComponentName thisWidget = new ComponentName(context,
					MyAppWidgetProvider.class);
			AppWidgetManager manager = AppWidgetManager.getInstance(context);
			manager.updateAppWidget(thisWidget, views);

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
							new ServiceController().addErrorMessage(context);
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							if (loadedImage != null) {
								curLikes = categoryArray[imageIndex].getLikes();
								setImageWidget(loadedImage, categoryArray);
								updateIndexes(categoryArray.length);
							} else {
								new ServiceController().addErrorMessage(context);
							}
						}
					});
		}
	}
}
