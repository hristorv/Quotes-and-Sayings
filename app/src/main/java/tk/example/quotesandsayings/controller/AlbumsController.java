package tk.example.quotesandsayings.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.support.design.widget.Snackbar;
import tk.example.quotesandsayings.R;
import tk.example.quotesandsayings.model.Album;
import tk.example.quotesandsayings.model.AlbumsData;
import tk.example.quotesandsayings.model.Image;
import tk.example.quotesandsayings.view.adapters.AlbumsGridRecyclerAdapter;

public class AlbumsController {
	private static final String DEFAULT_LIKES_TEXT = "-";

	public void removeImageFromAlbum(Context context, Album album, Image image) {
		Image[] images = album.getImages();
		List<Image> imageList = new ArrayList<Image>(images.length);
		Collections.addAll(imageList, images);
		for (Image img : imageList) {
			if (img.getUrl().equals(image.getUrl())) {
				imageList.remove(img);
				break;
			}
		}
		images = imageList.toArray(new Image[imageList.size()]);
		album.setImages(images);
		// Update albums.json.
		AlbumsData.getInstance().updateAlbumsFile(context);
	}

	public boolean addImageToAlbum(Context context, Album album, Image curImage) {
		if (new AlbumsController().imageIsInAlbum(curImage, album)) {
			return false;
		} else {
			Image image = curImage.copy();
			Image[] images = album.getImages();
			// Updates image category to be albums name.
			image.setCategory(album.getName());
			image.setLikes(DEFAULT_LIKES_TEXT);
			// Adds the image.
			List<Image> imageList = new ArrayList<Image>(images.length);
			Collections.addAll(imageList, images);
			imageList.add(image);
			images = imageList.toArray(new Image[imageList.size()]);
			album.setImages(images);
			// Update albums.json.
			AlbumsData.getInstance().updateAlbumsFile(context);
			return true;
		}
	}

	public boolean checkIfImagesAreFromAlbum(Image[] images) {
		if (images != null && images.length > 0) {
			String imagesTitle = images[0].getCategory();
			String[] albumTitles = getAlbumTitles();
			for (String albumTitle : albumTitles) {
				if (albumTitle.equals(imagesTitle))
					return true;
			}
		}
		return false;
	}

	public String[] getAlbumTitles() {
		Album[] albums = AlbumsData.getInstance().getAlbums();
		String[] albumsTitles = new String[albums.length];
		for (int i = 0; i < albums.length; i++) {
			albumsTitles[i] = albums[i].getName();
		}
		return albumsTitles;
	}

	public boolean imageIsInAlbum(Image curImage, Album album) {
		boolean isInAlbum = false;
		if (album.getImages().length > 0) {
			for (Image image : album.getImages()) {
				if (image.getUrl().equals(curImage.getUrl()))
					isInAlbum = true;
			}
		}
		return isInAlbum;
	}

	public Image[] getAlbumImagesByIndex(int albumIndex) {
		return AlbumsData.getInstance().getAlbums()[albumIndex].getImages();
	}

	public void addAlbum(Context context, Album newAlbum) {
		Album[] albums = AlbumsData.getInstance().getAlbums();
		List<Album> albumsList = new ArrayList<Album>(albums.length);
		Collections.addAll(albumsList, albums);
		albumsList.add(newAlbum);
		albums = albumsList.toArray(new Album[albumsList.size()]);
		AlbumsData.getInstance().setAlbums(albums);
		// Update albums.json
		AlbumsData.getInstance().updateAlbumsFile(context);
	}

	public void renameAlbum(Context context, String albumName, int albumPosition) {
		// Save the old album name.
		final String oldAlbumName = AlbumsData.getInstance().getAlbums()[albumPosition]
				.getName();
		AlbumsData.getInstance().setOldAlbumName(oldAlbumName);
		// Update album name.
		Album curAlbum = AlbumsData.getInstance().getAlbums()[albumPosition];
		curAlbum.setName(albumName);
		// Update the images category.
		updateAlbumsImagesCategory(curAlbum);
		// Update albums.json
		AlbumsData.getInstance().updateAlbumsFile(context);
	}

	public void reverseRenameAlbum(Context context, int position) {
		// Get the old album name.
		String oldAlbumName = AlbumsData.getInstance().getOldAlbumName();
		// Update album name.
		Album curAlbum = AlbumsData.getInstance().getAlbums()[position];
		curAlbum.setName(oldAlbumName);
		// Update the images category.
		updateAlbumsImagesCategory(curAlbum);
		// Update albums.json
		AlbumsData.getInstance().updateAlbumsFile(context);
	}

	private void updateAlbumsImagesCategory(Album album) {
		for (Image image : album.getImages()) {
			image.setCategory(album.getName());
		}
	}

	public void deleteAlbum(Context context, int albumIndex) {
		// Delete album from array.
		Album[] albums = AlbumsData.getInstance().getAlbums();
		List<Album> albumsList = new ArrayList<Album>(albums.length);
		Collections.addAll(albumsList, albums);
		AlbumsData.getInstance().setDeletedAlbum(albumsList.get(albumIndex));
		AlbumsData.getInstance().setDeletedAlbumIndex(albumIndex);
		albumsList.remove(albumIndex);
		albums = albumsList.toArray(new Album[albumsList.size()]);
		AlbumsData.getInstance().setAlbums(albums);
		// Update albums.json.
		AlbumsData.getInstance().updateAlbumsFile(context);
	}

	public void reverseDeleteAlbum(Context context) {
		Album[] albums = AlbumsData.getInstance().getAlbums();
		List<Album> albumsList = new ArrayList<Album>(albums.length);
		Collections.addAll(albumsList, albums);
		albumsList.add(AlbumsData.getInstance().getDeletedAlbumIndex(),
				AlbumsData.getInstance().getDeletedAlbum());
		albums = albumsList.toArray(new Album[albumsList.size()]);
		AlbumsData.getInstance().setAlbums(albums);
		// Update albums.json.
		AlbumsData.getInstance().updateAlbumsFile(context);
	}

	public void createNewAlbum(Context context, String name) {
		// Create album
		Album newAlbum = new Album(name);
		// Update ImageData
		new AlbumsController().addAlbum(context, newAlbum);
		// Update albums.json
		AlbumsData.getInstance().updateAlbumsFile(context);
	}

	public Image[] removeImageFromAlbum(Context context, Image curImage) {
		// Get the album
		Album curAlbum = null;
		for (Album album : AlbumsData.getInstance().getAlbums()) {
			if (album.getName().equals(curImage.getCategory()))
				curAlbum = album;
		}
		if (curAlbum != null) {
			for (Image image : curAlbum.getImages()) {
				if (image.getUrl().equals(curImage.getUrl())) {
					removeImageFromAlbum(context, curAlbum, image);
					return curAlbum.getImages();
				}

			}

		}
		return null;
	}

	public Image[] reverseRemoveImageFromAlbum(Context context, Image curImage) {
		for (Album album : AlbumsData.getInstance().getAlbums()) {
			if (album.getName().equals(curImage.getCategory())) {
				addImageToAlbum(context, album, curImage);
				AlbumsData.getInstance().updateAlbumsFile(context);
				return album.getImages();
			}
		}
		return null;
	}
}
