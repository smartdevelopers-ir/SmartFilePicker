package ir.smartdevelopers.smartfilebrowser.customClasses;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.annotation.Nullable;
import androidx.core.content.MimeTypeFilter;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FileUtil {
    private static final String TAG = "SmartFileBrowserTag";
    public static final int TYPE_VIDEO=3;
    public static final int TYPE_IMAGE=1;

    public static int getFileTypeCode(String mimeType){
       if (mimeType.toLowerCase().contains("video")){
           return TYPE_VIDEO;
       }else if (mimeType.toLowerCase().contains("image")){
           return TYPE_IMAGE;
       }

       return 0;
    }

    public static String getInternalStoragePath(Context context){
        try {
            String localPath=context.getExternalFilesDirs(null)[0].getPath();
            return localPath.split("Android")[0];
        }catch (Exception e){
            Log.e(TAG,"Error in getting internal Storage path",e);
        }
        return "";
    }
    @Nullable
    public static String getExternalStoragePath(Context context){
        try{
            File[] files=context.getExternalFilesDirs(null);
            if (files.length>1){
                String externalLocalPath=files[1].getPath();
                return externalLocalPath.split("Android")[0];
            }

        }catch (Exception ignore){}
        return null;
    }

    public static int getChildFileCount(File file){
        int count=0;
        File[] innerFiles=file.listFiles();
        for (File f:innerFiles){
            if (f.isFile()){
                count++;
            }
        }
        return count;
    }
    public List<File> getRecentFiles(Context context,FileFilter fileFilter){
        ContentResolver contentResolver=context.getContentResolver();
//        String[] projection={MediaStore.Files.FileColumns._ID,}
//        Cursor fileCursor=contentResolver.query(MediaStore.Files.getContentUri("external"),
//                )
        return null;
    }
    public static String getFileSizeToString(long fileSize){
        if(fileSize <= 0) return "0";
        final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(fileSize)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(fileSize/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
    public static String getMimeTypeFromPath(String path){
        String ext=getFileExtensionFromPath(path);
        String mimeType=MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
        return mimeType==null?"" :mimeType;
    }
    public static String getFileExtensionFromPath(String path){
        String uri=Uri.encode(path);
        return MimeTypeMap.getFileExtensionFromUrl(uri);
    }
    public static boolean isDirectory(File file){
        if (file==null){
            return true;
        }
        return file.isDirectory();
    }
    public static File getImageFile(Context context) throws IOException {
        File folder= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        DateFormat dateFormat=new SimpleDateFormat("yyyyMMdd-HHmmss",new Locale("en"));
        String name="JPEG_"+dateFormat.format(new Date())+"_";
//        return File.createTempFile(name,".jpg",folder);
        return new File(folder,name+".jpg");
    }
    public static void scanMediaFile(Context context,File file){
        Intent scanMediaIntent=new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri=Uri.fromFile(file);
        scanMediaIntent.setData(contentUri);
        context.sendBroadcast(scanMediaIntent);
    }
}
