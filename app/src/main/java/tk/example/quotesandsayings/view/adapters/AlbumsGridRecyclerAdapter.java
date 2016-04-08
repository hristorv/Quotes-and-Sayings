package tk.example.quotesandsayings.view.adapters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tk.example.quotesandsayings.R;
import tk.example.quotesandsayings.controller.AlbumsController;
import tk.example.quotesandsayings.model.Album;
import tk.example.quotesandsayings.model.AlbumsData;
import tk.example.quotesandsayings.model.Constants;
import tk.example.quotesandsayings.view.SystemUI;
import tk.example.quotesandsayings.view.activities.MainMenuActivity;
import tk.example.quotesandsayings.view.fragments.AlbumsFragment;
import tk.example.quotesandsayings.view.fragments.ImageGridFragment;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.Snackbar.Callback;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class AlbumsGridRecyclerAdapter extends
		RecyclerView.Adapter<AlbumsGridRecyclerAdapter.ViewHolder> {

	public String[] albumsTitles;
	private Context context;
	private DisplayImageOptions options;
	private static final int MAX_CHARS_ALBUM_NAME = 10;
	protected static final int MIN_NAME_SIZE = 3;

	public AlbumsGridRecyclerAdapter(Context context) {
		this.context = context;
		albumsTitles = new AlbumsController().getAlbumTitles();
		setImageLoaderOptions();
	}

	private void setImageLoaderOptions() {
		options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisk(true).bitmapConfig(Bitmap.Config.ARGB_8888)
				.delayBeforeLoading(0).build();
	}

	private void setCategoryHolder(int position, ViewHolder holder) {
		getImage(position, holder);
		holder.titleBar.setText(albumsTitles[position]);
	}

	private void getImage(final int position, final ViewHolder holder) {
		ImageLoader.getInstance().displayImage(
				"drawable://" + R.drawable.drawer_image20, holder.imageView,
				options);
	}

	// Usually involves inflating a layout from XML and returning the holder
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		Context context = parent.getContext();
		LayoutInflater inflater = LayoutInflater.from(context);

		// Inflate the custom layout
		View itemView = inflater.inflate(R.layout.fragment_categories_item,
				parent, false);

		// Return a new holder instance
		ViewHolder viewHolder = new ViewHolder(itemView);
		return viewHolder;
	}

	// Involves populating data into the item through holder
	@Override
	public void onBindViewHolder(ViewHolder viewHolder, int position) {
		viewHolder.itemView.setOnClickListener(new AlbumsOnClickListener(
				position));
		viewHolder.itemView
				.setOnLongClickListener(new AlbumsOnLongClickListener(position,
						viewHolder.itemView));
		setCategoryHolder(position, viewHolder);
		viewHolder.itemView.startAnimation(AnimationUtils.loadAnimation(
				context, R.animator.item_slide_in));
	}

	@Override
	public void onViewDetachedFromWindow(ViewHolder holder) {
		// This prevents the bug,when we are fast scrolling.
		holder.itemView.clearAnimation();
		super.onViewDetachedFromWindow(holder);
	}

	// Return the total count of items
	@Override
	public int getItemCount() {
		return albumsTitles.length;
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		// Your holder should contain a member variable
		// for any view that will be set as you render a row

		public ImageView imageView;
		public TextView titleBar;
		public View itemView;

		// We also create a constructor that accepts the entire item row
		// and does the view lookups to find each subview
		public ViewHolder(View itemView) {
			// Stores the itemView in a public final member variable that can be
			// used
			// to access the context from any ViewHolder instance.
			super(itemView);
			this.itemView = itemView;
			imageView = (ImageView) itemView.findViewById(R.id.category_image);
			titleBar = (TextView) itemView.findViewById(R.id.category_title);
		}
	}

	private void renameAlbum(final int position, final View view) {
		// Ask for album name
		final MaterialDialog materialDialog = new MaterialDialog.Builder(
				context)
				.title(R.string.action_rename)
				.inputMaxLengthRes(MAX_CHARS_ALBUM_NAME, R.color.primary_color)
				.inputType(
						InputType.TYPE_CLASS_TEXT
								| InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)
				.input(R.string.enter_album_name, R.string.album_name_default,
						new MaterialDialog.InputCallback() {
							@Override
							public void onInput(MaterialDialog dialog,
									CharSequence input) {

							}
						}).show();
		new SystemUI().setupFloatingLabelError(context, materialDialog);
		materialDialog.getActionButton(DialogAction.POSITIVE)
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						final AlbumsController albumsController = new AlbumsController();
						final CharSequence input = materialDialog
								.getInputEditText().getText();
						albumsController.renameAlbum(context, input.toString(),
								position);
						Snackbar.make(view, R.string.warning_renamed,
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
								.setAction(R.string.snackbar_action_undo,
										new View.OnClickListener() {

											@Override
											public void onClick(View v) {
												albumsController
														.reverseRenameAlbum(
																context,
																position);
												updateAlbumsTitles();
												// Update current UI (The
												// gridView).
												notifyItemChanged(position);
												MainMenuActivity activity = (MainMenuActivity) context;
												Fragment currentFragment = activity
														.getFragmentManager()
														.findFragmentByTag(
																"ImageGridFragment");
												if (currentFragment instanceof ImageGridFragment) {
													activity.setTitle(AlbumsData
															.getInstance()
															.getOldAlbumName());
												}
											}
										}).show();

						materialDialog.dismiss();
						updateAlbumsTitles();
						notifyItemChanged(position);
					}
				});
		materialDialog.show();
	}

	private void deleteAlbum(final int albumIndex, final View view) {
		new MaterialDialog.Builder(context)
				.content(R.string.action_delete_album)
				.negativeText(R.string.cancel)
				.negativeColorRes(R.color.primary_color)
				.positiveText(R.string.action_delete)
				.positiveColorRes(R.color.primary_color)
				.callback(new MaterialDialog.ButtonCallback() {
					@Override
					public void onPositive(MaterialDialog dialog) {
						final AlbumsController albumsController = new AlbumsController();
						albumsController.deleteAlbum(context, albumIndex);
						updateAlbumsTitles();
						// Update the adapter to remove the item.
						notifyItemRemoved(AlbumsData.getInstance()
								.getDeletedAlbumIndex());
						// Show toast message
						View baseView = ((Activity) context)
								.findViewById(R.id.fab);
						Snackbar.make(baseView, R.string.warning_deleted,
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
								.setAction(R.string.snackbar_action_undo,
										new View.OnClickListener() {

											@Override
											public void onClick(View v) {
												albumsController
														.reverseDeleteAlbum(context);
												updateAlbumsTitles();
												// Update the adapter to add
												// the item.
												notifyItemInserted(AlbumsData
														.getInstance()
														.getDeletedAlbumIndex());
											}
										}).show();

						super.onPositive(dialog);
					}
				}).show();
	}

	private class AlbumsOnLongClickListener implements
			View.OnLongClickListener, OnMenuItemClickListener {

		int position;
		View view;

		public AlbumsOnLongClickListener(int position, View view) {
			this.position = position;
			this.view = view;
		}

		@Override
		public boolean onMenuItemClick(MenuItem item) {
			switch (item.getItemId()) {
			case R.id.album_delete:
				deleteAlbum(position, view);
				return true;
			case R.id.album_rename:
				renameAlbum(position, view);
				return true;
			default:
				return false;
			}
		}

		@Override
		public boolean onLongClick(View v) {
			PopupMenu popup = new PopupMenu(context, view);
			popup.setOnMenuItemClickListener(this);
			popup.inflate(R.menu.album_popup);
			popup.show();
			return false;
		}

	}

	private class AlbumsOnClickListener implements View.OnClickListener {
		int position;

		public AlbumsOnClickListener(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			// Update the main content by replacing fragments
			Fragment fragment = createFragment(position);
			String tag = "ImageGridFragment";
			if (fragment != null) {
				FragmentManager fragmentManager = ((Activity) context)
						.getFragmentManager();
				fragmentManager
						.beginTransaction()
						.setCustomAnimations(R.animator.card_flip_right_in,
								R.animator.card_flip_right_out,
								R.animator.card_flip_left_in,
								R.animator.card_flip_left_out)
						.replace(R.id.content_frame, fragment, tag)
						// Add this transaction to the back stack
						.addToBackStack(Constants.FragmentNames.ALBUMS_FRAGMENT)
						.commit();

				// Update selected action bar title.
				MainMenuActivity activity = (MainMenuActivity) context;
				activity.setTitle(AlbumsData.getInstance().getAlbums()[position]
						.getName());
			}
		}

		private Fragment createFragment(int position) {
			Fragment fragment;
			Bundle bundle = new Bundle();
			bundle.putInt(Constants.Extra.ALBUM_INDEX, position);
			bundle.putBoolean(Constants.Extra.ALBUM_BOOLEAN, true);
			bundle.putBoolean(Constants.Extra.FAVORITES_BOOLEAN, false);
			fragment = new ImageGridFragment();
			fragment.setArguments(bundle);
			return fragment;
		}
	}

	public void updateAlbumsTitles() {
		AlbumsController albumController = new AlbumsController();
		this.albumsTitles = albumController.getAlbumTitles();

	}

}
