package tk.example.quotesandsayings.utils;

import tk.example.quotesandsayings.model.Constants;
import tk.example.quotesandsayings.model.ImageData;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.BitmapShader;
import android.preference.PreferenceManager;
import android.support.v4.graphics.BitmapCompat;
import android.util.Log;

import com.nostra13.universalimageloader.core.process.BitmapProcessor;

public class ScaleBitmapProcessor implements BitmapProcessor {

	private int height;
	private int width;

	public ScaleBitmapProcessor(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		this.width = prefs.getInt(Constants.PreferencesKeys.WIDTH_PREF, 0);
		this.height = prefs.getInt(Constants.PreferencesKeys.HEIGHT_PREF, 0);
		getCalculatedDimension();
	}

	@Override
	public Bitmap process(Bitmap defaultBitmap) {
		if (defaultBitmap != null && width != 0 && height != 0) {
			Bitmap scaledBitmap;
			if (isPortrait(defaultBitmap)) {
				scaledBitmap = Bitmap.createScaledBitmap(defaultBitmap, height,
						width, true);
			} else {
				scaledBitmap = Bitmap.createScaledBitmap(defaultBitmap, width,
						height, true);
			}
			if (!defaultBitmap.equals(scaledBitmap))
				defaultBitmap.recycle();
			return scaledBitmap;
		}
		return defaultBitmap;
	}

	private boolean isPortrait(Bitmap defaultBitmap) {
		return defaultBitmap.getHeight() > defaultBitmap.getWidth();
	}

	private void getCalculatedDimension() {
		if (height > width) {
			int x = height;
			height = width;
			width = x;
		}
	}

}
