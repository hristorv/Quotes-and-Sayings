package tk.example.quotesandsayings.view.fragments;

import tk.example.quotesandsayings.R;
import tk.example.quotesandsayings.view.SystemUI;
import tk.example.quotesandsayings.view.activities.MainMenuActivity;
import tk.example.quotesandsayings.view.adapters.CategoriesGridRecyclerAdapter;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.TimeInterpolator;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class CategoriesFragment extends Fragment {
    private RecyclerView grid;
    private CategoriesGridRecyclerAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainMenuActivity) getActivity()).setIsAlbum(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_categories_grid,
                container, false);
        grid = (RecyclerView) rootView.findViewById(R.id.categories_grid);
        adapter = new CategoriesGridRecyclerAdapter(getActivity());
        grid.setAdapter(adapter);
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 1);
        grid.setLayoutManager(manager);
        grid.setHasFixedSize(true);

        FloatingActionButton fab = (FloatingActionButton) getActivity()
                .findViewById(R.id.fab);
        fab.bringToFront();
        fab.setImageResource(R.drawable.ic_add_album);
        fab.show();
        fab.setClickable(true);
//        fab.hide();
//        fab.setClickable(false);

        new SystemUI().adjustGridColumnNum(getActivity(), grid, true);
        return rootView;
    }

}
