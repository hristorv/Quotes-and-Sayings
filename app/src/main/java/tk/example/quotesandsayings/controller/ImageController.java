package tk.example.quotesandsayings.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;

import tk.example.quotesandsayings.R;
import tk.example.quotesandsayings.model.AlbumsData;
import tk.example.quotesandsayings.model.Category;
import tk.example.quotesandsayings.model.Constants;
import tk.example.quotesandsayings.model.Image;
import tk.example.quotesandsayings.model.ImageData;
import tk.example.quotesandsayings.view.SystemUI;
import tk.example.quotesandsayings.view.activities.InitializationActivity;
import tk.example.quotesandsayings.view.adapters.ImagePagerAdapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.gson.Gson;

public class ImageController {

    private static final String FAVORITES = "Favorites";
    private static final int WIDTH_DEFAULT_VALUE = 0;
    private static final int SCROLL_VALUE = 2;
    private static final int IMAGE_NAME_LENGHT = 6;
    private static final int FIRST_TOP_ELEMENT_INDEX = 0;
    private static final int LAST_TOP_ELEMENT_INDEX = 49;
    public static final int STARTING_INDEX = 2;

    public Image[] getFavorites(Context context) {
        File myFile = new File(context.getFilesDir(), "favorites.txt");
        FileInputStream fIn = null;
        try {
            fIn = new FileInputStream(myFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
        String aDataRow = "";
        String aBuffer = ""; // Holds the text
        try {
            while ((aDataRow = myReader.readLine()) != null) {
                aBuffer += aDataRow;
            }
            myReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        Image[] imagesFavorites = gson.fromJson(aBuffer, Image[].class);
        if (imagesFavorites == null) {
            imagesFavorites = new Image[0];
        }
        return imagesFavorites;
    }

    public Image[] getCategoryImagesByIndex(int index) {
        return ImageData.getInstance().getCategories()[index].getImages();
    }

    public void createFavoritesFile(Context context) {
        File myFile = new File(context.getFilesDir(), "favorites.txt");
        try {
            myFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Uri getImageUri(Bitmap bitmapImage) {
        File mypath = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "image.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write
            // image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Uri.fromFile(mypath);
    }

    public void doDownload(final Bitmap curBitmap) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... params) {
                saveToInternalStorage(curBitmap);
                return null;
            }
        }.execute();
    }

    private void saveToInternalStorage(Bitmap bitmapImage) {
        File mypath = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                bitmapImage.toString().substring(
                        bitmapImage.toString().length() - IMAGE_NAME_LENGHT)
                        + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write
            // image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doSetWallpaper(final Context context, final Bitmap curBitmap,
                               final CharSequence choice) {
        final WallpaperManager wallpaperManager = WallpaperManager
                .getInstance(context);
        // Start a asynchronous task to change the wallpaper.
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... params) {
                try {
                    wallpaperManager.setBitmap(getScaledBitmap(context,
                            curBitmap, choice));

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    public Bitmap getScaledBitmap(Context context, Bitmap loadedBitmap,
                                  CharSequence choice) {
        String choiceString = choice.toString();
        final WallpaperManager wallpaperManager = WallpaperManager
                .getInstance(context);
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        boolean isTablet = context.getResources().getBoolean(R.bool.isTablet);
        int desiredWidth = wallpaperManager.getDesiredMinimumWidth();
        int desiredHeight = wallpaperManager.getDesiredMinimumHeight();
        int realWidth = prefs.getInt(Constants.PreferencesKeys.REAL_WIDTH_PREF,
                0);
        int realHeight = prefs.getInt(
                Constants.PreferencesKeys.REAL_HEIGHT_PREF, 0);
        int width = 0;
        int height = 0;
        if (choiceString.equals(Constants.WallpaperScaling.DEFAULT)) {
            width = desiredWidth;
            height = desiredHeight;
            if (!isTablet && desiredWidth == desiredHeight) {
                width = realWidth;
                height = realHeight;
            }
        } else if (choiceString.equals(Constants.WallpaperScaling.STRETCH)) {
            width = realWidth * SCROLL_VALUE;
            height = realHeight;

        } else if (choiceString.equals(Constants.WallpaperScaling.FIT)) {
            width = realWidth;
            height = realHeight;
        } else if (choiceString.equals(Constants.WallpaperScaling.CENTER)) {
            width = realWidth;
            height = realHeight;
            if (height > width) {
                width = height;
            } else {
                height = width;
            }
        }
        return Bitmap.createScaledBitmap(loadedBitmap, width, height, true);
    }

    public void likeImage(Context context, Image curImage) {
        new PostLike(context, curImage.getCategory(), curImage.getUrl())
                .execute();
        updateImageLikes(curImage);
        updateFavorites(context, curImage);
        if (ImageData.getInstance().getImagesTop() != null) {
            updateTop(curImage);
            updateCategory(curImage);
        }
    }

    private void updateImageLikes(Image curImage) {
        curImage.setLikes(Integer.toString((Integer.parseInt(curImage
                .getLikes()) + 1)));
    }

    private void updateCategory(Image curImage) {
        String imageCategory = curImage.getCategory();
        for (Category category : ImageData.getInstance().getCategories()) {
            if (category.getName().equals(imageCategory)) {
                changeLikes(curImage, category.getImages());
                break;
            }
        }
    }

    private void changeLikes(Image curImage, Image[] images) {
        for (Image image : images) {
            if (image.getUrl().equals(curImage.getUrl())) {
                image.setLikes(curImage.getLikes());
            }
        }
    }

    private void updateTop(Image curImage) {
        Image lastImage = ImageData.getInstance().getImagesTop()[LAST_TOP_ELEMENT_INDEX];
        ArrayList<Image> topList = new ArrayList<Image>();
        Collections.addAll(topList, ImageData.getInstance().getImagesTop());
        if (Integer.parseInt(curImage.getLikes()) >= Integer.parseInt(lastImage
                .getLikes())) {
            for (int i = LAST_TOP_ELEMENT_INDEX; i >= FIRST_TOP_ELEMENT_INDEX; i--) {
                if (topList.get(i).getUrl().equals(curImage.getUrl())) {
                    topList.remove(i);
                    break;
                }
            }
            topList.add(curImage);
            Collections.sort(topList);
            ImageData.getInstance().setImagesTop(
                    topList.subList(0, 50).toArray(new Image[50]));
        }

    }

    private void updateFavorites(Context context, Image curImage) {
        // Change image category to Favorites.
        curImage.setCategory(FAVORITES);
        ArrayList<Image> listFavorites = new ArrayList<Image>();
        Collections.addAll(listFavorites, getFavorites(context));
        listFavorites.add(curImage);
        ImageData.getInstance().setImagesFavorites(
                listFavorites.toArray(new Image[listFavorites.size()]));
        // Update database
        Gson gson = new Gson();
        String jsonString = gson.toJson(ImageData.getInstance()
                .getImagesFavorites());
        File myFile = new File(context.getFilesDir(), "favorites.txt");
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(myFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
        try {
            myOutWriter.append(jsonString);
            myOutWriter.close();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean checkIfImagesAreFromFavorites(Image[] images) {
        boolean isFavorites = false;
        if (images != null && images.length > 0) {
            isFavorites = images[0].getCategory().equals(FAVORITES);
        }
        return isFavorites;
    }

    public void setImageArraysDefault(Context context) {
        ImageData.getInstance().setCategories(new Category[0]);
        ImageData.getInstance().setImagesTop(new Image[0]);
        ImageData.getInstance().initImagesFavorites(
                context);
        AlbumsData.getInstance().initAlbums(context);
        ImageData.getInstance().setCategoryTitles(new String[0]);
    }

}
