package tk.example.quotesandsayings.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Album {

	String name;
	Image[] images;

	public Album() {
		this.name = "";
		images = new Image[0];
	}

	public Album(String name) {
		this.name = name;
		images = new Image[0];
	}

	public Album(String name, Image[] images) {
		this.name = name;
		this.images = images;
	}

	public String getName() {
		return name;
	}

	public Image[] getImages() {
		return images;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setImages(Image[] images) {
		this.images = images;
	}

}
