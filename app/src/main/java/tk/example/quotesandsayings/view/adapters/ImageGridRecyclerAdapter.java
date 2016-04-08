package tk.example.quotesandsayings.view.adapters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tk.example.quotesandsayings.R;
import tk.example.quotesandsayings.model.Album;
import tk.example.quotesandsayings.model.AlbumsData;
import tk.example.quotesandsayings.model.Constants;
import tk.example.quotesandsayings.model.Image;
import tk.example.quotesandsayings.view.activities.ImageActivity;
import tk.example.quotesandsayings.view.activities.MainMenuActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class ImageGridRecyclerAdapter extends
		RecyclerView.Adapter<ImageGridRecyclerAdapter.ViewHolder> {
	private static final String DEFAULT_LIKES_SIGN = "-";
	DisplayImageOptions options;
	Image[] images;
	private boolean isAlbum;
	private Context context;
	private int category;
	private int album;
	private boolean isFavorites;

	public ImageGridRecyclerAdapter(Context context, Image[] images,
			boolean isAlbum, boolean isFavorites, int category, int album) {
		this.context = context;
		this.images = images;
		this.isAlbum = isAlbum;
		this.isFavorites = isFavorites;
		this.category = category;
		this.album = album;
		setImageLoaderOptions();
	}

	private void setImageLoaderOptions() {
		options = new DisplayImageOptions.Builder()
				// TODO
				// .showImageOnLoading(R.drawable.ic_launcher)
				// .showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
				.resetViewBeforeLoading(true)
				.cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
				.delayBeforeLoading(0)
				.displayer(new FadeInBitmapDisplayer(500)).build();
	}

	private void getImage(final int position, final ViewHolder holder) {
		ImageLoader.getInstance().displayImage(images[position].getUrl(),
				holder.imageView, options, new SimpleImageLoadingListener() {
					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						super.onLoadingComplete(imageUri, view, loadedImage);
						holder.likesBar.setText(images[position].getLikes());
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						super.onLoadingFailed(imageUri, view, failReason);
						holder.likesBar.setText(DEFAULT_LIKES_SIGN);
					}
				});
	}

	// Usually involves inflating a layout from XML and returning the holder
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		Context context = parent.getContext();
		LayoutInflater inflater = LayoutInflater.from(context);
		View itemView = inflater.inflate(R.layout.fragment_pictures_item,
				parent, false);
		// Clears the likes layout, if we are in an album.
		if (isAlbum || isFavorites) {
			View likesLayout = (View) itemView.findViewById(R.id.likesLayout);
			likesLayout.setVisibility(View.GONE);
		}
		// Return a new holder instance
		ViewHolder viewHolder = new ViewHolder(itemView);
		return viewHolder;
	}

	// Involves populating data into the item through holder
	@Override
	public void onBindViewHolder(ViewHolder viewHolder, int position) {
		viewHolder.itemView.setOnClickListener(new MyOnItemClickListener(
				position));
		if (isAlbum) {
			viewHolder.itemView
					.setOnLongClickListener(new ImageOnLongClickListener(
							position, viewHolder.itemView));
		}
		getImage(position, viewHolder);
	}

	// Return the total count of items
	@Override
	public int getItemCount() {
		return images.length;
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		// Your holder should contain a member variable
		// for any view that will be set as you render a row
		public ImageView imageView;
		public TextView likesBar;
		public View itemView;

		// We also create a constructor that accepts the entire item row
		// and does the view lookups to find each subview
		public ViewHolder(View itemView) {
			// Stores the itemView in a public final member variable that can be
			// used
			// to access the context from any ViewHolder instance.
			super(itemView);
			this.itemView = itemView;
			imageView = (ImageView) itemView.findViewById(R.id.image);
			likesBar = (TextView) itemView.findViewById(R.id.likesBar);
			likesBar.setText(DEFAULT_LIKES_SIGN);
		}
	}

	public class MyOnItemClickListener implements View.OnClickListener {

		private int position;

		public MyOnItemClickListener(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			startImageActivity(position);

		}

	}

	private void startImageActivity(int position) {
		Intent intent = new Intent(context, ImageActivity.class);
		intent.putExtra(Constants.Extra.IMAGE_POSITION, position);
		if (!isAlbum) {
			intent.putExtra(Constants.Extra.CATEGORY_INDEX, category);
			if (isFavorites)
				intent.putExtra(Constants.Extra.FAVORITES_BOOLEAN, true);
		} else {
			intent.putExtra(Constants.Extra.ALBUM_INDEX, album);
			intent.putExtra(Constants.Extra.ALBUM_BOOLEAN, true);
		}
		// context.startActivity(intent);
		((Activity) context).startActivityForResult(intent,
				Constants.Extra.REQUEST_CODE_IMAGE);
	}

	public class ImageOnLongClickListener implements View.OnLongClickListener,
			OnMenuItemClickListener {
		int position;
		View view;

		public ImageOnLongClickListener(int position, View view) {
			this.position = position;
			this.view = view;
		}

		@Override
		public boolean onMenuItemClick(MenuItem item) {
			switch (item.getItemId()) {
			case R.id.delete:
				deleteImage(this.position, view);
				return true;
			default:
				return false;
			}
		}

		@Override
		public boolean onLongClick(View v) {
			PopupMenu popup = new PopupMenu(context, view);
			popup.setOnMenuItemClickListener(this);
			popup.inflate(R.menu.image_popup);
			popup.show();
			return false;
		}
	}

	private void deleteImage(final int position, View view) {
		// Get the current image.
		final Image curImage = images[position];
		// Get the album
		Album curAlbum = null;
		for (Album album : AlbumsData.getInstance().getAlbums()) {
			if (album.getName().equals(curImage.getCategory()))
				curAlbum = album;
		}
		if (curAlbum != null) {
			for (int i = 0; i < curAlbum.getImages().length; i++) {
				if (curAlbum.getImages()[i].getUrl().equals(curImage.getUrl())) {
					List<Image> imageList = new ArrayList<Image>(
							curAlbum.getImages().length);
					Collections.addAll(imageList, curAlbum.getImages());
					final int imageIndex = i;
					imageList.remove(i);
					curAlbum.setImages(imageList.toArray(new Image[imageList
							.size()]));
					this.images = curAlbum.getImages();
					AlbumsData.getInstance().updateAlbumsFile(context);
					Snackbar.make(view, R.string.warning_deleted,
							Snackbar.LENGTH_LONG)
							.setCallback(new Snackbar.Callback() {
									boolean isShown;

									@Override
									public void onShown(Snackbar snackbar) {
										isShown = true;
										super.onShown(snackbar);
									}

									@Override
									public void onDismissed(Snackbar snackbar,
											int event) {
										if (isShown) {
											((MainMenuActivity) context)
													.checkIfOffline();
											isShown = false;
										}
										super.onDismissed(snackbar, event);
									}
								})
							.setAction(
									R.string.snackbar_action_undo,
									new MyOnClickListener(curAlbum, imageIndex,
											curImage, position)).show();
					notifyItemRemoved(position);
				}
			}
		}
	}

	private class MyOnClickListener implements View.OnClickListener {

		private Album curAlbum;
		private int imageIndex;
		private Image curImage;
		private int position;

		public MyOnClickListener(Album curAlbum, int imageIndex,
				Image curImage, int position) {
			this.curAlbum = curAlbum;
			this.imageIndex = imageIndex;
			this.curImage = curImage;
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			List<Image> imageList = new ArrayList<Image>(
					curAlbum.getImages().length);
			Collections.addAll(imageList, curAlbum.getImages());
			imageList.add(imageIndex, curImage);
			curAlbum.setImages(imageList.toArray(new Image[imageList.size()]));
			ImageGridRecyclerAdapter.this.images = curAlbum.getImages();
			AlbumsData.getInstance().updateAlbumsFile(context);
			// Update the adapters.
			notifyItemInserted(position);

		}

	}

	public void setImages(Image[] images) {
		if (images != null)
			this.images = images;
	}

	public Image[] getImages() {
		return this.images;

	}

}
