package ir.smartdevelopers.smartfilebrowser.customClasses;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;

import java.io.File;
import java.util.Collections;
import java.util.List;

import ir.smartdevelopers.smartfilebrowser.acitivties.FileBrowserMainActivity;
import ir.smartdevelopers.smartfilebrowser.models.GalleryModel;

public class SmartFileBrowser {
    public static OnItemClickListener<GalleryModel> sOnGalleryModelClickListener;
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

        public IntentBuilder setShowVideosInGallery(boolean showVideosInGallery) {
            this.showVideosInGallery = showVideosInGallery;
            return this;
        }

        public IntentBuilder setShowCamera(boolean showCamera) {
            this.showCamera = showCamera;
            return this;
        }

        public IntentBuilder setCanSelectMultipleInGallery(boolean canSelectMultipleInGallery) {
            this.canSelectMultipleInGallery = canSelectMultipleInGallery;
            return this;
        }

        public IntentBuilder setCanSelectMultipleInFiles(boolean canSelectMultipleInFiles) {
            this.canSelectMultipleInFiles = canSelectMultipleInFiles;
            return this;
        }

        public IntentBuilder setShowPDFTab(boolean showPDFTab) {
            this.showPDFTab = showPDFTab;
            return this;
        }

        public IntentBuilder setShowFilesTab(boolean showFilesTab) {
            this.showFilesTab = showFilesTab;
            return this;
        }

        public IntentBuilder setShowAudioTab(boolean showAudioTab) {
            this.showAudioTab = showAudioTab;
            return this;
        }

        public IntentBuilder setShowGalleryTab(boolean showGalleryTab) {
            this.showGalleryTab = showGalleryTab;
            return this;
        }
        public IntentBuilder setOnGalleryItemClickListener(OnItemClickListener<GalleryModel> onGalleryModelClickListener){
            sOnGalleryModelClickListener=onGalleryModelClickListener;
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
            return filePickerIntent;
        }

        public IntentBuilder setFileFilter(SFBFileFilter fileFilter) {
            mFileFilter = fileFilter;
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
}
