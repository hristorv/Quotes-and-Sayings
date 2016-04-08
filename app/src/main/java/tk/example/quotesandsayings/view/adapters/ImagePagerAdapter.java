package tk.example.quotesandsayings.view.adapters;

import tk.example.quotesandsayings.R;
import tk.example.quotesandsayings.model.Image;
import tk.example.quotesandsayings.touchimage.FastBitmapDrawable;
import tk.example.quotesandsayings.utils.ScaleBitmapProcessor;
import tk.example.quotesandsayings.view.CustomViewPager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class ImagePagerAdapter extends PagerAdapter {

	Image[] images;
	int imagePosition;
	private LayoutInflater inflater;
	DisplayImageOptions options;
	private Context context;
	CustomViewPager viewPager;
	private View mCurrentView;

	public ImagePagerAdapter(Context context, Image[] images,
			int imagePosition, CustomViewPager viewPager) {
		inflater = LayoutInflater.from(context);
		this.viewPager = viewPager;
		this.context = context;
		this.images = images;
		this.imagePosition = imagePosition;
		initializeDisplayOptions(context);
	}

	private void initializeDisplayOptions(Context context) {
		options = new DisplayImageOptions.Builder()
				// TODO
				// .showImageForEmptyUri(R.drawable.ic_empty)
				// .showImageOnFail(R.drawable.ic_action_error)
				.resetViewBeforeLoading(true)
				.postProcessor(new ScaleBitmapProcessor(context))
				.imageScaleType(ImageScaleType.EXACTLY).delayBeforeLoading(0)
				.displayer(new FadeInBitmapDisplayer(500)).build();
	}

	@Override
	public int getCount() {
		if (images != null)
			return images.length;
		return 0;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public Object instantiateItem(ViewGroup view, int position) {
		imagePosition = position;
		View imageLayout = inflater.inflate(R.layout.fragment_image_fullscreen,
				view, false);
		assert imageLayout != null;
		ImageView imageView = (ImageView) imageLayout
				.findViewById(R.id.image_hq);
		final ProgressBar spinner = (ProgressBar) imageLayout
				.findViewById(R.id.loading);
		getImage(position, imageView, spinner);
		view.addView(imageLayout, 0);
		return imageLayout;
	}

	/**
	 * Create global configuration and initialize ImageLoader with this
	 * configuration.
	 */
	private void initImageLoader() {
		if (!ImageLoader.getInstance().isInited()) {
			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
					context).writeDebugLogs()
					.tasksProcessingOrder(QueueProcessingType.LIFO).build();
			ImageLoader.getInstance().init(config);
		}
	}

	private void getImage(final int position, ImageView imageView,
			final ProgressBar spinner) {
		initImageLoader();

		ImageLoader.getInstance().displayImage(getHQimageURL(position),
				imageView, options, new SimpleImageLoadingListener() {

					@Override
					public void onLoadingStarted(String imageUri, View view) {
						spinner.setVisibility(View.VISIBLE);
						Log.e("Started: " + position,"Loader");
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						String message = null;
						boolean show = false;
						switch (failReason.getType()) {
						case IO_ERROR:
							message = "Connection error";
							show = true;
							break;
						case DECODING_ERROR:
							message = "Image can't be decoded";
							break;
						case NETWORK_DENIED:
							message = "Downloads are denied";
							break;
						case OUT_OF_MEMORY:
							message = "Out Of Memory error";
							break;
						case UNKNOWN:
							message = "Unknown error";
							break;
						}
						if (message != null && show)
							Snackbar.make(view, message, Snackbar.LENGTH_LONG)
									.show();
						spinner.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						spinner.setVisibility(View.GONE);
						Log.e("Loaded: " + position, "Loader");
					}
				});
	}

	private String getHQimageURL(int position) {
		StringBuilder hq_url = new StringBuilder(images[position].getUrl());
		hq_url.delete(hq_url.length() - 7, hq_url.length() - 4);
		return hq_url.toString();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view.equals(object);
	}

	@Override
	public void restoreState(Parcelable state, ClassLoader loader) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		mCurrentView = (View) object;
		super.setPrimaryItem(container, position, object);
	}

	public Bitmap getCurrentBitmap() {
		ImageView currentImageView = (ImageView) mCurrentView
				.findViewById(R.id.image_hq);
		FastBitmapDrawable bd = (FastBitmapDrawable) currentImageView
				.getDrawable();
		if (bd == null)
			return null;
		Bitmap bmp = bd.getBitmap();
		return bmp;
	}

	public View getBaseView() {
		return mCurrentView;
	}
}
