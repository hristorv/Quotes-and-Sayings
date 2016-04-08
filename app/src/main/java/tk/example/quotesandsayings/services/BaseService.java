package tk.example.quotesandsayings.services;

import tk.example.quotesandsayings.model.Image;
import tk.example.quotesandsayings.utils.ScaleBitmapProcessor;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import android.app.IntentService;
import android.graphics.Bitmap;

public abstract class BaseService extends IntentService {
	protected static final int INDEX_DEFAULT_VALUE = 0;

	protected DisplayImageOptions options;

	public BaseService(String name) {
		super(name);
	}
	
	protected void initializeDisplayOptions() {
		options = new DisplayImageOptions.Builder()
				.resetViewBeforeLoading(true)
				.postProcessor(new ScaleBitmapProcessor(this))
				.imageScaleType(ImageScaleType.EXACTLY).delayBeforeLoading(0)
				.build();
	}

	/**
	 * Create global configuration and initialize ImageLoader with this
	 * configuration.
	 */
	protected void initImageLoader() {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				this).writeDebugLogs().build();
		ImageLoader.getInstance().init(config);
	}
	
	abstract void getCurImage(final Image[] categoryArray);


}
