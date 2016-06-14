package tk.example.quotesandsayings.view.activities;

import tk.example.quotesandsayings.R;
import tk.example.quotesandsayings.controller.AlbumsController;
import tk.example.quotesandsayings.controller.PreferencesListener;
import tk.example.quotesandsayings.model.Constants;
import tk.example.quotesandsayings.model.Image;
import tk.example.quotesandsayings.view.adapters.ImageGridRecyclerAdapter;
import tk.example.quotesandsayings.view.adapters.TabPagerAdapter;
import tk.example.quotesandsayings.view.fragments.AlbumsFragment;
import tk.example.quotesandsayings.view.fragments.CategoriesFragment;
import tk.example.quotesandsayings.view.fragments.HelpFragment;
import tk.example.quotesandsayings.view.fragments.ImageGridFragment;
import tk.example.quotesandsayings.view.fragments.SettingsFragment;


import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.Builder;

import java.util.ArrayList;

public class MainMenuActivity extends AppCompatActivity {

    private static final int GRID_IMAGE_STARTING_INDEX = 0;
    private static final String TITLE_KEY = "title";
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private CharSequence mTitle;
    ActionBar actionBar;
    TextView toolbarTitle;
    SharedPreferences.OnSharedPreferenceChangeListener listener;
    SharedPreferences prefs;
    private boolean isAlbum;
    private NavigationView nvDrawer;
    protected MenuItem mPreviousMenuItem;
    private boolean isOffline;
    private ViewPager tabViewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        listener = new PreferencesListener(this);
        prefs = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        isOffline = getIntent().getExtras().getBoolean(Constants.Extra.IS_OFFLINE);
        // Set the toolbar,to act like actionbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        // Set the title if the orientation is changed.
        if (savedInstanceState != null) {
            CharSequence title = savedInstanceState.getCharSequence(TITLE_KEY);
            setTitleNoAnimation(title);
            // Set the title in the beginning without the animation,since the
            // toolbarTitle view is not yet inflated.
        } else {
            setTitleNoAnimation(getResources().getString(
                    R.string.nav_categories));
        }
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Setup tabs
        tabViewPager = (ViewPager) findViewById(R.id.tab_viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        // Find our drawer view
        nvDrawer = (NavigationView) findViewById(R.id.nav_view);

        // Setup drawer view
        setupDrawerContent(nvDrawer);

        // enabling action bar application icon and behaving it as toggle button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.app_name, // navigation menu toggle icon
                R.string.app_name
                // accessibility
        ) {

            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
        if (savedInstanceState == null) {
            // on first time display view for first navigation item
            openDrawerItem(getResources().getString(R.string.nav_categories),
                    R.id.drawer_home);
            // Open help fragment, if the user opens application for the first
            // time.
            if (prefs.getBoolean(Constants.PreferencesKeys.KEY_FIRST_TIME_HELP,
                    true)) {
                openHelp();
                prefs.edit()
                        .putBoolean(
                                Constants.PreferencesKeys.KEY_FIRST_TIME_HELP,
                                false).commit();
            }
        }
    }

    public void checkIfOffline() {
        if (isOffline) {
            Snackbar.make(toolbarTitle,
                    R.string.offline_alert, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.snackbar_action_reconnect,
                            new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    Intent i = new Intent(MainMenuActivity.this,
                                            InitializationActivity.class);
                                    startActivity(i);
                                    MainMenuActivity.this.finish();
                                }
                            }).show();
        }
    }

    private void setupDrawerContent(NavigationView navDrawer) {
        navDrawer
                .setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        openDrawerItem(menuItem.getTitle(), menuItem.getItemId());
                        // Update highlighted item in the navigation menu
                        nvDrawer.setCheckedItem(menuItem.getItemId());
                        return true;
                    }
                });

    }

    private void setTitleNoAnimation(CharSequence title) {
        mTitle = title;
        toolbarTitle.setText(title);
    }


//    private void displayView(CharSequence title, int id) {
//        // update the main content by replacing fragments
//        Fragment fragment = null;
//        fragment = initializeFragmentByCategory(id, fragment);
//        String tag;
//        if (title.equals("Albums")) {
//            tag = "AlbumsFragment";
//        } else {
//            tag = "";
//        }
////        if (fragment != null) {
////            FragmentManager fragmentManager = getFragmentManager();
////            fragmentManager
////                    .beginTransaction()
////                    .setCustomAnimations(R.animator.slide_in_left,
////                            R.animator.slide_out_left,
////                            R.animator.slide_in_left, R.animator.slide_out_left)
////                    .replace(R.id.tab_viewpager, fragment, tag).commit();
//            setupTabs(id);
//            // update the title, then close the drawer
//            setTitle(title);
//            drawerLayout.closeDrawers();
// //       } else {
//            // If fragment is null,we are opening About or Help which is dialog
//            // and we need only to close the drawer since the old fragment is
//            // still active
//  //          drawerLayout.closeDrawers();
//  //      }
//    }

    private void initTabs(int id) {
        TabPagerAdapter adapter = new TabPagerAdapter(getSupportFragmentManager());
        ArrayList<Drawable> icons = new ArrayList<Drawable>();
        switch (id) {
            case R.id.drawer_home:
                adapter.addFrag(new CategoriesFragment(), getResources().getString(R.string.tab_categories));
                adapter.addFrag(new CategoriesFragment(), getResources().getString(R.string.tab_timeline));
                adapter.addFrag(new CategoriesFragment(), getResources().getString(R.string.tab_search));
                icons.add(tintDrawable(R.drawable.ic_tab_categories, R.color.tab_unselected));
                icons.add(tintDrawable(R.drawable.ic_timeline_white_36dp, R.color.tab_unselected));
                icons.add(tintDrawable(R.drawable.ic_search_white_36dp, R.color.tab_unselected));
                break;
            case R.id.drawer_pictures:
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.Extra.CATEGORY_INDEX, 2);
                Fragment fragment = new ImageGridFragment();
                fragment.setArguments(bundle);
                Bundle bundle2 = new Bundle();
                bundle2.putInt(Constants.Extra.CATEGORY_INDEX, 2);
                Fragment fragment2 = new ImageGridFragment();
                fragment2.setArguments(bundle2);
                Bundle bundle3 = new Bundle();
                bundle3.putInt(Constants.Extra.CATEGORY_INDEX, 2);
                Fragment fragment3 = new ImageGridFragment();
                fragment3.setArguments(bundle3);
                adapter.addFrag(fragment, getResources().getString(R.string.tab_collection));
                adapter.addFrag(fragment2, getResources().getString(R.string.tab_favorites));
                adapter.addFrag(fragment3, getResources().getString(R.string.tab_trending));
                icons.add(tintDrawable(R.drawable.ic_create_white, R.color.tab_unselected));
                icons.add(tintDrawable(R.drawable.ic_thumb_up_white_36dp, R.color.tab_unselected));
                icons.add(tintDrawable(R.drawable.ic_trending_up_white_36dp, R.color.tab_unselected));
                break;
            case R.id.drawer_albums:
                Fragment albumsFragment = new AlbumsFragment();
                adapter.addFrag(albumsFragment, getResources().getString(R.string.tab_browse));
                adapter.addFrag(new CategoriesFragment(), getResources().getString(R.string.tab_collection));
                adapter.addFrag(new CategoriesFragment(), getResources().getString(R.string.tab_favorites));
                adapter.addFrag(new CategoriesFragment(), getResources().getString(R.string.tab_trending));
                icons.add(tintDrawable(R.drawable.ic_tab_browse, R.color.tab_unselected));
                icons.add(tintDrawable(R.drawable.ic_create_white, R.color.tab_unselected));
                icons.add(tintDrawable(R.drawable.ic_thumb_up_white_36dp, R.color.tab_unselected));
                icons.add(tintDrawable(R.drawable.ic_trending_up_white_36dp, R.color.tab_unselected));
                break;
            case R.id.drawer_artists:
                adapter.addFrag(new CategoriesFragment(), getResources().getString(R.string.tab_browse));
                adapter.addFrag(new CategoriesFragment(), getResources().getString(R.string.tab_favorites));
                adapter.addFrag(new CategoriesFragment(), getResources().getString(R.string.tab_trending));
                icons.add(tintDrawable(R.drawable.ic_tab_browse, R.color.tab_unselected));
                icons.add(tintDrawable(R.drawable.ic_thumb_up_white_36dp, R.color.tab_unselected));
                icons.add(tintDrawable(R.drawable.ic_trending_up_white_36dp, R.color.tab_unselected));
                break;
        }

        tabViewPager.setAdapter(adapter);
        // Set the title when changing tabs.
        tabViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                TabPagerAdapter adapter = (TabPagerAdapter) tabViewPager.getAdapter();
                setTitle(adapter.getTitle(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout.setOnTabSelectedListener(null);
        tabLayout.setupWithViewPager(tabViewPager);

        // Tab listener for changing icons and also fixing clicking on tabs.
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.setIcon(tintDrawable(tab.getIcon(), R.color.white));
                if (tab.getPosition() != tabViewPager.getCurrentItem()) {
                    tabViewPager.setCurrentItem(tab.getPosition(), true);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.setIcon(tintDrawable(tab.getIcon(), R.color.tab_unselected));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        // Set the icons for each tab.
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            tabLayout.getTabAt(i).setIcon(icons.get(i));
        }
        // When starting tab from drawer, set the first item icon and title.
        TabLayout.Tab tab = tabLayout.getTabAt(0);
        tab.setIcon(tintDrawable(tab.getIcon(), R.color.white));
        setTitle(adapter.getTitle(0));
    }



    private void openDrawerItem(CharSequence title, int id) {

        switch (id) {
            case R.id.drawer_home:
                initTabs(id);
                drawerLayout.closeDrawers();
                break;
            case R.id.drawer_pictures:
                initTabs(id);
                drawerLayout.closeDrawers();
                break;
            case R.id.drawer_albums:
                initTabs(id);
                drawerLayout.closeDrawers();
                break;
            case R.id.drawer_artists:
                initTabs(id);
                drawerLayout.closeDrawers();
                break;
            case R.id.drawer_schedulers:
                setTitle(title);
                drawerLayout.closeDrawers();
                break;
            case R.id.drawer_account:
                setTitle(title);
                drawerLayout.closeDrawers();
                openAccount();
                break;
            case R.id.drawer_feedback:
                openAbout();
                drawerLayout.closeDrawers();
                break;
            case R.id.drawer_logout:
                openHelp();
                drawerLayout.closeDrawers();
                break;
            default:
                break;
        }
    }

    private Drawable tintDrawable(Drawable drawableInput, int colorID) {
        Drawable drawable = DrawableCompat.wrap(drawableInput);
        ColorStateList colorSelector = getResources().getColorStateList(colorID);
        DrawableCompat.setTintList(drawable, colorSelector);
        return drawable;
    }

    private Drawable tintDrawable(int drawableID, int colorID) {
        Drawable drawable = DrawableCompat.wrap(getResources().getDrawable(drawableID));
        ColorStateList colorSelector = getResources().getColorStateList(colorID);
        DrawableCompat.setTintList(drawable, colorSelector);
        return drawable;
    }

    private void clearBackStackEntries() {
        getFragmentManager().popBackStack(
                Constants.FragmentNames.ALBUMS_FRAGMENT,
                FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getFragmentManager().popBackStack(
                Constants.FragmentNames.CATEGORIES_FRAGMENT,
                FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }


    private void openAccount() {

        Builder alertDialog = new MaterialDialog.Builder(this);
        alertDialog.customView(R.layout.account_dialog, false);

        alertDialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * Handles the Action bar items.Both settings and about items are from the
     * overflow menu.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle navigation drawer on selecting action bar application
        // icon/title
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Changes the action bar title.
     */
    @Override
    public void setTitle(final CharSequence title) {
        mTitle = title;
        Animation makeOut = AnimationUtils.makeOutAnimation(
                MainMenuActivity.this, false);
        makeOut.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationEnd(Animation animation) {
                toolbarTitle.setText(title);
                toolbarTitle.startAnimation(AnimationUtils.makeInAnimation(
                        MainMenuActivity.this, true));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationStart(Animation animation) {

            }
        });
        toolbarTitle.startAnimation(makeOut);

    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(TITLE_KEY, mTitle);
    }

    /**
     * Pass any configuration change to the drawer toggles.
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Starts the About menu activity.
     */
    private void openAbout() {
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.about_layout, null);
        Builder alertDialog = new MaterialDialog.Builder(this);
        alertDialog.title(R.string.about_title);
        alertDialog.customView(dialogView, false);
        // Get the version number.
        TextView versionView = (TextView) dialogView
                .findViewById(R.id.about_version);
        String version = "";
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            Log.e("Open About", "Version name not found!");
            e.printStackTrace();
        }
        versionView.setText(versionView.getText() + " " + version);

        alertDialog.neutralText(R.string.action_ok);
        alertDialog.show();
    }

    private void openHelp() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        HelpFragment frag = new HelpFragment();
        frag.show(ft, Constants.FragmentNames.HELP_FRAGMENT);

    }

    @Override
    protected void onResume() {
        super.onResume();
        prefs.registerOnSharedPreferenceChangeListener(listener);
        checkIfOffline();
    }

    @Override
    protected void onPause() {
        super.onPause();
        prefs.unregisterOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
            if (!isAlbum) {
                setTitle(getResources().getString(R.string.nav_categories));
            } else {
                setTitle(getResources().getString(R.string.nav_albums));
            }
        } else {
            // Ask the user for exit confirmation.
            new MaterialDialog.Builder(this).content(R.string.exit_confirm)
                    .negativeText(R.string.cancel)
                    .negativeColorRes(R.color.primary_color)
                    .positiveText(R.string.action_exit)
                    .positiveColorRes(R.color.primary_color)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            // Exit the application
                            MainMenuActivity.super.onBackPressed();
                            super.onPositive(dialog);
                        }
                    }).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    final Intent data) {
        if (requestCode == Constants.Extra.REQUEST_CODE_IMAGE) {
            if (resultCode == RESULT_OK) {
                Snackbar.make(toolbarTitle, R.string.warning_deleted,
                        Snackbar.LENGTH_LONG)
                        .setAction(R.string.snackbar_action_undo,
                                new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        Image lastDeletedImage = data
                                                .getExtras()
                                                .getParcelable(
                                                        Constants.Extra.IMAGE_PARCEABLE);
                                        Image[] images = new AlbumsController()
                                                .reverseRemoveImageFromAlbum(
                                                        MainMenuActivity.this,
                                                        lastDeletedImage);
                                        Fragment currentFragment = getSupportFragmentManager()
                                                .findFragmentByTag(
                                                        "ImageGridFragment");
                                        if (currentFragment instanceof ImageGridFragment) {
                                            ImageGridRecyclerAdapter adapter = ((ImageGridFragment) currentFragment)
                                                    .getAdapter();
                                            adapter.setImages(images);
                                            adapter.notifyItemInserted(GRID_IMAGE_STARTING_INDEX);
                                        }
                                    }
                                }).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void setIsAlbum(boolean isAlbum) {
        this.isAlbum = isAlbum;
    }


}
