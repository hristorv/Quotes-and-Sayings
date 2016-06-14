package tk.example.quotesandsayings.view.adapters;

import tk.example.quotesandsayings.R;
import tk.example.quotesandsayings.model.Constants;
import tk.example.quotesandsayings.model.ImageData;
import tk.example.quotesandsayings.view.activities.MainMenuActivity;
import tk.example.quotesandsayings.view.fragments.ImageGridFragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class CategoriesGridRecyclerAdapter extends
        RecyclerView.Adapter<CategoriesGridRecyclerAdapter.ViewHolder> {

    private String[] categoriesTitles;
    private Context context;
    private DisplayImageOptions options;

    //    public final static Integer[] imageResIds = new Integer[]{
//            R.drawable.cat_top, R.drawable.cat_favourites,
//            R.drawable.cat_funny, R.drawable.cat_happiness,
//            R.drawable.cat_inspirational, R.drawable.cat_life,
//            R.drawable.cat_love, R.drawable.cat_success, R.drawable.cat_travel,
//            R.drawable.cat_wisdom};
    private boolean firstTime = true;

    public CategoriesGridRecyclerAdapter(Context context) {
        this.context = context;
        categoriesTitles = ImageData.getInstance().getCategoriesTitles();
        setImageLoaderOptions();

//        for (int i = 0; i < ImageData.getInstance().getCategories().length; i++) {
//            ImageLoader.getInstance().loadImage(ImageData.getInstance().getCategories()[i].getBackgroundUrl(), options, new SimpleImageLoadingListener());
//        }
    }

    private void setImageLoaderOptions() {
        options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisk(true).bitmapConfig(Bitmap.Config.ARGB_8888)
                .delayBeforeLoading(0)
                .resetViewBeforeLoading(true)
                        // .displayer(new FadeInBitmapDisplayer(250))
                .build();
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View itemView = inflater.inflate(R.layout.fragment_categories_item,
                parent, false);
//        if (firstTime) {
//            for (int i = 0; i < ImageData.getInstance().getCategories().length; i++) {
//                ImageLoader.getInstance().loadImage
//                        (ImageData.getInstance().getCategories()[i].getBackgroundUrl(), new ImageSize(itemView.getWidth(), itemView.getHeight()), options, new SimpleImageLoadingListener());
//            }
//            firstTime = false;
//        }
        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        //viewHolder.imageView.setImageDrawable(null);

        ImageLoader.getInstance().displayImage(
                ImageData.getInstance().getCategories()[position].getBackgroundUrl(), viewHolder.imageView,
                options);

        viewHolder.titleBar.setText(categoriesTitles[position]);
        viewHolder.itemView.setOnClickListener(new CategoriesOnClickListener(
                position));
        // Starts the animation
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
        return categoriesTitles.length;
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

    private Fragment createFragment(int position) {
        Fragment fragment;
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.Extra.CATEGORY_INDEX, position);
        bundle.putBoolean(Constants.Extra.ALBUM_BOOLEAN, false);
        bundle.putBoolean(Constants.Extra.FAVORITES_BOOLEAN,
                getFavoritesBoolean(position));
        fragment = new ImageGridFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private boolean getFavoritesBoolean(int position) {
        return position == Constants.CategoriesIndex.FAVOURITES_CATEGORY;
    }

    private class CategoriesOnClickListener implements View.OnClickListener {
        int position;

        public CategoriesOnClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            Fragment fragment = createFragment(position);
            if (fragment != null) {
                FragmentManager fragmentManager = ((AppCompatActivity) context)
                        .getSupportFragmentManager();
                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(R.animator.card_flip_right_in,
                                R.animator.card_flip_right_out,
                                R.animator.card_flip_left_in,
                                R.animator.card_flip_left_out)
                        .replace(R.id.content_frame, fragment)
                                // Add this transaction to the back stack
                        .addToBackStack(
                                Constants.FragmentNames.CATEGORIES_FRAGMENT)
                        .commit();

                // Update selected action bar title.
                MainMenuActivity activity = (MainMenuActivity) context;
                activity.setTitle(categoriesTitles[position]);

            }
        }
    }
}
