package ir.smartdevelopers.smartfilebrowser.models;

import android.net.Uri;

import java.io.File;
import java.util.Objects;

import ir.smartdevelopers.smartfilebrowser.customClasses.FileUtil;
import ir.smartdevelopers.smartfilebrowser.customClasses.Utils;

public  class GalleryModel implements FileModel ,Comparable<GalleryModel>{

    public static final int TYPE_CAMERA = -102;
    public static final int TYPE_SYSTEM_GALLERY = -103;

    private long id;
    private String mPath;
    private Uri mUri;
    private String name;
    private long mDateAdded;
    private File mFile;
    private int mType;
    private boolean mSelected;
    private int mNumber;
    private String mThumbnailPath;
    private String mThumbnailId;
    private long duration;


    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;
        if (path!=null){
            mFile=new File(path);
        }
    }

    public Uri getUri() {
        return mUri;
    }

    public void setUri(Uri uri) {
        mUri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDateAdded() {
        return mDateAdded;
    }

    public void setDateAdded(long dateAdded) {
        mDateAdded = dateAdded;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return mType;
    }


    /**@param type {@link FileUtil#TYPE_IMAGE} And {@link FileUtil#TYPE_VIDEO}*/
    public void setType(int type) {
        mType = type;
    }

    @Override
    public File getCurrentFile() {
        return mFile;
    }

    public boolean isSelected() {
        return mSelected;
    }

    public void setSelected(boolean selected) {
        mSelected = selected;
    }

    public int getNumber() {
        return mNumber;
    }

    public void setNumber(int number) {
        mNumber = number;
    }

    public String getThumbnailId() {
        return mThumbnailId;
    }

    public void setThumbnailId(String thumbnailId) {
        mThumbnailId = thumbnailId;
    }

    public String getThumbnailPath() {
        return mThumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        mThumbnailPath = thumbnailPath;
    }

    @Override
    public int compareTo(GalleryModel o) {
        return Long.compare(this.mDateAdded,o.mDateAdded)*-1;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
    public String getDurationTime(){
        return Utils.formatTime(duration);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GalleryModel that = (GalleryModel) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
