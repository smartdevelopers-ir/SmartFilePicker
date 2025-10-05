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
    public static class IntentBuilder {
        private SFBFileFilter mFileFilter;
        private boolean showVideosInGallery = true;
        private boolean showCamera = true;
        private boolean canSelectMultipleInGallery = true;
        private boolean canSelectMultipleInFiles = true;
        private boolean showPDFTab = true;
        private boolean showFilesTab = true;
        private boolean showAudioTab = true;
        private boolean showGalleryTab = true;
        private boolean showPickFromSystemGalleyMenu = true;
        private Bundle mExtra;

        /**
         * @param showVideosInGallery if true, In gallery tab you can see videos , if set false
         *                            videos not showing in gallery tab
         */
        public IntentBuilder setShowVideosInGallery(boolean showVideosInGallery) {
            this.showVideosInGallery = showVideosInGallery;
            return this;
        }

        /**
         * @param showCamera if false in gallery tab you don't see tack pick by camera button
         */
        public IntentBuilder showCamera(boolean showCamera) {
            this.showCamera = showCamera;
            return this;
        }

        /**
         * @param canSelectMultipleInGallery if set true, you can select multiple items
         *                                   in gallery
         */
        public IntentBuilder canSelectMultipleInGallery(boolean canSelectMultipleInGallery) {
            this.canSelectMultipleInGallery = canSelectMultipleInGallery;
            return this;
        }

        /**
         * @param canSelectMultipleInFiles if set true, you can select multiple items
         *                                 in other tabs except gallery
         */
        public IntentBuilder canSelectMultipleInFiles(boolean canSelectMultipleInFiles) {
            this.canSelectMultipleInFiles = canSelectMultipleInFiles;
            return this;
        }

        /**
         * shows PDF tab or not
         */
        public IntentBuilder showPDFTab(boolean showPDFTab) {
            this.showPDFTab = showPDFTab;
            return this;
        }

        /**
         * shows Files tab or not
         */
        public IntentBuilder showFilesTab(boolean showFilesTab) {
            this.showFilesTab = showFilesTab;
            return this;
        }

        /**
         * shows Audio tab or not
         */
        public IntentBuilder showAudioTab(boolean showAudioTab) {
            this.showAudioTab = showAudioTab;
            return this;
        }

        /**
         * shows Gallery tab or not
         */
        public IntentBuilder showGalleryTab(boolean showGalleryTab) {
            this.showGalleryTab = showGalleryTab;
            return this;
        }

        public Intent build(Context context) {
            Intent filePickerIntent = new Intent(context, FileBrowserMainActivity.class);
            filePickerIntent.putExtra("mShowVideosInGallery", showVideosInGallery);
            filePickerIntent.putExtra("mShowCamera", showCamera);
            filePickerIntent.putExtra("mCanSelectMultipleInGallery", canSelectMultipleInGallery);
            filePickerIntent.putExtra("mCanSelectMultipleInFiles", canSelectMultipleInFiles);
            filePickerIntent.putExtra("mShowPDFTab", showPDFTab);
            filePickerIntent.putExtra("mShowFilesTab", showFilesTab);
            filePickerIntent.putExtra("mShowAudioTab", showAudioTab);
            filePickerIntent.putExtra("mShowGalleryTab", showGalleryTab);
            filePickerIntent.putExtra("mFileTabFileFilter", mFileFilter);
            filePickerIntent.putExtra("mShowPickFromSystemGalleryMenuButton", showPickFromSystemGalleyMenu);
            filePickerIntent.putExtra("sfb_extra", mExtra);
            return filePickerIntent;
        }

        /**
         * You can set a File filter to filter witch kind of files should be shown in File tab
         */
        public IntentBuilder setFileFilter(SFBFileFilter fileFilter) {
            mFileFilter = fileFilter;
            return this;
        }

        /**
         * if true, it shows a 3dot menu on gallery tab that user can pick item by system provided
         * gallery app
         */
        public IntentBuilder showPickFromSystemGalleyMenu(boolean showPickFromSystemGalleyMenu) {
            this.showPickFromSystemGalleyMenu = showPickFromSystemGalleyMenu;
            return this;
        }

        /**
         * You can put extras and receive it on OnActivityResult callback
         * to get result in OnActivityResult you can use {@link #getExtra} helper method
         */
        public IntentBuilder setExtra(Bundle extra) {
            mExtra = extra;
            return this;
        }
    }


    /**
     * get results on OnActivityResult callback as arrays of File object
     *
     * @deprecated do not use this in android 10 and above use {@link #getResultUris} instead
     */
    @Deprecated
    @Nullable
    public static File[] getResult(Intent data) {
        if (data == null) {
            return null;
        }
        Bundle bundle = data.getExtras();
        if (bundle == null) {
            return null;
        }
        String[] filesPath = bundle.getStringArray(FileBrowserMainActivity.EXTRA_RESULT);
        if (filesPath != null) {
            File[] files = new File[filesPath.length];
            for (int i = 0; i < filesPath.length; i++) {
                files[i] = new File(filesPath[i]);
            }
            return files;
        }
        return null;

    }

    /**
     * get results on OnActivityResult callback as arrays of Uri object
     *
     * @param data Intent that passed in OnActivityResult method
     * @return selected items uris
     */
    @Nullable
    public static Uri[] getResultUris(Intent data) {
        Uri[] result = null;
        if (data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                Parcelable[] parcelables = bundle.getParcelableArray(FileBrowserMainActivity.EXTRA_RESULT_URIS);
                if (parcelables != null && parcelables.length >0) {
                    result = new Uri[parcelables.length];
                    for (int i = 0; i < parcelables.length; i++) {
                        result[i] = (Uri) parcelables[i];
                    }
                }
            }
        }
        return result;
    }

    public static Bundle getExtra(Intent data) {
        if (data == null) {
            return null;
        }
        Bundle bundle = data.getExtras();
        if (bundle == null) {
            return null;
        }
        return bundle.getBundle("sfb_extra");
    }
}
