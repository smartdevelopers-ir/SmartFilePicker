package ir.smartdevelopers.smartfilebrowser.customClasses;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import java.io.File;

import ir.smartdevelopers.smartfilebrowser.acitivties.FileBrowserMainActivity;
import ir.smartdevelopers.smartfilebrowser.models.GalleryModel;

public class SmartFilePicker {
    public static class IntentBuilder{
        private SFBFileFilter mFileFilter;
        private boolean showVideosInGallery=true;
        private boolean showCamera=true;
        private boolean canSelectMultipleInGallery=true;
        private boolean canSelectMultipleInFiles=true;
        private boolean showPDFTab=true;
        private boolean showFilesTab=true;
        private boolean showAudioTab=true;
        private boolean showGalleryTab=true;
        private boolean showPickFromSystemGalleyMenu=true;
        private Bundle mExtra;

        public IntentBuilder setShowVideosInGallery(boolean showVideosInGallery) {
            this.showVideosInGallery = showVideosInGallery;
            return this;
        }

        public IntentBuilder showCamera(boolean showCamera) {
            this.showCamera = showCamera;
            return this;
        }

        public IntentBuilder canSelectMultipleInGallery(boolean canSelectMultipleInGallery) {
            this.canSelectMultipleInGallery = canSelectMultipleInGallery;
            return this;
        }

        public IntentBuilder canSelectMultipleInFiles(boolean canSelectMultipleInFiles) {
            this.canSelectMultipleInFiles = canSelectMultipleInFiles;
            return this;
        }

        public IntentBuilder showPDFTab(boolean showPDFTab) {
            this.showPDFTab = showPDFTab;
            return this;
        }

        public IntentBuilder showFilesTab(boolean showFilesTab) {
            this.showFilesTab = showFilesTab;
            return this;
        }

        public IntentBuilder showAudioTab(boolean showAudioTab) {
            this.showAudioTab = showAudioTab;
            return this;
        }

        public IntentBuilder showGalleryTab(boolean showGalleryTab) {
            this.showGalleryTab = showGalleryTab;
            return this;
        }

        public Intent build(Context context){
            Intent filePickerIntent=new Intent(context, FileBrowserMainActivity.class);
            filePickerIntent.putExtra("mShowVideosInGallery",showVideosInGallery);
            filePickerIntent.putExtra("mShowCamera",showCamera);
            filePickerIntent.putExtra("mCanSelectMultipleInGallery",canSelectMultipleInGallery);
            filePickerIntent.putExtra("mCanSelectMultipleInFiles",canSelectMultipleInFiles);
            filePickerIntent.putExtra("mShowPDFTab",showPDFTab);
            filePickerIntent.putExtra("mShowFilesTab",showFilesTab);
            filePickerIntent.putExtra("mShowAudioTab",showAudioTab);
            filePickerIntent.putExtra("mShowGalleryTab",showGalleryTab);
            filePickerIntent.putExtra("mFileTabFileFilter",mFileFilter);
            filePickerIntent.putExtra("mShowPickFromSystemGalleryMenuButton",showPickFromSystemGalleyMenu);
            filePickerIntent.putExtra("sfb_extra",mExtra);
            return filePickerIntent;
        }

        public IntentBuilder setFileFilter(SFBFileFilter fileFilter) {
            mFileFilter = fileFilter;
            return this;
        }

        public IntentBuilder showPickFromSystemGalleyMenu(boolean showPickFromSystemGalleyMenu) {
            this.showPickFromSystemGalleyMenu = showPickFromSystemGalleyMenu;
            return this;
        }

        public IntentBuilder setExtra(Bundle extra) {
            mExtra = extra;
            return this;
        }
    }

    @Nullable
    public static File[] getResult(Intent data){
        if (data==null){
            return null;
        }
        Bundle bundle=data.getExtras();
        if (bundle==null){
            return null;
        }
        String[] filesPath=bundle.getStringArray(FileBrowserMainActivity.EXTRA_RESULT);
        if (filesPath != null) {
            File[] files=new File[filesPath.length];
            for (int i=0;i<filesPath.length;i++){
                files[i]=new File(filesPath[i]);
            }
            return files;
        }
        return null;

    }
    @Nullable
    public static Uri[] getResultUris(Intent data){
        Uri[] result=null;
        if (data!=null){
            Bundle bundle=data.getExtras();
            if (bundle != null) {
                Parcelable[] parcelables= bundle.getParcelableArray(FileBrowserMainActivity.EXTRA_RESULT_URIS);
                if (parcelables!=null){
                    result=new Uri[parcelables.length];
                    for (int i=0;i<parcelables.length;i++){
                        result[i]= (Uri) parcelables[i];
                    }
                }
            }
        }
        return result;
    }
    public static Bundle getExtra(Intent data){
        if (data==null){
            return null;
        }
        Bundle bundle=data.getExtras();
        if (bundle==null){
            return  null;
        }
        return bundle.getBundle("sfb_extra");
    }
}
