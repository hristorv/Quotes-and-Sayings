package tk.example.quotesandsayings.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import tk.example.quotesandsayings.controller.ImageController;
import tk.example.quotesandsayings.model.Image;
import tk.example.quotesandsayings.model.ImageData;
import tk.example.quotesandsayings.touchimage.ImageViewTouch;
import android.app.Activity;
import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar.OnMenuVisibilityListener;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.gson.Gson;

public class CustomViewPager extends ViewPager implements
		ViewPager.PageTransformer {

	private static final int UI_VISIBILITY_TIME = 3000;
	public static final String VIEW_PAGER_OBJECT_TAG = "image#";
	private int previousPosition;
	private OnPageSelectedListener onPageSelectedListener;
	private Context context;
	private MenuItem likeItem;
	private Image[] images;
	private TextView likesView;
	private boolean isShowing;
	private GestureDetector detector;

	public CustomViewPager(Context context) {
		super(context);
		this.context = context;
		init();
	}

	public CustomViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	public void setOnPageSelectedListener(OnPageSelectedListener listener) {
		onPageSelectedListener = listener;
	}

	@Override
	protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
		if (v instanceof ImageViewTouch) {
			return ((ImageViewTouch) v).canScroll(dx);
		} else {
			return super.canScroll(v, checkV, dx, x, y);
		}
	}

	public interface OnPageSelectedListener {

		public void onPageSelected(int position);

	}

	private void init() {
		detector = new GestureDetector(context, new GestureTap());
		previousPosition = getCurrentItem();
		this.setPageTransformer(true, this);
		AppCompatActivity activity = (AppCompatActivity) context;
//		activity.getSupportActionBar().addOnMenuVisibilityListener(
//				new OnMenuVisibilityListener() {
//
//					@Override
//					public void onMenuVisibilityChanged(boolean isVisible) {
//						if (isVisible)
//							delayHide();
//					}
//				});

		addOnPageChangeListener(new SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				if (onPageSelectedListener != null) {
					onPageSelectedListener.onPageSelected(position);
				}
				adjustLikeButton(position);
			}

			@Override
			public void onPageScrollStateChanged(int state) {
				if (state == SCROLL_STATE_SETTLING
						&& previousPosition != getCurrentItem()) {
					try {
						ImageViewTouch imageViewTouch = (ImageViewTouch) findViewWithTag(VIEW_PAGER_OBJECT_TAG
								+ getCurrentItem());
						if (imageViewTouch != null) {
							imageViewTouch.zoomTo(1f, 300);
						}

						previousPosition = getCurrentItem();
					} catch (ClassCastException ex) {
						ex.printStackTrace();
					}
				}
			}
		});
		//setVisibilityChangeListener();
	}

	protected void adjustLikeButton(int position) {
		Image curImage = images[position];
		likesView.setText(curImage.getLikes());
		Image[] favoritesArray;
		if (ImageData.getInstance().getImagesFavorites() != null) {
			favoritesArray = ImageData.getInstance().getImagesFavorites();
		} else {
			favoritesArray = new ImageController().getFavorites(context);
		}
		for (Image image : favoritesArray) {
			if (image.getUrl().equals(curImage.getUrl())) {
				setLikeEnabled(false);
				return;
			}
		}
		setLikeEnabled(true);
	}

	private void setLikeEnabled(boolean enabled) {
		likeItem.setEnabled(enabled);
	}

	public void setLikeItem(MenuItem likeItem) {
		this.likeItem = likeItem;
	}

	public void setImages(Image[] images) {
		this.images = images;
	}

	Runnable navHider = new Runnable() {
		@Override
		public void run() {
			new SystemUI().hideSystemUi(context);
			isShowing = false;
		}

	};

//	private void delayHide() {
//		getHandler().removeCallbacks(navHider, null);
//		getHandler().postDelayed(navHider, UI_VISIBILITY_TIME);
//	}

//	private void setVisibilityChangeListener() {
//		View decorView = ((Activity) context).getWindow().getDecorView();
//		decorView
//				.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
//					@Override
//					public void onSystemUiVisibilityChange(int visibility) {
//						// Note that system bars will only be "visible" if none
//						// of the
//						// LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are
//						// set.
//						SystemUI systemUI = new SystemUI();
//						if (systemUI.isLandscape(context)) {
//							if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
//								getHandler().postDelayed(navHider,
//										UI_VISIBILITY_TIME);
//							}
//						}
//					}
//				});
//	}

	public void setLikeView(TextView likesView) {
		this.likesView = likesView;

	}

	@Override
	public void transformPage(View view, float position) {
		final float MIN_SCALE = 0.85f;
		final float MIN_ALPHA = 0.5f;
		int pageWidth = view.getWidth();
		int pageHeight = view.getHeight();
		if (position < -1) { // [-Infinity,-1)
			// This page is way off-screen to the left.
			view.setAlpha(0);
		} else if (position <= 1) { // [-1,1]
			// Modify the default slide transition to shrink the page as well
			float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
			float vertMargin = pageHeight * (1 - scaleFactor) / 2;
			float horzMargin = pageWidth * (1 - scaleFactor) / 2;
			if (position < 0) {
				view.setTranslationX(horzMargin - vertMargin / 2);
			} else {
				view.setTranslationX(-horzMargin + vertMargin / 2);
			}
			// Scale the page down (between MIN_SCALE and 1)
			view.setScaleX(scaleFactor);
			view.setScaleY(scaleFactor);
			// Fade the page relative to its size.
			view.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE)
					/ (1 - MIN_SCALE) * (1 - MIN_ALPHA));
		} else { // (1,+Infinity]
			// This page is way off-screen to the right.
			view.setAlpha(0);
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		detector.onTouchEvent(event);
		return super.onInterceptTouchEvent(event);

	}

	class GestureTap extends GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			SystemUI systemUI = new SystemUI();
			if (isShowing) {
				systemUI.hideSystemUi(context);
				isShowing = false;
			} else {
				systemUI.showSystemUi(context);
				isShowing = true;
			}
			return true;
		}
	}

}