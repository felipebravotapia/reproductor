package com.felipebravo.reproductor.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;


import com.felipebravo.reproductor.R;
import com.felipebravo.reproductor.adapters.SongAdapter;
import com.felipebravo.reproductor.database.DatabaseClass;
import com.felipebravo.reproductor.databinding.LayoutBottomSheetLikeBinding;
import com.felipebravo.reproductor.entity.Song;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class BottomSheetFavoriteSong extends BottomSheetDialogFragment {


    BottomSheetBehavior bottomSheetBehavior;
    LayoutBottomSheetLikeBinding bi;
    Animation barAminShow, barAminHiden;
    DatabaseClass db;

    private RecyclerView mRecyclerView;
    public SongAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    List<Song> songList;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog bottomSheet = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view = View.inflate(getContext(), R.layout.layout_bottom_sheet_like, null);

        bi = DataBindingUtil.bind(view);

        bottomSheet.setContentView(view);

        barAminShow = AnimationUtils.loadAnimation(getContext(), R.anim.anim_bar_show);
        barAminHiden= AnimationUtils.loadAnimation(getContext(), R.anim.anim_bar_hiden);

        bottomSheetBehavior = BottomSheetBehavior.from((View) (view.getParent()));

        bottomSheetBehavior.setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);

        bi.extraSpace.setMinimumHeight((Resources.getSystem().getDisplayMetrics().heightPixels) / 2);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                if (BottomSheetBehavior.STATE_EXPANDED == i) {
                    slideUp(bi.appBarLayout);
                    showView(bi.appBarLayout, getActionBarSize());
                    hideAppBar(bi.profileLayout);

                }
                if (BottomSheetBehavior.STATE_COLLAPSED == i) {
                    slideDown(bi.appBarLayout);
                    hideAppBar(bi.appBarLayout);
                    showView(bi.profileLayout, getActionBarSize());
                }

                if (BottomSheetBehavior.STATE_HIDDEN == i) {
                    dismiss();
                }

            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

        bi.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismiss();
            }
        });

        hideAppBar(bi.appBarLayout);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        db = Room.databaseBuilder(getContext(),
                DatabaseClass.class, "myDatabase").allowMainThreadQueries().build();
        getAllData();
        return bottomSheet;
    }


    private void getAllData() {
        List<Song> songList = new ArrayList<>();
        songList = db.daoClass().getSongs();

        if (songList.size() > 0) {
            mAdapter = new SongAdapter(songList);
            mRecyclerView.setAdapter(mAdapter);

        } else {
            Toast.makeText(getContext(), "Sin canciones favoritas", Toast.LENGTH_SHORT).show();
        }
    }


    public void slideUp(View view){
        view.setVisibility(View.VISIBLE);
        view.startAnimation(barAminShow);
    }

    public void slideDown(View view){

        view.startAnimation(barAminHiden);
    }

    @Override
    public void onStart() {
        super.onStart();

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void hideAppBar(View view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = 0;
        view.setLayoutParams(params);

    }

    private void showView(View view, int size) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = size;
        view.setLayoutParams(params);
    }

    private int getActionBarSize() {
        final TypedArray array = getContext().getTheme().obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
        int size = (int) array.getDimension(0, 0);
        return size;
    }
}