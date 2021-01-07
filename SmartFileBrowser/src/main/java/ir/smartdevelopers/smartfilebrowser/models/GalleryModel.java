package ir.smartdevelopers.smartfilebrowser.models;

import android.net.Uri;

import java.io.File;

import ir.smartdevelopers.smartfilebrowser.customClasses.FileModel;
import ir.smartdevelopers.smartfilebrowser.customClasses.FileUtil;

public  class GalleryModel implements FileModel {

    public static final int TYPE_CAMERA = -102;
    private long id;
    private String mPath;
    private Uri mUri;
    private String name;
    private long mDateAdded;
    private File mFile;
    private int mType;
    private boolean mSelected;
    private int mNumber;


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
}
