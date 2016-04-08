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

import tk.example.quotesandsayings.R;
import tk.example.quotesandsayings.model.Album;
import tk.example.quotesandsayings.model.AlbumsData;
import tk.example.quotesandsayings.model.Image;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.google.gson.Gson;

class GetCategoryJson extends AsyncTask<Void, Void, Void> {

    private Image[] categoryArray;
    Bitmap loadedBitmap;
    private BaseService baseService;
    private String curCategory;
    private Context context;

    public GetCategoryJson(BaseService baseService, String curCategory) {
        this.baseService = baseService;
        this.context = baseService;
        this.curCategory = curCategory;
    }

    @Override
    protected Void doInBackground(Void... params) {

        Gson gson = new Gson();
        if (curCategory != null) {
            Resources res = context.getResources();
            if (checkAlbums()) {
            } else if (curCategory.equals(res.getString(R.string.favorites))) {
                getFavoritesCat();
            } else if (curCategory.equals(res.getString(R.string.top))) {
                getCategory(gson,res.getString(R.string.url_top));
            } else {
                String url = res.getString(R.string.get_single_category_url);
                url = url + "?category=" + curCategory;
                getCategory(gson,url);
            }
        }
        return null;
    }

    private boolean checkAlbums() {
        AlbumsData.getInstance().initAlbums(context);
        Album[] albums = AlbumsData.getInstance().getAlbums();
        for (Album album : albums) {
            if (album.getName().equals(curCategory)) {
                categoryArray = album.getImages();
                return true;
            }
        }
        return false;
    }

    private void getFavoritesCat() {
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

        categoryArray = gson.fromJson(aBuffer, Image[].class);
        if (categoryArray == null) {
            categoryArray = new Image[0];
        }

    }

    private void getCategory(Gson gson, String url) {
        InputStream source = retrieveStream(url);
        if (source != null) {
            Reader reader = new InputStreamReader(source);
            categoryArray = gson.fromJson(reader, Image[].class);
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showErrorMessage();
        }
    }

    private void showErrorMessage() {
        ServiceController serviceController = new ServiceController();
        if (baseService instanceof WidgetService) {
            serviceController.addErrorMessage(context);
        } else if (baseService instanceof NotificationService) {
            serviceController.makeErrorNotification(context, ServiceController.NOTIFICATION);
        } else if (baseService instanceof WallpaperService) {
            serviceController.makeErrorNotification(context, ServiceController.WALLPAPER);
        }
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if (categoryArray != null) {
            baseService.getCurImage(categoryArray);
        }
    }

    private InputStream retrieveStream(String urlText) {

        try {

            URL url = new URL(urlText);

            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            InputStream input = connection.getInputStream();

            return input;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
