package tk.example.quotesandsayings.view;

import tk.example.quotesandsayings.R;
import tk.example.quotesandsayings.model.Album;
import tk.example.quotesandsayings.model.AlbumsData;
import tk.example.quotesandsayings.model.ImageData;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class SystemUI {

	private static final double TABLET_MINIMUM_SIZE = 6.5;
	private static final int MAX_CHARS_ALBUM_NAME = 10;
	private static final int MIN_NAME_SIZE = 3;
	private static final int DEFAULT_GRID_COLUMNS_LARGE = 1;
	private static final int DEFAULT_GRID_COLUMNS_SMALL = 2;

	/** Hides StatusBar and NavigationBar */
	public void hideSystemUi(Context context) {
		if (isLandscape(context)) {
			// Set flags for hiding status bar and navigation bar
			View decorView = ((Activity) context).getWindow().getDecorView();
			// // Hide the status bar.
			// int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
			// | View.SYSTEM_UI_FLAG_FULLSCREEN
			// | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
			// | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
			// | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
			int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
					| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
			decorView.setSystemUiVisibility(uiOptions);

			Window window = ((Activity) context).getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
			((AppCompatActivity) context).getSupportActionBar().hide();

		}
	}

	public void showSystemUi(Context context) {
		View decorView = ((Activity) context).getWindow().getDecorView();
		Window window = ((Activity) context).getWindow();
		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
		// | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
		// decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
		// | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
		window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
		((AppCompatActivity) context).getSupportActionBar().show();
		// getHandler().postDelayed(navHider, UI_VISIBILITY_TIME);

	}

	public boolean isLandscape(Context context) {
		if (((Activity) context).getResources().getConfiguration().orientation == 2)
			return true;
		return false;
	}

	public void setupFloatingLabelError(final Context context,
			final MaterialDialog materialDialog) {
		materialDialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
		final TextInputLayout floatingUsernameLabel = (TextInputLayout) materialDialog
				.findViewById(R.id.text_input_layout);
		floatingUsernameLabel.getEditText().addTextChangedListener(
				new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence text, int start,
							int count, int after) {
						floatingUsernameLabel.setErrorEnabled(false);
						// Check for maximum length.
						if (text.length() > MAX_CHARS_ALBUM_NAME) {
							floatingUsernameLabel
									.setError(context
											.getResources()
											.getString(
													(R.string.album_warning_input_max_length)));
							floatingUsernameLabel.setErrorEnabled(true);
							materialDialog.getActionButton(
									DialogAction.POSITIVE).setEnabled(false);
							return;
						}
						// Check for minimum name length.
						if (text.length() < MIN_NAME_SIZE) {
							floatingUsernameLabel
									.setError(context
											.getResources()
											.getString(
													(R.string.album_warning_input_min_length)));
							floatingUsernameLabel.setErrorEnabled(true);
							materialDialog.getActionButton(
									DialogAction.POSITIVE).setEnabled(false);
							return;
						}
						// Check if album name is not taken.
						for (Album album : AlbumsData.getInstance().getAlbums()) {
							if (album.getName().equals(text.toString())) {
								floatingUsernameLabel
										.setError(context
												.getResources()
												.getString(
														R.string.album_warning_input_name_taken));
								floatingUsernameLabel.setErrorEnabled(true);
								materialDialog.getActionButton(
										DialogAction.POSITIVE)
										.setEnabled(false);
								return;
							}
						}
						materialDialog.getActionButton(DialogAction.POSITIVE)
								.setEnabled(true);
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {
						// TODO Auto-generated method stub

					}

					@Override
					public void afterTextChanged(Editable s) {
						// TODO Auto-generated method stub

					}
				});
	}

	public void adjustGridColumnNum(Context context, RecyclerView grid,
			boolean isLargeGridItems) {
		int numColumns;
		if (isLargeGridItems) {
			numColumns = DEFAULT_GRID_COLUMNS_LARGE;
		} else {
			numColumns = DEFAULT_GRID_COLUMNS_SMALL;
		}
		DisplayMetrics metrics = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(metrics);
		int width = metrics.widthPixels;
		int height = metrics.heightPixels;
		int dens = metrics.densityDpi;
		double wi = (double) width / (double) dens;
		double hi = (double) height / (double) dens;
		double x = Math.pow(wi, 2);
		double y = Math.pow(hi, 2);
		double screenInches = Math.sqrt(x + y);
		if (screenInches > TABLET_MINIMUM_SIZE) {
			numColumns += 1;
		}

		int orientation = context.getResources().getConfiguration().orientation;
		if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
			numColumns += 1;
		}
		GridLayoutManager manager = (GridLayoutManager) grid.getLayoutManager();
		manager.setSpanCount(numColumns);
	}


}
