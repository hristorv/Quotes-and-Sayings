package tk.example.quotesandsayings.model;

/**
 * Created by Hri100 on 11/22/2015.
 */
public class Category {

    private String name;
    private Image[] images;
    private String backgroundUrl;

    public Category(String name, Image[] images, String backgroundUrl) {
        this.name = name;
        this.images = images;
        this.backgroundUrl = backgroundUrl;
    }

    public String getBackgroundUrl() {
        return backgroundUrl;
    }

    public void setBackgroundUrl(String backgroundUrl) {
        this.backgroundUrl = backgroundUrl;
    }


    public Image[] getImages() {
        return images;
    }

    public void setImages(Image[] images) {
        this.images = images;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
