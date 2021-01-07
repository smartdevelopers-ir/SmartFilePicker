package ir.smartdevelopers.smartfilebrowser.customClasses;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

import java.io.File;
import java.util.Collections;
import java.util.List;

import ir.smartdevelopers.smartfilebrowser.acitivties.FileBrowserMainActivity;

public class SmartFileBrowser {
    public static class IntentBuilder{
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
            return filePickerIntent;
        }
    }

    @Nullable
    public static File[] getResult(Intent data){
        if (data==null){
            return null;
        }
        return (File[]) data.getSerializableExtra(FileBrowserMainActivity.EXTRA_RESULT);
    }
}
