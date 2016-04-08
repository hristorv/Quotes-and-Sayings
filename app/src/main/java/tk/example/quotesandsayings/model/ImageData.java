package tk.example.quotesandsayings.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tk.example.quotesandsayings.R;
import tk.example.quotesandsayings.view.activities.InitializationActivity;
import tk.example.quotesandsayings.view.adapters.AlbumsGridRecyclerAdapter;
import tk.example.quotesandsayings.view.adapters.ImageGridRecyclerAdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.google.gson.Gson;

public class ImageData {
    private static ImageData instance = null;

    private Category[] categories;
    private Image[] imagesTop;
    private Image[] imagesFavorites;
    private String[] categoryTitles;


    private ImageData() {
    }

    public static ImageData getInstance() {
        if (instance == null) {
            instance = new ImageData();
        }
        return instance;
    }

    public void initImagesFavorites(Context context) {
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
        ImageData.getInstance().setImagesFavorites(
                gson.fromJson(aBuffer, Image[].class));
        if (ImageData.getInstance().getImagesFavorites() == null) {
            ImageData.getInstance().setImagesFavorites(new Image[0]);
        }
    }

    public Category[] getCategories() {
        return categories;
    }

    public void setCategories(Category[] categories) {
        this.categories = categories;
    }

    public Image[] getImagesTop() {
        return imagesTop;
    }

    public void setImagesTop(Image[] imagesTop) {
        this.imagesTop = imagesTop;
    }

    public void setImagesFavorites(Image[] imagesFavorites) {
        this.imagesFavorites = imagesFavorites;
    }

    public Image[] getImagesFavorites() {
        return imagesFavorites;
    }


    public String[] getCategoriesTitles() {
        return categoryTitles;
    }

    public void initCategoryTitles(Context context) {
        ArrayList titlesList = new ArrayList();
        for (Category category : categories)
            titlesList.add(category.getName());
        categoryTitles = (String[]) titlesList.toArray(new String[titlesList.size()]);
    }

    public void setCategoryTitles(String[] categoryTitles) {
        this.categoryTitles = categoryTitles;
    }
}
