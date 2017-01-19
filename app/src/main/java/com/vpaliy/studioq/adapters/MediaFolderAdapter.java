package com.vpaliy.studioq.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.DrawableCrossFadeFactory;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.vpaliy.studioq.R;
import com.vpaliy.studioq.media.MediaFile;
import com.vpaliy.studioq.media.MediaFolder;
import com.vpaliy.studioq.MultiChoiceMode.MultiChoiceMode;
import com.vpaliy.studioq.utils.OnLaunchGalleryActivity;

public class MediaFolderAdapter extends BaseMediaAdapter<MediaFolder> {

    private final static String TAG=MediaFolderAdapter.class.getSimpleName();

    private OnLaunchGalleryActivity mOnLaunchGallery;
    private ArrayList<MediaFolder> currentFolderList;
    private Mode adapterMode=Mode.ALL;
    private  final Bitmap paletteBitmap;
    private List<Palette.Swatch> swatchList;
    private boolean[] isAnimated;


    public MediaFolderAdapter(Context context, MultiChoiceMode
            multiChoiceModeListener, ArrayList<MediaFolder> mDataModel) {
        super(context, multiChoiceModeListener,mDataModel);
        this.mOnLaunchGallery = (OnLaunchGalleryActivity) (context);
        this.currentFolderList=mDataModel;
        this.paletteBitmap= BitmapFactory.decodeResource(context.getResources(), R.drawable.bitmap);
        this.isAnimated=new boolean[mDataModel.size()];
        initSwatchList();
    }

    private void initSwatchList() {
        if(paletteBitmap!=null) {
            if (!paletteBitmap.isRecycled()) {
                Palette.from(paletteBitmap).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        swatchList=new ArrayList<>(palette.getSwatches());
                    }
                });
            }
        }
    }


    @Override
    public void onActionItemClicked(MenuItem item, int[] mCheckedIndices) {
        //TODO deleting folders here
    }

    public class FolderViewHolder extends SelectableViewHolder {

        private ImageView icon;
        private ImageView mMainImage;
        private TextView mFolderName;
        private TextView mImageCount;
        private RelativeLayout bodyLayout;

        public FolderViewHolder(View itemView) {
            super(itemView);
            this.icon=(ImageView)(itemView.findViewById(R.id.icon));
            this.mMainImage=(ImageView)(itemView.findViewById(R.id.mainImage));
            this.mFolderName=(TextView) (itemView.findViewById(R.id.folderName));
            this.mImageCount=(TextView)(itemView.findViewById(R.id.imageCount));
            this.bodyLayout=(RelativeLayout)(itemView.findViewById(R.id.cardBody));
        }

        @Override
        public void setBackground(int position) {
        /*TODO set a background for folder */

            if(isSelected(position)) {
                if(!isAnimated[position]) {
                    itemView.animate()
                            .scaleX(SCALE_ITEM)
                            .scaleY(SCALE_ITEM)
                            .setDuration(180)
                            .start();
                    isAnimated[position]=!isAnimated[position];
                }else {
                    itemView.setScaleX(SCALE_ITEM);
                    itemView.setScaleY(SCALE_ITEM);
                }
            }else if(itemView.getScaleX()!=1.f){
                itemView.setScaleX(1.f);
                itemView.setScaleY(1.f);
                isAnimated[position]=false;
            }
        }

        private void applySwatch() {
            Palette.Swatch currentSwatch=swatchList.get(getAdapterPosition() % 16);
            if(currentSwatch!=null) {
                bodyLayout.setBackgroundColor(currentSwatch.getRgb());
                mFolderName.setTextColor(currentSwatch.getTitleTextColor());
            }
        }

        @Override
        public void onClick(View view) {
            if(!isActivatedMultipleChoiceMode()) {
                MediaFolder resultFolder=currentFolderList.get(getAdapterPosition());
                if(adapterMode==Mode.IMAGE) {
                    resultFolder = resultFolder.createImageSubfolder();
                }else if(adapterMode==Mode.VIDEO) {
                    resultFolder = resultFolder.createVideoSubfolder();
                }
                mOnLaunchGallery.onLaunchGalleryActivity(currentFolderList,resultFolder,itemView);
            }
            super.onClick(view);
        }

        @Override
        public boolean onLongClick(View view) {
            super.onLongClick(view);
            notifyDataSetChanged();
            return true;
        }


        @Override
        public void onBindData(int position) {
            MediaFile mediaFile=loaderCover(position);
            Glide.with(itemView.getContext())
                    .load(mediaFile.mediaFile())
                    .listener(new RequestListener<File, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, File model, com.bumptech.glide.request.target.Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, File model, com.bumptech.glide.request.target.Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            if (isFirstResource) {
                                return new DrawableCrossFadeFactory<>()
                                        .build(false, false)
                                        .animate(resource, (GlideAnimation.ViewAdapter) target);
                            }
                            return false;
                        }
                    })
                    .centerCrop()
                    .priority(Priority.HIGH)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .thumbnail(0.5f)
                    .placeholder(R.drawable.placeholder)
                    .animate(R.anim.fade_in)
                    .into(mMainImage);

            icon.setVisibility(mediaFile.getType()== MediaFile.Type.VIDEO?View.VISIBLE:View.INVISIBLE);

            mFolderName.setText(currentFolderList.get(position).getFolderName());
            mImageCount.setText(String.format(Locale.US,"%d",currentFolderList.get(position).getFileCount()));
            if(swatchList!=null) {
                applySwatch();
            }
        }

    }


    @Override
    public FolderViewHolder onCreateViewHolder(ViewGroup parentGroup, int viewType) {
        View root=mInflater.inflate(R.layout.media_folder_adapter_item,parentGroup,false);
        return new FolderViewHolder(root);
    }


    public void setAdapterMode(Mode mode) {
        if(mode!=adapterMode) {
            this.adapterMode=mode;
            initCurrentList();
            notifyDataSetChanged();
        }
    }

    private void initCurrentList() {
        if(adapterMode==Mode.ALL) {
            this.currentFolderList=mDataModel;
        }else if(adapterMode==Mode.IMAGE) {
            ArrayList<MediaFolder> imageFolderList=new ArrayList<>();
            for(MediaFolder folder:mDataModel) {
                if(folder.getCoverForImage()!=null) {
                    imageFolderList.add(folder);
                }
            }
            currentFolderList=imageFolderList;
        }else {
            ArrayList<MediaFolder> videoFolderList=new ArrayList<>();
            for(MediaFolder folder:mDataModel) {
                if(folder.getCoverForVideo()!=null) {
                    videoFolderList.add(folder);
                }
            }
            currentFolderList=videoFolderList;
        }
    }

    private MediaFile loaderCover(int position) {
        if(adapterMode==Mode.ALL) {
            return currentFolderList.get(position).getCoverForAll();
        }else if(adapterMode==Mode.IMAGE) {
            return currentFolderList.get(position).getCoverForImage();
        }
        return currentFolderList.get(position).getCoverForVideo();
    }

    public ArrayList<MediaFolder> getMediaList() {
        return mDataModel;
    }

    @Override
    public int getItemCount() {
        return currentFolderList.size();
    }

    public enum Mode {

        ALL (1),

        VIDEO(2),

        IMAGE (0);

        Mode(int ni){
            nativeInt = ni;
        }

        final int nativeInt;

    }

}
