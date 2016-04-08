package tk.example.quotesandsayings.view.fragments;

import tk.example.quotesandsayings.R;
import tk.example.quotesandsayings.controller.AlbumsController;
import tk.example.quotesandsayings.model.Album;
import tk.example.quotesandsayings.model.AlbumsData;
import tk.example.quotesandsayings.model.ImageData;
import tk.example.quotesandsayings.view.SystemUI;
import tk.example.quotesandsayings.view.activities.MainMenuActivity;
import tk.example.quotesandsayings.view.adapters.AlbumsGridRecyclerAdapter;
import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.FloatingActionButton.OnVisibilityChangedListener;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

public class AlbumsFragment extends Fragment {
	private RecyclerView grid;
	private FloatingActionButton fab;
	private static final int MAX_CHARS_ALBUM_NAME = 10;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MainMenuActivity parentActivity = (MainMenuActivity) getActivity();
		parentActivity.setIsAlbum(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_categories_grid,
				container, false);
		grid = (RecyclerView) rootView.findViewById(R.id.categories_grid);
		grid.setAdapter(new AlbumsGridRecyclerAdapter(getActivity()));
		GridLayoutManager manager = new GridLayoutManager(getActivity(), 1);
		grid.setLayoutManager(manager);
		grid.setHasFixedSize(true);
		setupFloatingActionButton();
		new SystemUI().adjustGridColumnNum(getActivity(), grid, true);
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		AlbumsGridRecyclerAdapter adapter = (AlbumsGridRecyclerAdapter) grid
				.getAdapter();
		adapter.updateAlbumsTitles();
		adapter.notifyDataSetChanged();
	}

	public AlbumsGridRecyclerAdapter getAdapter() {
		return (AlbumsGridRecyclerAdapter) grid.getAdapter();
	}

	private void setupFloatingActionButton() {
		fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
		if (fab.isShown()) {
			fab.hide(new OnVisibilityChangedListener() {
				@Override
				public void onHidden(FloatingActionButton fab) {
					fab.setImageResource(R.drawable.ic_add_album);
					fab.show();
					super.onHidden(fab);
				}
			});
		} else {
			fab.bringToFront();
			fab.setImageResource(R.drawable.ic_add_album);
			fab.show(new OnVisibilityChangedListener() {
				@Override
				public void onShown(FloatingActionButton fab) {
					((MainMenuActivity) getActivity()).checkIfOffline();
					super.onShown(fab);
				}
			});
		}
		fab.setClickable(true);
		fab.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				createNewAlbum();
			}
		});
	}

	protected void createNewAlbum() {
		// Ask for album name
		final MaterialDialog materialDialog = new MaterialDialog.Builder(
				getActivity())
				.title(R.string.create_album)
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
						}).callback(new MaterialDialog.ButtonCallback() {
					public void onPositive(MaterialDialog dialog) {
						CharSequence input = dialog.getInputEditText()
								.getText();
						new AlbumsController().createNewAlbum(getActivity(),
								input.toString());
						AlbumsGridRecyclerAdapter adapter = (AlbumsGridRecyclerAdapter) grid
								.getAdapter();
						adapter.updateAlbumsTitles();
						adapter.notifyItemInserted(AlbumsData.getInstance()
								.getAlbums().length - 1);
						dialog.dismiss();
					}
				}).show();
		new SystemUI().setupFloatingLabelError(getActivity(), materialDialog);
		materialDialog.show();
	}
}
