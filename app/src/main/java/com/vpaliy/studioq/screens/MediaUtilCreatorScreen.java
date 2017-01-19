package com.vpaliy.studioq.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import com.vpaliy.studioq.R;
import com.vpaliy.studioq.media.MediaFile;
import com.vpaliy.studioq.fragments.MediaUtilReviewFragment;
import com.vpaliy.studioq.fragments.MediaUtilSelectionFragment;
import com.vpaliy.studioq.utils.ProjectUtils;

public class MediaUtilCreatorScreen extends AppCompatActivity
        implements MediaUtilSelectionFragment.OnMediaSetCreatedListener,
        MediaUtilReviewFragment.OnMediaDataSetReviewedListener{

    private static final String  TAG=MediaUtilCreatorScreen.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_util_creator_layout);

        Bundle intentData=getIntent().getExtras();
        ArrayList<MediaFile> mediaFileList= intentData.getParcelableArrayList(ProjectUtils.MEDIA_DATA);
        final FragmentManager manager=getSupportFragmentManager();
        final FragmentTransaction transaction=manager.beginTransaction();
        transaction.replace(R.id.fragmentPlaceHolder,
                MediaUtilSelectionFragment.newInstance(mediaFileList), ProjectUtils.SELECTION_FRAGMENT)
                .commit();

    }


    @Override
    public void onMediaSetCreated(ArrayList<MediaFile> mediaFileList) {

        final FragmentManager manager=getSupportFragmentManager();
        manager.beginTransaction()
                .hide(manager.findFragmentByTag(ProjectUtils.SELECTION_FRAGMENT))
                .add(R.id.fragmentPlaceHolder,
                        MediaUtilReviewFragment.newInstance(mediaFileList), ProjectUtils.REVIEW_FRAGMENT)
                .addToBackStack(ProjectUtils.SELECTION_FRAGMENT)
                .commit();
    }


    @Override
    public void onMediaSetReviewed(Intent data) {
        setResult(RESULT_OK,data);
        finish();
    }


}
