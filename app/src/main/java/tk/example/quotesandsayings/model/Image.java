package tk.example.quotesandsayings.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Image implements Comparable<Image>, Parcelable {
	String url;
	String likes;
	String category;

	// Parceling part
	public Image(Parcel in) {
		String[] data = new String[3];

		in.readStringArray(data);
		this.category = data[0];
		this.likes = data[1];
		this.url = data[2];
	}

	public Image() {
	}

	public Image copy() {
		Image newImage = new Image();
		newImage.setCategory(this.category);
		newImage.setLikes(this.likes);
		newImage.setUrl(this.url);
		return newImage;
	}

	public String getUrl() {
		return url;
	}

	public String getLikes() {
		return likes;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setLikes(String likes) {
		this.likes = likes;
	}

	@Override
	public int compareTo(Image another) {
		return Integer.parseInt(another.likes) - Integer.parseInt(this.likes);
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeStringArray(new String[] { this.category, this.likes,
				this.url });
	}

	public static final Parcelable.Creator<Image> CREATOR = new Parcelable.Creator<Image>() {
		public Image createFromParcel(Parcel in) {
			return new Image(in);
		}

		public Image[] newArray(int size) {
			return new Image[size];
		}
	};

}
