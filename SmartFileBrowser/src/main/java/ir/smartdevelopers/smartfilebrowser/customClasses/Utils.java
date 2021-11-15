package ir.smartdevelopers.smartfilebrowser.customClasses;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ir.smartdevelopers.smartfilebrowser.R;
import ir.smartdevelopers.smartfilebrowser.models.FileBrowserModel;

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
        FileBrowserModel internalStorage=new FileBrowserModel(-1,context.getResources().getString(R.string.internal_storage),
                context.getString(R.string.internal_storage_subTitle),FileBrowserModel.MODEL_TYPE_INTERNAL_STORAGE,
                null, FileUtil.getInternalStoragePath(context),null);
        fileBrowserModels.add(internalStorage);

        /*External if exists*/
        String externalPath=FileUtil.getExternalStoragePath(context);
        if (externalPath!=null) {
            FileBrowserModel externalStorage = new FileBrowserModel(-2, context.getResources().getString(R.string.external_storage),
                    context.getString(R.string.external_storage_subTitle), FileBrowserModel.MODEL_TYPE_EXTERNAL_STORAGE,
                    null, externalPath, null);
            fileBrowserModels.add(externalStorage);
        }

        /*Download*/
        File downloadFolderFile= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        int downloadedFileCount=FileUtil.getChildFileCount(downloadFolderFile);
        FileBrowserModel downloadFolder = new FileBrowserModel(-3, context.getResources().getString(R.string.download_folder),
                context.getString(R.string.download_folder_subTitle,downloadedFileCount), FileBrowserModel.MODEL_TYPE_DOWNLOAD_FOLDER,
                null, downloadFolderFile.getPath(), null);
        fileBrowserModels.add(downloadFolder);

        /*Recent file*/
        FileBrowserModel recent=new FileBrowserModel(-4,context.getString(R.string.recent),"",FileBrowserModel.MODEL_TYPE_ALL_FILE_TITLE,
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
}
