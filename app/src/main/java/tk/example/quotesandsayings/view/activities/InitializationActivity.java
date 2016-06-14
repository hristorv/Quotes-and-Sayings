package tk.example.quotesandsayings.view.activities;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

import tk.example.quotesandsayings.R;
import tk.example.quotesandsayings.controller.AlarmReceiver;
import tk.example.quotesandsayings.controller.ImageController;
import tk.example.quotesandsayings.model.AlbumsData;
import tk.example.quotesandsayings.model.Category;
import tk.example.quotesandsayings.model.Constants;
import tk.example.quotesandsayings.model.Image;
import tk.example.quotesandsayings.model.ImageData;
import tk.example.quotesandsayings.utils.ConnectionManager;
import tk.example.quotesandsayings.view.SystemUI;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.MeasureSpec;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class InitializationActivity extends Activity {

    private MaterialDialog offlineDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initialization);
        adjustNotNavBars();
        initImageLoader();
        checkScreenDimensions();
        new ImageController().createFavoritesFile(this);
        AlbumsData.getInstance().createAlbumsFile(this);
        downloadCategories();
    }

    private void cacheCategoryImages() {
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisk(true).bitmapConfig(Bitmap.Config.ARGB_8888)
                .delayBeforeLoading(0)
                .resetViewBeforeLoading(true)
                .build();
        for (int i = 0; i < ImageData.getInstance().getCategories().length; i++) {
            ImageLoader.getInstance().loadImage(ImageData.getInstance().getCategories()[i].getBackgroundUrl(), new ImageSize(100, 100), options, new SimpleImageLoadingListener());
        }
    }

    private void downloadCategories() {
        // if (ConnectionManager.isConnected(this)) {
        new GetJson().execute();
        //  } else {
        //      showOfflineDialog();
        // }
    }

    private void showOfflineDialog() {
        if (offlineDialog == null) {
            offlineDialog = new MaterialDialog.Builder(this).cancelable(false)
                    .title(R.string.no_connection_dialog_title)
                    .content(R.string.no_connection_dialog_content)
                    .positiveText(R.string.action_retry)
                    .negativeText(R.string.action_go_offline)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {

                        @Override
                        public void onClick(@NonNull MaterialDialog dialog,
                                            @NonNull DialogAction which) {
                            downloadCategories();
                        }
                    }).onNegative(new MaterialDialog.SingleButtonCallback() {

                        @Override
                        public void onClick(@NonNull MaterialDialog dialog,
                                            @NonNull DialogAction which) {
                            new ImageController()
                                    .setImageArraysDefault(InitializationActivity.this);
                            // Start MainMenuActivity and finish this one.
                            Intent i = new Intent(InitializationActivity.this,
                                    MainMenuActivity.class);
                            i.putExtra(Constants.Extra.IS_OFFLINE, true);
                            startActivity(i);
                            InitializationActivity.this.finish();
                        }
                    }).show();
        } else {
            offlineDialog.show();
        }
    }

    @Override
    protected void onDestroy() {
        if (offlineDialog != null && offlineDialog.isShowing()) {
            offlineDialog.dismiss();
        }
        super.onDestroy();
    }

    private void checkScreenDimensions() {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        if (!prefs.contains(Constants.PreferencesKeys.WIDTH_PREF))
            getScreenDimensions(prefs);
    }

    @SuppressLint("NewApi")
    public void getScreenDimensions(SharedPreferences prefs) {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        int width_real = 0;
        int height_real = 0;
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Point size_real = new Point();
            display.getRealSize(size_real);
            width_real = size_real.x;
            height_real = size_real.y;
            if (height_real > height) {
                int nav_bar_height = height_real - height;
                if (!isNavBarOnTheRight()) {
                    height = height_real;
                    width = width - nav_bar_height;
                }
            }
        }
        if (width_real == 0 || height_real == 0) {
            width_real = width;
            height_real = height;
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(Constants.PreferencesKeys.WIDTH_PREF, width);
        editor.putInt(Constants.PreferencesKeys.HEIGHT_PREF, height);
        editor.putInt(Constants.PreferencesKeys.REAL_WIDTH_PREF, width_real);
        editor.putInt(Constants.PreferencesKeys.REAL_HEIGHT_PREF, height_real);
        editor.commit();
    }

    @SuppressLint("NewApi")
    private boolean isNavBarOnTheRight() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        Point size_real = new Point();
        display.getRealSize(size_real);
        int height_real = size_real.y;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        return height_real == height;
    }

    private void adjustNotNavBars() {
        View decorView = getWindow().getDecorView();
        // Hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);
    }

    /**
     * Create global configuration and initialize ImageLoader with this
     * configuration.
     */
    private void initImageLoader() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this).writeDebugLogs()
                .tasksProcessingOrder(QueueProcessingType.LIFO).build();
        ImageLoader.getInstance().init(config);
    }

    private class GetJson extends AsyncTask<Void, Void, Void> {

        private static final int TIMEOUT = 5000;

        @Override
        protected Void doInBackground(Void... params) {
          //  try {

                //getCategories();
                Category[] categories = new Category[10];
                categories[0] = new Category("Favorites", new Image[0], "drawable://" + R.drawable.cat_favourites);
                categories[1] = new Category("Top", new Image[0], "drawable://" + R.drawable.cat_top);
                categories[2] = new Category("Funny", new Image[0], "drawable://" + R.drawable.cat_funny);
                categories[3] = new Category("Happiness", new Image[0], "drawable://" + R.drawable.cat_happiness);
                categories[4] = new Category("Inspirational", new Image[0], "drawable://" + R.drawable.cat_inspirational);
                categories[5] = new Category("Life", new Image[0], "drawable://" + R.drawable.cat_life);
                categories[6] = new Category("Love", new Image[0], "drawable://" + R.drawable.cat_love);
                categories[7] = new Category("Success", new Image[0], "drawable://" + R.drawable.cat_success);
                categories[8] = new Category("Travel", new Image[0], "drawable://" + R.drawable.cat_travel);
                categories[9] = new Category("Wisdom", new Image[0], "drawable://" + R.drawable.cat_wisdom);


                Image[] imagesTest = new Image[10];
                for (int i = 0; i < imagesTest.length; i++) {
                    Image image = new Image();
                    image.setCategory(categories[i].getName());
                    image.setLikes(String.valueOf(i));
                    image.setUrl(categories[i].getBackgroundUrl());
                    imagesTest[i] = image;
                }
                Image[] images = new Image[50];
                for (int i = 0; i < images.length; i++) {
                    int num;
                    if (i < 10) {
                        num = i;
                    } else {
                        num = i / 10;
                    }
                    images[i] = imagesTest[num];
                }
                categories[2].setImages(images);

                ImageData.getInstance().setCategories(categories);

        //    } catch (Exception e) {
       //         showOfflineDialogOnMainThread();
       //     }
            ImageData.getInstance().initImagesFavorites(
                    InitializationActivity.this);
            for (Category category : ImageData.getInstance().getCategories()) {
                if (category.getName().equals("Favorites")) {
                    category.setImages(ImageData.getInstance().getImagesFavorites());
                }
                if (category.getName().equals("Top 50")) {
                    ImageData.getInstance().setImagesTop(category.getImages());
                }
            }
            ImageData.getInstance().initCategoryTitles(InitializationActivity.this);
            AlbumsData.getInstance().initAlbums(InitializationActivity.this);
            return null;
        }

        private void getCategories() {
            Gson gson = new Gson();
            InputStream source = retrieveStream(getResources().getString(
                    R.string.get_category_url));
            Reader reader = new InputStreamReader(source);

            ImageData.getInstance().setCategories(
                    gson.fromJson(reader, Category[].class));
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // cacheCategoryImages();
            Intent i = new Intent(InitializationActivity.this,
                    MainMenuActivity.class);
            i.putExtra(Constants.Extra.IS_OFFLINE, false);
            startActivity(i);
            InitializationActivity.this.finish();
        }

        private InputStream retrieveStream(String urlText) {

            try {

                URL url = new URL(urlText);

                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                InputStream input = connection.getInputStream();

                return input;

            } catch (IOException e) {
                showOfflineDialogOnMainThread();
                this.cancel(true);
            }
            return null;
        }

        private void showOfflineDialogOnMainThread() {
            InitializationActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    showOfflineDialog();
                }
            });
        }
    }
}