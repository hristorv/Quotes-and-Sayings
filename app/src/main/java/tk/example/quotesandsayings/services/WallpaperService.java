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
import tk.example.quotesandsayings.controller.ImageController;
import tk.example.quotesandsayings.model.Album;
import tk.example.quotesandsayings.model.AlbumsData;
import tk.example.quotesandsayings.model.Constants;
import tk.example.quotesandsayings.model.Image;
import tk.example.quotesandsayings.utils.ScaleBitmapProcessor;
import android.app.IntentService;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class WallpaperService extends BaseService {
	private static final String DEFAULT_RECOMMENDED = "Default (Recommended)";
	private static final String serviceTag = "Wallpaper Service";
	public static final String KEY_PREF_WALLPAPER_FILTER = "pref_wallapper_filter";
	int categoryIndex;
	String curCategory;
	int imageIndex;
	SharedPreferences prefs;
	String[] filterArray;
	private Context context;

	public WallpaperService() {
		super(serviceTag);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		this.context = this;
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		// Get the category index from the preferences
		categoryIndex = prefs.getInt(
				Constants.PreferencesKeys.CATEGORY_INDEX_WALLPAPER,
				INDEX_DEFAULT_VALUE);
		imageIndex = prefs.getInt(
				Constants.PreferencesKeys.IMAGE_INDEX_WALLPAPER,
				INDEX_DEFAULT_VALUE);

		Set<String> filterSet = prefs.getStringSet(KEY_PREF_WALLPAPER_FILTER,
				null);
		filterArray = filterSet.toArray(new String[filterSet.size()]);
		if (filterArray.length > 0) {
			curCategory = filterArray[categoryIndex];
			new GetCategoryJson(this, curCategory).execute();
		}

	}

	private void updateIndexes(int categoryArraySize) {
		if (imageIndex == categoryArraySize - 1) {
			SharedPreferences.Editor editor = prefs.edit();
			editor.putInt(Constants.PreferencesKeys.IMAGE_INDEX_WALLPAPER,
					INDEX_DEFAULT_VALUE);
			editor.commit();
			int nextCategoryIndex;
			if (categoryIndex != filterArray.length - 1) {
				nextCategoryIndex = categoryIndex + 1;
			} else {
				nextCategoryIndex = INDEX_DEFAULT_VALUE;
			}
			editor.putInt(Constants.PreferencesKeys.CATEGORY_INDEX_WALLPAPER,
					nextCategoryIndex);
			editor.commit();
		} else {
			SharedPreferences.Editor editor = prefs.edit();
			editor.putInt(Constants.PreferencesKeys.IMAGE_INDEX_WALLPAPER,
					imageIndex + 1);
			editor.commit();
		}

	}

	private void setImageWallpaper(Bitmap loadedBitmap) {
		WallpaperManager wallpaperManager = WallpaperManager
				.getInstance(context);
		String scaling = prefs.getString(
				Constants.PreferencesKeys.KEY_PREF_WALLPAPER_SCALING,
				DEFAULT_RECOMMENDED);
		try {
			if (loadedBitmap != null) {
				wallpaperManager.setBitmap(new ImageController()
						.getScaledBitmap(this, loadedBitmap, scaling));
			}
		} catch (IOException e) {
			e.printStackTrace();
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
							new ServiceController().makeErrorNotification(
									context, ServiceController.WALLPAPER);
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							if (loadedImage != null) {
								setImageWallpaper(loadedImage);
								updateIndexes(categoryArray.length);
							} else {
								new ServiceController().makeErrorNotification(
										context, ServiceController.WALLPAPER);
							}
						}
					});
		}
	}

}
