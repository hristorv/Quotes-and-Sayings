package tk.example.quotesandsayings.view.activities;

import java.io.File;
import java.io.IOException;

import tk.example.quotesandsayings.controller.ImageController;
import tk.example.quotesandsayings.model.Constants;
import tk.example.quotesandsayings.model.ImageData;
import tk.example.quotesandsayings.view.SystemUI;
import tk.example.quotesandsayings.view.fragments.ImagePagerFragment;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.afollestad.materialdialogs.folderselector.FolderChooserDialog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class ImageActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		SystemUI systemUI = new SystemUI();
		if (systemUI.isLandscape(this)) {
			systemUI.hideSystemUi(this);
		}
		new ImageController().createFavoritesFile(this);
		//initImageLoader();
		ImagePagerFragment fr;
		if (getFragmentManager().findFragmentByTag("imageFragment") == null) {
			fr = new ImagePagerFragment();
			fr.setArguments(getIntent().getExtras());
			getFragmentManager().beginTransaction()
					.replace(android.R.id.content, fr, "imageFragment")
					.commit();
		} else {
			fr = (ImagePagerFragment) getFragmentManager().findFragmentByTag(
					"imageFragment");
			getFragmentManager().beginTransaction().replace(
					android.R.id.content, fr);

		}
	}

//	/**
//	 * Create global configuration and initialize ImageLoader with this
//	 * configuration.
//	 */
//	private void initImageLoader() {
//		if (!ImageLoader.getInstance().isInited()) {
//			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
//					this).writeDebugLogs()
//					.tasksProcessingOrder(QueueProcessingType.LIFO).build();
//			ImageLoader.getInstance().init(config);
//		}
//	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		SystemUI systemUI = new SystemUI();
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			systemUI.hideSystemUi(this);
		}
		if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			systemUI.showSystemUi(this);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		ImagePagerFragment fr;
		fr = new ImagePagerFragment();
		fr.setArguments(intent.getExtras());
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, fr, "imageFragment").commit();
		super.onNewIntent(intent);
	}

}
