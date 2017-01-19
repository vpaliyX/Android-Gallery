package com.vpaliy.studioq.media;

import android.os.Parcel;
import android.os.Parcelable;
import org.parceler.ParcelConstructor;
import org.parceler.ParcelPropertyConverter;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@org.parceler.Parcel(org.parceler.Parcel.Serialization.BEAN)
public final class MediaFolder implements Parcelable {

    private String folderName;

    @ParcelPropertyConverter(MediaFile.MediaListConverter.class)
    private List<MediaFile> mediaFileList;

    private List<ImageFile> imageFileList;
    private List<VideoFile> videoFileList;

    public MediaFolder(String folderName) {
        this.folderName = folderName;
        this.mediaFileList= new ArrayList<>();
        this.videoFileList=new ArrayList<>();
        this.imageFileList=new ArrayList<>();
    }

    public MediaFolder(Parcel in) {
        this.folderName=in.readString();
        mediaFileList=new ArrayList<>();
        in.readTypedList(mediaFileList, MediaFile.CREATOR);
    }

    @ParcelConstructor
    public MediaFolder(String folderName, List<MediaFile> mediaFileList) {
        this.folderName=folderName;
        this.mediaFileList=mediaFileList;
    }

    public MediaFolder(String folderName, Collection<? extends MediaFile> collection) {
        this(folderName);
        this.mediaFileList=new ArrayList<>(collection);
    }

    public MediaFolder addMediaFile(MediaFile mediaFile) {
        mediaFileList.add(mediaFile);
        return this;
    }

    public MediaFolder addAll(Collection<? extends MediaFile> collection) {
        mediaFileList.addAll(collection);
        return this;
    }

    public void addVideoFile(VideoFile videoFile) {
        mediaFileList.add(videoFile);
        videoFileList.add(videoFile);
    }

    public void addImageFile(ImageFile imageFile) {
        mediaFileList.add(imageFile);
        imageFileList.add(imageFile);
    }

    public String getAbsolutePathToFolder() {
        if(mediaFileList!=null) {
            if (mediaFileList.get(0) != null)
                return mediaFileList.get(0).mediaFile().getParentFile().getAbsolutePath();
        }

        return folderName;
    }

    public void setMediaFileList(List<MediaFile> mediaFileList) {
        this.mediaFileList=mediaFileList;
    }


    //Covers
    public MediaFile getCoverForAll() {
        return mediaFileList.get(0);
    }

    public MediaFile  getCoverForVideo() {
        if (videoFileList.isEmpty()) {
            return null;
        }
        return videoFileList.get(0);
    }

    public MediaFile  getCoverForImage() {
        if (imageFileList.isEmpty())
            return null;
        return imageFileList.get(0);
    }


    //Data providers
    public List<ImageFile> getImageFileList() {
        return imageFileList;
    }

    public List<VideoFile> getVideoFileList() {
        return videoFileList;
    }

    public List<MediaFile> getMediaFileList() {
        return mediaFileList;
    }


    public int getFileCount() {
        return mediaFileList.size();
    }

    public String getFolderName() {
        return folderName;
    }

    @Override
    public String toString() {
        return getFolderName();
    }


    public MediaFolder createImageSubfolder() {
        MediaFolder imageFolder=new MediaFolder(folderName);
        imageFolder.addAll(imageFileList);
        return imageFolder;
    }

    public MediaFolder createVideoSubfolder() {
        MediaFolder videoFolder=new MediaFolder(folderName);
        videoFolder.addAll(videoFileList);
        return videoFolder;
    }

    //Parcel stuff goes here
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(folderName);
        out.writeTypedList(mediaFileList);
    }

    public final static Creator<MediaFolder> CREATOR
            =new Creator<MediaFolder>() {
        @Override
        public MediaFolder createFromParcel(Parcel parcel) {
            return new MediaFolder(parcel);
        }

        @Override
        public MediaFolder[] newArray(int size) {
            return new MediaFolder[size];
        }
    };

}
