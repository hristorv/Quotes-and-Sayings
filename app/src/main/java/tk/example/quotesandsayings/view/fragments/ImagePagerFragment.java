package tk.example.quotesandsayings.view.fragments;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tk.example.quotesandsayings.R;
import tk.example.quotesandsayings.controller.AlbumsController;
import tk.example.quotesandsayings.controller.ImageController;
import tk.example.quotesandsayings.controller.PostLike;
import tk.example.quotesandsayings.model.Album;
import tk.example.quotesandsayings.model.AlbumsData;
import tk.example.quotesandsayings.model.Constants;
import tk.example.quotesandsayings.model.Image;
import tk.example.quotesandsayings.model.ImageData;
import tk.example.quotesandsayings.utils.ConnectionManager;
import tk.example.quotesandsayings.utils.Convertor;
import tk.example.quotesandsayings.view.CustomViewPager;
import tk.example.quotesandsayings.view.SystemUI;
import tk.example.quotesandsayings.view.activities.ImageActivity;
import tk.example.quotesandsayings.view.activities.MainMenuActivity;
import tk.example.quotesandsayings.view.adapters.ImageGridRecyclerAdapter;
import tk.example.quotesandsayings.view.adapters.ImagePagerAdapter;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore.Images;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.ActionProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.folderselector.FolderChooserDialog;
import com.google.gson.Gson;

public class ImagePagerFragment extends Fragment {

	private static final int MAX_CHARS_ALBUM_NAME = 10;
	private static final int SHARE_IMAGE_REQUEST_CODE = 0;
	private static final int STARTING_POSITION = 0;
	Image[] images;
	int imagePosition;
	CustomViewPager viewPager;
	public boolean dimensionCalc = false;
	private MenuItem likeItem;
	private TextView likesView;
	private boolean isAlbum;
	private TextView likesViewText;
	private boolean isFromWidget;
	private int categoryIndex;
	private int albumIndex;
	private boolean isFavorites;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		isFromWidget = getArguments()
				.getBoolean(Constants.Extra.IS_FROM_WIDGET);
		if (isFromWidget) {
			getImagesWidget();
			AlbumsData.getInstance().initAlbums(getActivity());
			if (new AlbumsController().checkIfImagesAreFromAlbum(images))
				isAlbum = true;
			if (new ImageController().checkIfImagesAreFromFavorites(images))
				isFavorites = true;
		} else {
			getImages();
		}
		getImagePosition();
	}

	private void getImagePosition() {
		this.imagePosition = getArguments().getInt(
				Constants.Extra.IMAGE_POSITION, 0);

	}

	private void getImagesWidget() {
		if (getArguments().getParcelableArray(Constants.Extra.IMAGE_ARRAY) != null) {
			Parcelable[] ps = getArguments().getParcelableArray(
					Constants.Extra.IMAGE_ARRAY);
			images = new Image[ps.length];
			System.arraycopy(ps, 0, images, 0, ps.length);
		}
	}

	private void getImages() {
		isAlbum = getArguments().getBoolean(Constants.Extra.ALBUM_BOOLEAN);
		isFavorites = getArguments().getBoolean(
				Constants.Extra.FAVORITES_BOOLEAN);
		if (!isAlbum) {
			categoryIndex = getArguments().getInt(
					Constants.Extra.CATEGORY_INDEX);
			images = new ImageController()
					.getCategoryImagesByIndex(categoryIndex);
		} else {
			albumIndex = getArguments().getInt(Constants.Extra.ALBUM_INDEX);
			images = new AlbumsController().getAlbumImagesByIndex(albumIndex);
		}
	}

	@Override
	public void setArguments(Bundle args) {
		super.setArguments(args);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.image_pager_menu, menu);
		// If its an album fragment, hide the like button and the add to album
		// button and show the delete button.
		if (isAlbum) {
			menu.findItem(R.id.like).setVisible(false);
			menu.findItem(R.id.add_album).setVisible(false);
			menu.findItem(R.id.delete).setVisible(true);
		}
		if (isFavorites) {
			menu.findItem(R.id.like).setVisible(false);
		}
		getActivity().setTitle(images[0].getCategory());
		// Get the like menu item
		likeItem = menu.findItem(R.id.like);
		addTextView(menu);
		// Hide likes bar, if its album.
		if (isAlbum || isFavorites) {
			likesView.setVisibility(View.GONE);
			likesViewText.setVisibility(View.GONE);
		}
		// This fixes a bug,when you open the view pager,with 0 index.
		if (imagePosition == STARTING_POSITION) {
			adjustLikeButton(0);
		}
		this.viewPager.setLikeItem(likeItem);
		this.viewPager.setLikeView(likesView);
		ImagePagerAdapter adapter = new ImagePagerAdapter(getActivity(),
				images, imagePosition, viewPager);
		// Setting blank adapter first, to fix the issue with loading first two elements allways. This happens when we call setCurrentItem after setting adapter
		this.viewPager.setAdapter(new PagerAdapter() {
			@Override
			public int getCount() {
				return 0;
			}

			@Override
			public boolean isViewFromObject(View view, Object object) {
				return false;
			}
		});
		this.viewPager.setCurrentItem(imagePosition, false);
		this.viewPager.setAdapter(adapter);
		this.viewPager.setCurrentItem(imagePosition, false);

		forceShowIcons(menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	private void forceShowIcons(Menu menu) {
		// if(featureId == Window.FEATURE_ACTION_BAR && menu != null){
		if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
			try {
				Method m = menu.getClass().getDeclaredMethod(
						"setOptionalIconsVisible", Boolean.TYPE);
				m.setAccessible(true);
				m.invoke(menu, true);
			} catch (NoSuchMethodException e) {
				Log.e("ImageActivity", "onMenuOpened", e);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		// }

	}

	private void addTextView(Menu menu) {
		likesView = new TextView(getActivity());
		likesView.setTextColor(getResources().getColor(R.color.white_overlay));
		likesView.setPadding(5, 0, 5, 0);
		Typeface robotoMedium = Typeface.createFromAsset(getActivity()
				.getAssets(), "RobotoCondensed-Bold.ttf");
		likesView.setTypeface(robotoMedium);
		likesView.setTextSize(16);
		likesViewText = new TextView(getActivity());
		likesViewText.setText(R.string.likes);
		likesViewText.setTextColor(getResources().getColor(
				R.color.white_overlay));
		likesViewText.setPadding(5, 0, 5, 0);
		likesViewText.setTypeface(robotoMedium);
		likesViewText.setTextSize(16);

		// Sets the text views on the left side of the action bar.
		LinearLayout layout = new LinearLayout(getActivity());
		layout.setGravity(Gravity.CENTER);
		layout.setPadding(Convertor.convertDpToPixel(10), 0,
				Convertor.convertDpToPixel(10), 0);
		layout.addView(likesView);
		layout.addView(likesViewText);
		AppCompatActivity activity = (AppCompatActivity) getActivity();
		activity.getSupportActionBar().setDisplayOptions(
				activity.getSupportActionBar().getDisplayOptions()
						| ActionBar.DISPLAY_SHOW_CUSTOM);

		activity.getSupportActionBar().setCustomView(layout);
	}

	private Bitmap getCurrentBitmap() {
		return ((ImagePagerAdapter) viewPager.getAdapter()).getCurrentBitmap();
	}

	private void doDownload() {
		if (isImageLoaded()) {
			new ImageController().doDownload(getCurrentBitmap());
			Snackbar.make(viewPager,
					getResources().getString(R.string.image_saved),
					Snackbar.LENGTH_LONG).show();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.set_wallpaper) {
			doSetWallpaper();
			return true;
		} else if (itemId == R.id.download) {
			doDownload();
			return true;
		} else if (itemId == R.id.like) {
			doLike();
			return true;
		} else if (itemId == R.id.add_album) {
			doAddToAlbum();
			return true;
		} else if (itemId == R.id.delete) {
			doDelete();
			return true;
		} else if (itemId == R.id.share) {
			doShareCustomDialog();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	private void doSetWallpaper() {
		if (isImageLoaded()) {
			MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
					.title(R.string.wallpaper_dialog_title)
					.items(R.array.wallpaper_dialog_items)
					.titleColorRes(R.color.white)
					.itemsCallbackSingleChoice(0,
							new MaterialDialog.ListCallbackSingleChoice() {
								@Override
								public boolean onSelection(
										MaterialDialog dialog, View view,
										int which, CharSequence text) {
									new ImageController().doSetWallpaper(
											getActivity(), getCurrentBitmap(),
											text);
									Snackbar.make(
											viewPager,
											getResources().getString(
													R.string.wallpaper_changed),
											Snackbar.LENGTH_LONG).show();
									return true;
								}
							}).positiveText(R.string.action_ok)
					.negativeText(R.string.cancel).build();
			ListView list = dialog.getListView();
			// Remove and reset the adapter so the addFooterView() method works
			// on pre 4.4 devices.
			ListAdapter adapter = list.getAdapter();
			list.setAdapter(null);

			ImageView image = new ImageView(getActivity());
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					25, 25);
			layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
			layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
			image.setScaleType(ScaleType.FIT_XY);
			image.setAdjustViewBounds(true);
			image.setImageResource(R.drawable.wallpaper_scaling);
			list.addFooterView(image, null, false);
			// Sets back the old adapter.
			list.setAdapter(adapter);

			dialog.getWindow().setBackgroundDrawableResource(
					R.color.black_overlay);
			dialog.show();
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.picture_view_pager,
				container, false);
		ViewPager pager = (ViewPager) rootView.findViewById(R.id.view_pager);
		this.viewPager = (CustomViewPager) pager;
		this.viewPager.setImages(images);
		return rootView;
	}

	protected void adjustLikeButton(int position) {
		Image curImage = images[position];
		likesView.setText(curImage.getLikes());
		for (Image image : new ImageController().getFavorites(getActivity())) {
			if (image.getUrl().equals(curImage.getUrl())) {
				setLikeEnabled(false);
				return;
			}
		}
		setLikeEnabled(true);
	}

	public void doLike() {
		if (isImageLoaded()) {
			if (ConnectionManager.isConnected(getActivity())) {
				Image curImage = images[viewPager.getCurrentItem()];
				new ImageController().likeImage(getActivity(), curImage);
				updateLikesView();
				updateButton();
				Snackbar.make(viewPager,
						getResources().getString(R.string.liked),
						Snackbar.LENGTH_LONG).show();
			} else {
				Snackbar.make(viewPager,
						getResources().getString(R.string.connection_error),
						Snackbar.LENGTH_LONG).show();
			}
		}
	}

	private void updateLikesView() {
		int curLikes = Integer.parseInt((String) likesView.getText());
		String updatedLikes = (String.valueOf(curLikes + 1));
		this.likesView.setText(updatedLikes);
	}

	private void updateButton() {
		setLikeEnabled(false);
	}

	private void setLikeEnabled(boolean enabled) {
		likeItem.setEnabled(enabled);
	}

	private boolean isImageLoaded() {
		return ((ImagePagerAdapter) viewPager.getAdapter()).getCurrentBitmap() != null;
	}

	private void doShareCustomDialog() {
		if (isImageLoaded()) {
			final Intent imageIntent = new Intent();
			imageIntent.setAction(Intent.ACTION_SEND);
			imageIntent.setType("image/jpeg");

			PackageManager pm = getActivity().getPackageManager();
			List<ResolveInfo> launchables = pm.queryIntentActivities(
					imageIntent, 0);
			Collections.sort(launchables,
					new ResolveInfo.DisplayNameComparator(pm));
			final AppAdapter adapter = new AppAdapter(pm, launchables);

			final MaterialDialog dialogWait = new MaterialDialog.Builder(
					getActivity()).content(R.string.please_wait)
					.autoDismiss(false).progress(true, 0).build();
			dialogWait.getWindow().setBackgroundDrawableResource(
					R.color.black_overlay);

			new MaterialDialog.Builder(getActivity())
					.title(R.string.choose_app)
					.titleColorRes(R.color.white)
					.adapter(adapter, new MaterialDialog.ListCallback() {

						@Override
						public void onSelection(MaterialDialog dialog,
								View itemView, final int which,
								CharSequence text) {
							new AsyncTask<Void, Void, Void>() {
								@Override
								protected void onPreExecute() {
									dialogWait.show();
									super.onPreExecute();
								}

								@Override
								protected void onPostExecute(Void result) {
									dialogWait.dismiss();
									super.onPostExecute(result);
								}

								@Override
								protected Void doInBackground(
										final Void... params) {
									Bitmap curBitmap = ((ImagePagerAdapter) viewPager
											.getAdapter()).getCurrentBitmap();
									imageIntent.putExtra(Intent.EXTRA_STREAM,
											new ImageController()
													.getImageUri(curBitmap));
									ResolveInfo launchable = adapter
											.getItem(which);
									ActivityInfo activity = launchable.activityInfo;
									ComponentName name = new ComponentName(
											activity.applicationInfo.packageName,
											activity.name);
									imageIntent
											.addCategory(Intent.CATEGORY_LAUNCHER);
									imageIntent.setComponent(name);
									startActivityForResult(imageIntent,
											SHARE_IMAGE_REQUEST_CODE);
									return null;
								}
							}.execute();
						}
					}).negativeColorRes(R.color.primary_color)
					.negativeText(R.string.cancel).show().getWindow()
					.setBackgroundDrawableResource(R.color.black_overlay);
		}
	}

	private Image getCurrentImage() {
		return images[viewPager.getCurrentItem()];
	}

	private void doDelete() {
		if (isImageLoaded()) {
			new MaterialDialog.Builder(getActivity())
					.content(R.string.action_delete_image)
					.negativeText(R.string.cancel)
					.negativeColorRes(R.color.primary_color)
					.positiveText(R.string.action_delete)
					.positiveColorRes(R.color.primary_color)
					.callback(new MaterialDialog.ButtonCallback() {
						@Override
						public void onPositive(MaterialDialog dialog) {
							final AlbumsController albumsController = new AlbumsController();
							// Get the current image.
							Image curImage = getCurrentImage();
							final Image lastDeletedImage = curImage;
							final int lastDeletedImageIndex = viewPager
									.getCurrentItem();
							Image[] images = albumsController
									.removeImageFromAlbum(getActivity(),
											curImage);
							if (images != null) {
								ImagePagerFragment.this.images = images;
							}
							Snackbar.make(viewPager, R.string.warning_deleted,
									Snackbar.LENGTH_LONG)
									.setAction(R.string.snackbar_action_undo,
											new View.OnClickListener() {

												@Override
												public void onClick(View v) {
													Image[] images = albumsController
															.reverseRemoveImageFromAlbum(
																	getActivity(),
																	lastDeletedImage);
													ImagePagerFragment.this.images = images;
													ImagePagerFragment.this.imagePosition = lastDeletedImageIndex;
													ImagePagerAdapter adapter = new ImagePagerAdapter(
															getActivity(),
															images,
															lastDeletedImageIndex,
															viewPager);
													viewPager.setImages(images);
													viewPager
															.setAdapter(adapter);
													viewPager
															.setCurrentItem(
																	lastDeletedImageIndex,
																	false);
												}
											}).show();
							// Update the adapters.
							if (images.length > 0) {
								imagePosition = 0;
							} else {
								Intent data = new Intent();
								data.putExtra(Constants.Extra.IMAGE_PARCEABLE,
										lastDeletedImage);
								getActivity();
								getActivity().setResult(Activity.RESULT_OK,
										data);
								getActivity().finish();
							}

							ImagePagerAdapter adapter = new ImagePagerAdapter(
									getActivity(), images, imagePosition,
									viewPager);
							viewPager.setImages(images);
							viewPager.setAdapter(adapter);

							super.onPositive(dialog);
						}
					}).show().getWindow()
					.setBackgroundDrawableResource(R.color.black_overlay);
		}
	}

	private void doAddToAlbum() {
		if (isImageLoaded()) {
			final AlbumsController albumsController = new AlbumsController();
			// Get the list of album names.
			List<CharSequence> albumNames = new ArrayList<CharSequence>();
			for (Album album : AlbumsData.getInstance().getAlbums()) {
				albumNames.add(album.getName());
			}
			// Show the material dialog.
			new MaterialDialog.Builder(getActivity())
					.title(R.string.action_add_album)
					.autoDismiss(false)
					.titleColorRes(R.color.primary_text_color_light)
					.items(albumNames.toArray(new CharSequence[albumNames
							.size()]))
					.itemsCallback(new MaterialDialog.ListCallback() {
						@Override
						public void onSelection(MaterialDialog dialog,
								View itemView, int which, CharSequence text) {
							// Get the chosen album.
							final Album album = AlbumsData.getInstance()
									.getAlbums()[which];
							if (albumsController.addImageToAlbum(getActivity(),
									album, getCurrentImage())) {
								// Show toast message
								Snackbar.make(
										viewPager,
										getResources().getString(
												R.string.image_added),
										Snackbar.LENGTH_LONG)
										.setAction(
												getResources()
														.getString(
																R.string.snackbar_action_undo),
												new View.OnClickListener() {

													@Override
													public void onClick(View v) {
														// Remove the image from
														// the album.
														AlbumsController albumsController = new AlbumsController();
														albumsController
																.removeImageFromAlbum(
																		getActivity(),
																		album,
																		getCurrentImage());
													}
												}).show();
								dialog.dismiss();
							} else {
								Snackbar.make(
										viewPager,
										getResources().getString(
												R.string.image_is_in_album),
										Snackbar.LENGTH_LONG).show();
							}
						}
					}).neutralText(R.string.create_album)
					.neutralColorRes(R.color.primary_color)
					.callback(new MaterialDialog.ButtonCallback() {
						@Override
						public void onNeutral(MaterialDialog dialog) {
							createNewAlbum();
							dialog.dismiss();
						}
					}).show().getWindow()
					.setBackgroundDrawableResource(R.color.black_overlay);
		}
	}

	protected void createNewAlbum() {
		// Ask for album name
		final MaterialDialog materialDialog = new MaterialDialog.Builder(
				getActivity())
				.title(R.string.create_album)
				.titleColorRes(R.color.primary_text_color_light)
				.autoDismiss(false)
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
		materialDialog.getWindow().setBackgroundDrawableResource(
				R.color.black_overlay);
		new SystemUI().setupFloatingLabelError(getActivity(), materialDialog);
		materialDialog.getActionButton(DialogAction.POSITIVE)
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						CharSequence input = materialDialog.getInputEditText()
								.getText();
						// Create album
						Album newAlbum = new Album(input.toString());
						// Update ImageData
						new AlbumsController()
								.addAlbum(getActivity(), newAlbum);
						// Update album dialog
						doAddToAlbum();
						materialDialog.dismiss();
					}
				});
		materialDialog.show();
	}

	private class AppAdapter extends ArrayAdapter<ResolveInfo> {
		private PackageManager pm = null;

		AppAdapter(PackageManager pm, List<ResolveInfo> apps) {
			super(getActivity(), R.layout.share_chooser_row, apps);
			this.pm = pm;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = newView(parent);
			}

			bindView(position, convertView);

			return (convertView);
		}

		private View newView(ViewGroup parent) {
			return (getActivity().getLayoutInflater().inflate(
					R.layout.share_chooser_row, parent, false));
		}

		private void bindView(int position, View row) {
			TextView label = (TextView) row.findViewById(R.id.chooser_label);
			label.setText(getItem(position).loadLabel(pm));
			ImageView icon = (ImageView) row.findViewById(R.id.chooser_icon);
			icon.setImageDrawable(getItem(position).loadIcon(pm));
		}
	}

}
