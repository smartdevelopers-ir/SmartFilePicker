package ir.smartdevelopers.smartfilebrowser.customClasses;

import android.graphics.Bitmap;

public class ResultListener {
    private static ResultListener instance;
    private OnResult mOnResult;
    private Bitmap mSavedBitmap;

    public static ResultListener getInstance() {
        if (instance == null) {
            instance=new ResultListener();
        }
        return instance;
    }
    public void registerResultListener(OnResult onResult){
        mOnResult=onResult;
    }
    public void setResult(String path){
        if (mOnResult != null) {
            mOnResult.onResultSet(path);
        }
    }
    public void clear(){
        instance=null;
    }

    public Bitmap getSavedBitmap() {
        return mSavedBitmap;
    }

    public void setSavedBitmap(Bitmap savedBitmap) {
        mSavedBitmap = savedBitmap;
    }

    public interface OnResult{
        void onResultSet(String path);
    }
}
