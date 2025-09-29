package ir.smartdevelopers.smartfilebrowser.acitivties;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.WindowCompat;

import android.Manifest;
import android.app.SharedElementCallback;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.ChangeClipBounds;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.material.transition.platform.MaterialContainerTransform;
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback;
import com.google.android.material.transition.platform.MaterialElevationScale;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import ir.smartdevelopers.smartfilebrowser.R;
import ir.smartdevelopers.smartfilebrowser.customClasses.ResultListener;
import ir.smartdevelopers.smartphotoeditor.PhotoEditorFragment;
import ir.smartdevelopers.smartphotoeditor.photoeditor.PhotoEditor;

public class PhotoEditorActivity extends AppCompatActivity {

    public static final String KEY_TRANSITION_NAME = "transition_name";
    public static final String KEY_SAVE_PATH = "save_path";
    public static final String KEY_PREVIEW = "preview_bitmap";
    public static Bitmap Preview = null;

    private PhotoEditorFragment mPhotoEditorFragment;
    private ImageButton btnDone;
    private Uri mUri;
    private String mSavePath;
    private PhotoEditorFragment.OnEditorListener mOnEditorListener;
//    private CallbackViewModel mCallbackViewModel;
    private ResultListener mResultListener;
    private boolean saving=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        String transitionName = getIntent().getStringExtra(KEY_TRANSITION_NAME);

        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);

        Transition transition= TransitionInflater.from(this).inflateTransition(R.transition.iten_transition_in);
        getWindow().setSharedElementEnterTransition(transition);
        getWindow().setSharedElementReturnTransition(transition);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_editor);
        mResultListener=ResultListener.getInstance();
        mUri = getIntent().getData();
        mSavePath = getIntent().getStringExtra(KEY_SAVE_PATH);

        if (Preview != null){
            mPhotoEditorFragment = PhotoEditorFragment.getInstance(mUri,Preview);
            Preview = null;
        }else{
            mPhotoEditorFragment = PhotoEditorFragment.getInstance(mUri,true);
        }
        mPhotoEditorFragment.setTransitionName(transitionName);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.sfb_activity_photoEditor_fragmentView, mPhotoEditorFragment,"photoEditor")
                .commit();

        btnDone = findViewById(R.id.sfb_activity_photoEditor_btnDone);
        btnDone.setOnClickListener(v -> {
            saveChangesAndClose();
        });
        initListener();
        ActivityCompat.postponeEnterTransition(this);
    }

    private void initListener() {
        mOnEditorListener=new PhotoEditorFragment.SimpleOnEditorListener() {
            private boolean isEdited=false;
            @Override
            public void onCropWindowOpened() {
                btnDone.setVisibility(View.GONE);
            }

            @Override
            public void onCropWindowClosed() {
                btnDone.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFadeViews(float alpha) {
                btnDone.setAlpha(alpha);
            }

            @Override
            public void onPreviewLoaded() {
                if (!saving){
                    ActivityCompat.startPostponedEnterTransition(PhotoEditorActivity.this);
                }
            }

            @Override
            public void onEdit(boolean edited) {
                isEdited=edited;
                if (edited){
                    btnDone.setVisibility(View.VISIBLE);
                }else {
                    btnDone.setVisibility(View.GONE);
                }
            }

            @Override
            public void onImageLoaded(Bitmap bitmap, boolean b) {
                if (!saving){
//                    ActivityCompat.startPostponedEnterTransition(PhotoEditorActivity.this);
                }else {
                    mResultListener.setSavedBitmap(bitmap);
                    mPhotoEditorFragment.clearAll();
                    close();
                }
            }


            @Override
            public void onImageLoadFailed(Exception e) {
                if (!saving){
                    ActivityCompat.startPostponedEnterTransition(PhotoEditorActivity.this);
                }

            }

            @Override
            public void onOpenAddInputText() {
                if (isEdited){
                    btnDone.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCloseAddInputText() {
                if (isEdited){
                    btnDone.setVisibility(View.VISIBLE);
                }
            }
        };
        mPhotoEditorFragment.registerEditorListener(mOnEditorListener);
    }

    @Override
    protected void onDestroy() {
        mPhotoEditorFragment.unRegisterEditorListener();
        super.onDestroy();
    }

    private void saveChangesAndClose() {

        if (mPhotoEditorFragment.isEdited()) {
            saveEditedFile();
        }else {
            close();
        }
    }

    private void close() {

       if (mPhotoEditorFragment.isEdited()){
           mResultListener.setResult(mSavePath);
       }
       ActivityCompat.finishAfterTransition(this);
    }

    private void saveEditedFile() {

        saving=true;
        Uri saveUri = FileProvider.getUriForFile(getApplicationContext(),
                getPackageName() + ".sfb_provider", new File(mSavePath));
        try {
            OutputStream outputStream = getContentResolver().openOutputStream(saveUri);

            mPhotoEditorFragment.saveAsFile(outputStream, Bitmap.CompressFormat.JPEG, new PhotoEditor.OnSaveListener() {
                @Override
                public void onSuccess() {
                    mPhotoEditorFragment.setImageUri(saveUri);
//                    mPhotoEditorFragment.clearAll();

                }

                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}