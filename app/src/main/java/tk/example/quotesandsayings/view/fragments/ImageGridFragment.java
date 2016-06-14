package tk.example.quotesandsayings.view.fragments;

import tk.example.quotesandsayings.R;
import tk.example.quotesandsayings.controller.AlbumsController;
import tk.example.quotesandsayings.controller.ImageController;
import tk.example.quotesandsayings.model.Album;
import tk.example.quotesandsayings.model.AlbumsData;
import tk.example.quotesandsayings.model.Constants;
import tk.example.quotesandsayings.model.Image;
import tk.example.quotesandsayings.model.ImageData;
import tk.example.quotesandsayings.view.SystemUI;
import tk.example.quotesandsayings.view.activities.MainMenuActivity;
import tk.example.quotesandsayings.view.adapters.AlbumsGridRecyclerAdapter;
import tk.example.quotesandsayings.view.adapters.ImageGridRecyclerAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.design.widget.FloatingActionButton.OnVisibilityChangedListener;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnDismissListener;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

public class ImageGridFragment extends Fragment {
    private static final String ALBUMS_TITLE = "Albums";
    public static final int INDEX = 1;
    private Image[] images;
    private int category = -1; // - 1 , prevents from default being 0 which is
    // associated with Top category index.
    private RecyclerView grid;
    private ImageGridRecyclerAdapter adapter;
    private int album;
    private boolean isAlbum;
    private FloatingActionButton fab;
    private View popupAnchor;
    private boolean isFavorites;
    private static final int MAX_CHARS_ALBUM_NAME = 10;
    protected static final int MIN_NAME_SIZE = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getBundleCategory();

    }

    private void getBundleCategory() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            isAlbum = bundle.getBoolean(Constants.Extra.ALBUM_BOOLEAN);
            isFavorites = bundle.getBoolean(Constants.Extra.FAVORITES_BOOLEAN);
            if (!isAlbum) {
                category = bundle.getInt(Constants.Extra.CATEGORY_INDEX);
                images = new ImageController()
                        .getCategoryImagesByIndex(category);
            } else {
                album = bundle.getInt(Constants.Extra.ALBUM_INDEX);
                images = new AlbumsController().getAlbumImagesByIndex(album);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pictures_grid,
                container, false);
        grid = (RecyclerView) rootView.findViewById(R.id.grid);
        this.adapter = new ImageGridRecyclerAdapter(getActivity(), images,
                isAlbum, isFavorites, category, album);
        grid.setAdapter(adapter);
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 1);
        grid.setLayoutManager(manager);
        grid.setHasFixedSize(true);
        new SystemUI().adjustGridColumnNum(getActivity(), grid, false);
        popupAnchor = getActivity().findViewById(R.id.popup_anchor);
        if (isAlbum) {
            setupFloatingActionButton();
        } else {
            fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
            fab.setClickable(false);
        }

        return rootView;
    }

    public ImageGridRecyclerAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isAlbum) {
            images = new AlbumsController().getAlbumImagesByIndex(album);
            ((ImageGridRecyclerAdapter) this.adapter).setImages(images);
        }
        if (category == Constants.CategoriesIndex.TOP_CATEGORY) {
            images = ImageData.getInstance().getImagesTop();
            this.adapter.setImages(images);
        }
        this.adapter.notifyDataSetChanged();
    }

    private void setupFloatingActionButton() {
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        if (fab.isShown()) {
            fab.hide(new OnVisibilityChangedListener() {
                @Override
                public void onHidden(FloatingActionButton fab) {
                    fab.setImageResource(R.drawable.ic_edit);
                    fab.show();
                    super.onHidden(fab);
                }
            });
        } else {
            fab.setImageResource(R.drawable.ic_edit);
            fab.bringToFront();
            fab.show();
        }
        fab.setClickable(true);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                fab.hide();
                initAnimation().show();

            }
        });
    }

    private PopupMenu initAnimation() {
        PopupMenu popup = new PopupMenu(getActivity(), popupAnchor);
        popup.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(PopupMenu menu) {
                fab.show();
            }
        });
       // popup.setOnMenuItemClickListener(new MyOnMenuClickListener());
        popup.inflate(R.menu.album_popup);
        return popup;
    }

 /*   private class MyOnMenuClickListener implements OnMenuItemClickListener {

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.album_delete:
                    deleteAlbum();
                    return true;
                case R.id.album_rename:
                    renameAlbum();
                    return true;
                default:
                    return false;
            }
        }

    }

    public void deleteAlbum() {
        final AlbumsController albumsController = new AlbumsController();
        final Context context = getActivity();
        albumsController.deleteAlbum(getActivity(), album);
        // Exit fragment.
        getActivity().getFragmentManager().popBackStack();
        // Save activity instance so we can use it after fragment is detached.
        final Activity mainActivity = getActivity();
        // Change title.
        ((MainMenuActivity) getActivity()).setTitle(ALBUMS_TITLE);
        // Show toast message
        View baseView = ((Activity) getActivity()).findViewById(R.id.fab);
        Snackbar.make(baseView, R.string.warning_deleted, Snackbar.LENGTH_LONG)
                .setCallback(new Snackbar.Callback() {
                    boolean isShown;

                    @Override
                    public void onShown(Snackbar snackbar) {
                        isShown = true;
                        super.onShown(snackbar);
                    }

                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        if (isShown) {
                            ((MainMenuActivity) context).checkIfOffline();
                            isShown = false;
                        }
                        super.onDismissed(snackbar, event);
                    }
                })
                .setAction(R.string.snackbar_action_undo,
                        new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                albumsController.reverseDeleteAlbum(context);
                                Fragment currentFragment = mainActivity
                                        .getFragmentManager()
                                        .findFragmentByTag("AlbumsFragment");
                                if (currentFragment instanceof AlbumsFragment) {
                                    ((AlbumsFragment) currentFragment)
                                            .getAdapter().updateAlbumsTitles();
                                    ((AlbumsFragment) currentFragment)
                                            .getAdapter()
                                            .notifyItemInserted(
                                                    AlbumsData
                                                            .getInstance()
                                                            .getDeletedAlbumIndex());
                                    ;
                                }

                            }
                        }).show();
    }

    public void renameAlbum() {
        // Ask for album name
        final MaterialDialog materialDialog = new MaterialDialog.Builder(
                getActivity())
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
        new SystemUI().setupFloatingLabelError(getActivity(), materialDialog);
        materialDialog.getActionButton(DialogAction.POSITIVE)
                .setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        CharSequence input = materialDialog.getInputEditText()
                                .getText();
                        final AlbumsController albumsController = new AlbumsController();
                        albumsController.renameAlbum(getActivity(),
                                input.toString(), album);
                        View baseView = ((Activity) getActivity())
                                .findViewById(R.id.fab);
                        final Activity activity = getActivity();
                        Snackbar.make(baseView, R.string.warning_renamed,
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
                                            ((MainMenuActivity) getActivity())
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
                                                                activity, album);
                                                // If we hit the back button
                                                // while the Snackbar is active
                                                // and the current fragment is
                                                // AlbumsFragment we have to
                                                // update the adapter
                                                Fragment currentFragment = activity
                                                        .getFragmentManager()
                                                        .findFragmentByTag(
                                                                "AlbumsFragment");
                                                if (currentFragment instanceof AlbumsFragment) {
                                                    ((AlbumsFragment) currentFragment)
                                                            .getAdapter()
                                                            .updateAlbumsTitles();
                                                    ((AlbumsFragment) currentFragment)
                                                            .getAdapter()
                                                            .notifyDataSetChanged();
                                                }
                                                // If we are not in the
                                                // ImageGridFragment when the
                                                // user press the undo button
                                                // the title is not changed.
                                                currentFragment = activity
                                                        .getFragmentManager()
                                                        .findFragmentByTag(
                                                                "ImageGridFragment");
                                                if (currentFragment instanceof ImageGridFragment) {
                                                    ((MainMenuActivity) activity)
                                                            .setTitle(AlbumsData
                                                                    .getInstance()
                                                                    .getOldAlbumName());
                                                }
                                            }
                                        }).show();

                        materialDialog.dismiss();
                        ((MainMenuActivity) getActivity()).setTitle(input
                                .toString());
                    }
                });
        materialDialog.show();

    }
    */

}
