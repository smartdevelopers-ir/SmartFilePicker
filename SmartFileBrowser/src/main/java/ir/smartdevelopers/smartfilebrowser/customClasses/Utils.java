package ir.smartdevelopers.smartfilebrowser.customClasses;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ir.smartdevelopers.smartfilebrowser.R;
import ir.smartdevelopers.smartfilebrowser.models.FileBrowserModel;
import ir.smartdevelopers.smartfilebrowser.models.GalleryModel;

public class Utils {
    public static String join(String[] strings,String joinChar){
        StringBuilder result = new StringBuilder();
        for (int i=0;i<strings.length;i++){
            result.append(strings[i]);
            if (i<strings.length-1){
                result.append(joinChar);
            }
        }
        return result.toString();
    }
    public static boolean contains(String s1,String s2){
        if (s1==null ){
            return  false;
        }
        return s2 != null && s1.toLowerCase().contains(s2.toLowerCase());
    }
    public static List<FileBrowserModel> generateFirstPageList(Context context,List<FileBrowserModel> recentList){
        List<FileBrowserModel> fileBrowserModels=new ArrayList<>();
        /*Internal*/
        FileBrowserModel internalStorage=new FileBrowserModel(FileBrowserModel.ID_INTERNAL_STORAGE
                ,context.getResources().getString(R.string.internal_storage),
                context.getString(R.string.internal_storage_subTitle),FileBrowserModel.MODEL_TYPE_INTERNAL_STORAGE,
                null, FileUtil.getInternalStoragePath(context),null);
        fileBrowserModels.add(internalStorage);

        /*External if exists*/
        String externalPath=FileUtil.getExternalStoragePath(context);
        if (externalPath!=null) {
            FileBrowserModel externalStorage = new FileBrowserModel(FileBrowserModel.ID_EXTERNAL_STORAGE, context.getResources().getString(R.string.external_storage),
                    context.getString(R.string.external_storage_subTitle), FileBrowserModel.MODEL_TYPE_EXTERNAL_STORAGE,
                    null, externalPath, null);
            fileBrowserModels.add(externalStorage);
        }

        /*Download*/
        File downloadFolderFile= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        int downloadedFileCount=FileUtil.getChildFileCount(downloadFolderFile);
        FileBrowserModel downloadFolder = new FileBrowserModel(FileBrowserModel.ID_DOWNLOAD_FOLDER, context.getResources().getString(R.string.download_folder),
                context.getString(R.string.download_folder_subTitle,downloadedFileCount), FileBrowserModel.MODEL_TYPE_DOWNLOAD_FOLDER,
                null, downloadFolderFile.getPath(), null);
        fileBrowserModels.add(downloadFolder);

        /*Recent file*/
        FileBrowserModel recent=new FileBrowserModel(FileBrowserModel.ID_RECENT_FILES,context.getString(R.string.recent),"",FileBrowserModel.MODEL_TYPE_ALL_FILE_TITLE,
                null,"",null);
        fileBrowserModels.add(recent);

        fileBrowserModels.addAll(recentList);
        return fileBrowserModels;
    }
    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;
        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
    public static String formatTime(long millis){
        int minute= (int) (millis/1000/60);
        int second= (int) ((millis/1000)%60);
        if (minute>9){
            return String.format(new Locale("en"),"%02d:%02d",minute,second);
        }else {
            return String.format(new Locale("en"),"%2d:%02d",minute,second);
        }
    }
    public static Rect calculateBitmapBound(Resources res,GalleryModel model) {

        DisplayMetrics metrics = res.getDisplayMetrics();
        int displayWidth = metrics.widthPixels;
        int displayHeight = metrics.heightPixels;
        int modelWidth = model.getWidth();
        int modelHeight = model.getHeight();
        if (model.getOrientation() == 90 || model.getOrientation() == 270){
            modelWidth = model.getHeight();
            modelHeight = model.getWidth();
        }
        int width ,height;
        double factor = 1;

        if (modelWidth >= modelHeight){
            if (modelWidth > displayWidth){
                factor = (double) modelWidth / displayWidth;
            } else if (modelHeight> displayHeight) {
                factor = (double) modelHeight / displayHeight;
            }

        }else{
            if (modelHeight > displayHeight) {
                factor = (double) modelHeight / displayHeight;
            }else if (modelWidth > displayWidth){
                factor = (double) modelWidth / displayWidth;
            }
        }
        width = (int) (modelWidth / factor);
        height = (int) (modelHeight / factor);
        return new Rect(0,0,width,height);
    }
    public static Rect scale(Rect rect,int maxBound){
        int width = rect.width();
        int height = rect.height();
        double factor=1;
        if (width > height){
            if (width > maxBound){
                factor = (double) width / maxBound;
            }
        }else{
            if (height > maxBound){
                factor = (double) height / maxBound;
            }
        }
        return new Rect(0,0, (int) (width/factor), (int) (height/factor));
    }
    static int resolveColor(Context context,TypedArray typedArray, int id, int defaultColor) {
        TypedValue value = new TypedValue();
        typedArray.getValue(id, value);

        if (value.type == TypedValue.TYPE_ATTRIBUTE) {
            TypedValue colorVal = new TypedValue();
            context.getTheme().resolveAttribute(value.data, colorVal, true);
            if (colorVal.data != 0) {
                return colorVal.data;
            }

        } else if (value.type >= TypedValue.TYPE_FIRST_COLOR_INT && value.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            return value.data;
        }
        return defaultColor;
    }
}
