package ir.smartdevelopers.smartfilebrowser.models;

import android.text.TextUtils;
import android.webkit.URLUtil;

import java.io.File;

import ir.smartdevelopers.smartfilebrowser.customClasses.FileModel;
import ir.smartdevelopers.smartfilebrowser.customClasses.Utils;

public class FileBrowserModel implements FileModel {
    public static final int MODEL_TYPE_FOLDER=0;
    public static final int MODEL_TYPE_INTERNAL_STORAGE=1;
    public static final int MODEL_TYPE_EXTERNAL_STORAGE=2;
    public static final int MODEL_TYPE_DOWNLOAD_FOLDER=3;
    public static final int MODEL_TYPE_VIDEO=4;
    public static final int MODEL_TYPE_IMAGE=5;
    public static final int MODEL_TYPE_AUDIO=6;
    public static final int MODEL_TYPE_PDF=7;
    public static final int MODEL_TYPE_FILE=8;
    public static final int MODEL_TYPE_ALL_FILE_TITLE = 9;
    public static final int MODEL_TYPE_GO_BACK = 10;


    private long id;
    private String mTitle;
    private String mSubTitle;
    private int mModelType;
    private String mimeType;
    private String mPath;
    private String mParentPath;
    private File mCurrentFile;
    private File mParentFile;
    private boolean mSelected;


    public FileBrowserModel(long id, String title, String subTitle, int modelType,
                            String mimeType, String path, String parentPath) {
        this.id = id;
        mTitle = title;
        mSubTitle = subTitle;

        this.mimeType = mimeType;
        mPath = path;
        mParentPath = parentPath;
        if (modelType==MODEL_TYPE_FILE){
            mModelType=guessModelType(mimeType);
        }else {
            mModelType = modelType;
        }
        if (!TextUtils.isEmpty(path)){
            mCurrentFile=new File(path);
        }
        if (!TextUtils.isEmpty(parentPath)){
            mParentFile=new File(parentPath);
        }
    }

    @Override
    public File getCurrentFile() {
        return mCurrentFile;
    }

    public void setCurrentFile(File currentFile) {
        mCurrentFile = currentFile;
    }

    public File getParentFile() {
        return mParentFile;
    }

    public void setParentFile(File parentFile) {
        mParentFile = parentFile;
    }

    private int guessModelType(String mimeType) {
        if (Utils.contains(mimeType,"pdf")){
            return MODEL_TYPE_PDF;
        }else if (Utils.contains(mimeType,"audio")){
            return MODEL_TYPE_AUDIO;
        }else if (Utils.contains(mimeType,"video")){
            return MODEL_TYPE_VIDEO;
        }else if (Utils.contains(mimeType,"image")){
            return MODEL_TYPE_IMAGE;
        }
        return MODEL_TYPE_FILE;

    }

    public FileBrowserModel() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getSubTitle() {
        return mSubTitle;
    }

    public void setSubTitle(String subTitle) {
        mSubTitle = subTitle;
    }

    public int getModelType() {
        return mModelType;
    }

    public void setModelType(int modelType) {
        mModelType = modelType;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;
    }

    public String getParentPath() {
        return mParentPath;
    }

    public void setParentPath(String parentPath) {
        mParentPath = parentPath;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public boolean isSelected() {
        return mSelected;
    }

    public void setSelected(boolean selected) {
        mSelected = selected;
    }
}
