package com.vpaliy.studioq.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Transition;
import android.view.Window;
import java.util.ArrayList;
import com.vpaliy.studioq.common.eventBus.ExitEvent;
import com.vpaliy.studioq.common.eventBus.Launcher;
import com.vpaliy.studioq.common.eventBus.Registrator;
import com.vpaliy.studioq.model.MediaFile;
import com.vpaliy.studioq.fragments.GalleryFragment;
import com.vpaliy.studioq.slider.screens.MediaSliderActivity;
import com.vpaliy.studioq.common.utils.Permissions;
import com.vpaliy.studioq.common.utils.ProjectUtils;
import com.vpaliy.studioq.R;
import butterknife.ButterKnife;

import android.annotation.TargetApi;
import android.support.annotation.NonNull;
import com.squareup.otto.Subscribe;

import static com.vpaliy.studioq.common.utils.ProjectUtils.LAUNCH_SLIDER;
import static com.vpaliy.studioq.common.utils.ProjectUtils.GALLERY_FRAGMENT;

public class GalleryActivity extends AppCompatActivity
            implements GalleryFragment.ActionBarCallback {

    private GalleryFragment fragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Permissions.checkForVersion(Build.VERSION_CODES.LOLLIPOP)) {
         //    requestFeature();
        }
        setContentView(R.layout.activity_gallery);
        ButterKnife.bind(this);
        initUI(savedInstanceState);
    }


    @Override
    protected void onStart() {
        super.onStart();
        Registrator.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Registrator.unregister(this);
    }

    private void initUI(Bundle args) {
        if (args == null) {
            args = getIntent().getExtras();
            fragment = GalleryFragment.newInstance(args);
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            if (Permissions.checkForVersion(Build.VERSION_CODES.LOLLIPOP)) {
                startPostponedEnterTransition();
            } else {
                transaction.setCustomAnimations(R.anim.enter, R.anim.exit);
            }
            transaction.replace(R.id.mediaFragmentPlaceHolder, fragment, GALLERY_FRAGMENT);
            transaction.commit();
            manager.executePendingTransactions();
        }
    }


    @Override
    public void onBackPressed() {
        if(fragment!=null) {
            fragment.onBackPressed();
        }else {
            fragment=GalleryFragment.class.cast(getSupportFragmentManager().
                findFragmentByTag(GALLERY_FRAGMENT));
            if(fragment!=null) {
                onBackPressed();
            }
        }
    }


    @Subscribe
    public void startSliderActivity(Launcher<ArrayList<MediaFile>> launcher) {
        Intent intent=new Intent(this, MediaSliderActivity.class);
        intent.putExtra(ProjectUtils.MEDIA_DATA,launcher.data);
        intent.putExtra(ProjectUtils.POSITION,launcher.position);
        startActivityForResult(intent, LAUNCH_SLIDER);
    }

    @Subscribe
    public void onExit(@NonNull ExitEvent exitEvent) {
        if(exitEvent.intent!=null) {
            setResult(RESULT_OK, exitEvent.intent);
        }
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case LAUNCH_SLIDER:
                    break;
            }
        }
    }

    @Override
    public void hookUp(@NonNull Toolbar toolbar) {
        if(getSupportActionBar()==null) {
            setSupportActionBar(toolbar);
        }
    }

    @Override
    public void invalidateOptionsMenu() {
        if(getSupportActionBar()!=null) {
            getSupportActionBar().invalidateOptionsMenu();
        }
    }

    @TargetApi(21)
    private void requestFeature() {
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        Transition enterTransition=new Explode();
        enterTransition.excludeTarget(android.R.id.navigationBarBackground,true);
        enterTransition.excludeTarget(android.R.id.statusBarBackground,true);
        enterTransition.excludeTarget(R.id.actionButton,true);
        enterTransition.setDuration(200);
        getWindow().setEnterTransition(enterTransition);
        getWindow().setSharedElementsUseOverlay(false);
        postponeEnterTransition();

    }


}
