package tk.example.quotesandsayings.view.fragments;

import tk.example.quotesandsayings.R;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class HelpFragment extends DialogFragment {

	int numOfPages = 3;

	int[] res = { R.drawable.wallpaper_help, R.drawable.widget_help,
			R.drawable.notifications_help };

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setRequestedOrientation(
				ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setStyle(DialogFragment.STYLE_NORMAL, R.style.FullscreenDialog);
	}

	@Override
	public void onStart() {
		super.onStart();
		Dialog dialog = getDialog();
		if (dialog != null) {
			int width = ViewGroup.LayoutParams.MATCH_PARENT;
			int height = ViewGroup.LayoutParams.MATCH_PARENT;
			dialog.getWindow().setLayout(width, height);
		}
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_help, container,
				false);
		ViewPager pager = (ViewPager) rootView.findViewById(R.id.help_pager);
		Button button = (Button) rootView.findViewById(R.id.help_button);
		ImageView iconFirst = (ImageView) rootView.findViewById(R.id.icnFirst);
		ImageView iconSecond = (ImageView) rootView
				.findViewById(R.id.icnSecond);
		ImageView iconThird = (ImageView) rootView.findViewById(R.id.icnThird);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				HelpFragment.this.dismiss();
			}
		});
		setPagerListener(pager, iconFirst, iconSecond, iconThird);

		pager.setAdapter(new ScreenSlidePagerAdapter());

		return rootView;
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		if (getActivity() != null)
			getActivity().setRequestedOrientation(
					ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		super.onDismiss(dialog);
	}

	private void setPagerListener(ViewPager pager, final ImageView iconFirst,
			final ImageView iconSecond, final ImageView iconThird) {
		pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				if (position == 0) {
					iconFirst
							.setImageResource(R.drawable.ic_radio_button_on_white_18dp);
					iconSecond
							.setImageResource(R.drawable.ic_radio_button_off_white_18dp);
					iconThird
							.setImageResource(R.drawable.ic_radio_button_off_white_18dp);
				}
				if (position == 1) {
					iconFirst
							.setImageResource(R.drawable.ic_radio_button_off_white_18dp);
					iconSecond
							.setImageResource(R.drawable.ic_radio_button_on_white_18dp);
					iconThird
							.setImageResource(R.drawable.ic_radio_button_off_white_18dp);
				}
				if (position == 2) {
					iconFirst
							.setImageResource(R.drawable.ic_radio_button_off_white_18dp);
					iconSecond
							.setImageResource(R.drawable.ic_radio_button_off_white_18dp);
					iconThird
							.setImageResource(R.drawable.ic_radio_button_on_white_18dp);
				}
			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
	}

	private class ScreenSlidePagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return numOfPages;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView image = new ImageView(getActivity());
			image.setScaleType(ScaleType.CENTER_INSIDE);
			ViewPager.LayoutParams params = new ViewPager.LayoutParams();
			params.width = ViewPager.LayoutParams.MATCH_PARENT;
			params.height = ViewPager.LayoutParams.MATCH_PARENT;
			image.setLayoutParams(params);
			image.setImageResource(res[position]);
			((ViewPager) container).addView(image);
			return image;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

	}

}
