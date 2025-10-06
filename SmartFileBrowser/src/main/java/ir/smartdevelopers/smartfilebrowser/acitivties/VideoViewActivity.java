package ir.smartdevelopers.smartfilebrowser.acitivties;

import static ir.smartdevelopers.smartfilebrowser.customClasses.Utils.formatTime;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.SharedElementCallback;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.Slider;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ir.smartdevelopers.smartfilebrowser.R;
import ir.smartdevelopers.smartfilebrowser.customClasses.MyVideoView;

public class VideoViewActivity extends AppCompatActivity {

    public static final String KEY_TRANSITION_NAME="transition_name";
    private final long animationDuration = 500;

    private Uri mVideoUri;
    private ImageView imgThumbnailHolder;

    private MyVideoView mVideoView;
    private boolean isExiting=false;
    private AppCompatImageView btnPlay;
    private AppCompatTextView txtVideoLength,txtCurrentTime;
    private Runnable mTimerRunnable;
    private Handler mTimerHandler;
    private boolean isPlaying;
    private Slider mSlider;
    private boolean mVideoPrapered = false;
    private boolean mTransitionEnds = false;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window window = getWindow();
        String transitionName=getIntent().getStringExtra(KEY_TRANSITION_NAME);
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
//        window.setSharedElementsUseOverlay(false);
        Transition transition= TransitionInflater.from(this).inflateTransition(R.transition.iten_transition_in);
        //transition.addTarget(R.id.sfb_activity_videoView_imagePlaceHolder);
        window.setSharedElementEnterTransition(transition);
        window.setSharedElementReturnTransition(transition);

        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {
                if (!isExiting){
                    mTransitionEnds = true;
                    mVideoView.setVideoURI(mVideoUri);
                    hideThumbNaile(true);
                }
            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }

            @Override
            public void onTransitionStart(Transition transition) {

            }
        });
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        WindowCompat.enableEdgeToEdge(window);
        WindowInsetsControllerCompat controllerCompat = WindowCompat.getInsetsController(window,window.getDecorView());
        if (controllerCompat != null){
            controllerCompat.setAppearanceLightStatusBars(false);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);
        supportPostponeEnterTransition();

       mVideoUri=getIntent().getData();


        findViews();
        ViewCompat.setTransitionName(imgThumbnailHolder,transitionName);
        initTimer();

        btnPlay.setOnClickListener(v->{
            btnPlay.setVisibility(View.GONE);
            playVideo();
            hideThumbNaile(false);


        });

        mVideoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction()==MotionEvent.ACTION_DOWN){
                    if (mVideoView.isPlaying()){
                        pauseVideo();
                        return true;
                    }
                }
                return false;
            }
        });
        mVideoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                Log.d("VideoVieAcivity","Player info = "+what);
                return false;
            }
        });

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mVideoPrapered = true;
                mVideoView.seekTo(1);
                hideThumbNaile(true);
            }
        });
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mVideoView.seekTo(1);
                btnPlay.setVisibility(View.VISIBLE);
//                mPlayerSeekbar.setProgress(0);
                mSlider.setValue(0);
                isPlaying=false;
                setTime(0,txtCurrentTime);
            }
        });

        //mVideoView.setZOrderOnTop(false);

//        mVideoView.resume();
//        mVideoView.pause();


        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (mVideoView !=null ){
                    mVideoView.pause();
                }

                isPlaying=false;
                showThumbnail(false);
                isExiting=true;
                remove();
                ActivityCompat.finishAfterTransition(VideoViewActivity.this);

            }
        });

        loadVideoThumbnail();
//        new Handler().postDelayed(()->loadVideoThumbnail(),2000);



    }
    private void showThumbnail(boolean animate) {
        if (animate){
            imgThumbnailHolder.animate().setDuration(animationDuration)
                    .alpha(1)
                    .start();
        }else{
            imgThumbnailHolder.setAlpha(1f);
            mVideoView.setVisibility(View.GONE);
        }
    }


    private void hideThumbNaile(boolean animate) {
        if (mVideoPrapered && mTransitionEnds){
            if (animate){
                imgThumbnailHolder.animate().setDuration(animationDuration).alpha(0);
            }else{
                imgThumbnailHolder.setAlpha(0f);
            }
        }
    }

    private void initTimer() {
        mTimerHandler=new Handler(Looper.getMainLooper());
        mTimerRunnable=new Runnable() {
            @Override
            public void run() {
                if (isPlaying){
                    long current=mVideoView.getCurrentPosition();
                    setTime(current,txtCurrentTime);
                    mSlider.setValue(current);
                    mTimerHandler.postDelayed(this,100);
                }
            }
        };
    }

    private void setTime(long millis, TextView textView){
        textView.setText(formatTime(millis));

    }

    private void pauseVideo(){
        if (!isExiting){
            btnPlay.setVisibility(View.VISIBLE);
        }
        mVideoView.pause();
        isPlaying=false;
        if (mTimerRunnable!=null){
            mTimerHandler.removeCallbacks(mTimerRunnable);
        }
    }
    private void playVideo(){
        btnPlay.setVisibility(View.GONE);
        mVideoView.start();
        isPlaying=true;
        startTimer();
    }

    private void startTimer() {
        if (mTimerRunnable!=null){
            mTimerHandler.removeCallbacks(mTimerRunnable);
            mTimerHandler.postDelayed(mTimerRunnable,100);
        }

    }

    private void findViews() {
        imgThumbnailHolder=findViewById(R.id.sfb_activity_videoView_imagePlaceHolder);
        mVideoView=findViewById(R.id.sfb_activity_videoView_playerView);
        btnPlay=findViewById(R.id.sfb_activity_videoView_imgPlayButton);
        txtCurrentTime=findViewById(R.id.sfb_activity_videoView_playerSeekbarCurrentTime);
        txtVideoLength=findViewById(R.id.sfb_activity_videoView_playerSeekbarEndTime);
        mSlider=findViewById(R.id.sfb_activity_videoView_playerSeekbar);
        mSlider.setValueFrom(0);
        LabelFormatter labelFormatter=new LabelFormatter() {
            @NonNull
            @Override
            public String getFormattedValue(float value) {

                return formatTime((long) value);
            }
        };
        mSlider.setLabelFormatter(labelFormatter);
        mSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                if (fromUser){
                    mVideoView.seekTo((int) value);
                }
            }
        });
        mSlider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            boolean lastPlayingStateIsPlaying=false;
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
                lastPlayingStateIsPlaying=isPlaying || btnPlay.getVisibility()!=View.VISIBLE;
                mVideoView.pause();
                isPlaying=false;
                mTimerHandler.removeCallbacks(mTimerRunnable);

            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                mVideoView.seekTo((int) slider.getValue());
                if(lastPlayingStateIsPlaying){
                    playVideo();
                }
            }

        });

    }

    private void loadVideoThumbnail() {
        imgThumbnailHolder.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                imgThumbnailHolder.getViewTreeObserver().removeOnPreDrawListener(this);
                int h = imgThumbnailHolder.getMeasuredHeight();
                int w = imgThumbnailHolder.getMeasuredWidth();
                supportStartPostponedEnterTransition();
                return true;
            }
        });

            try (MediaMetadataRetriever retriever=new MediaMetadataRetriever()){
                retriever.setDataSource(this,mVideoUri);
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                    Bitmap bitmap=retriever.getFrameAtTime();
                    Glide.with(imgThumbnailHolder).load(bitmap).dontTransform()
                            .override(metrics.widthPixels,metrics.heightPixels)
                            .into(imgThumbnailHolder);

                String durationSt=retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                if (durationSt!=null){
                    long duration=Long.parseLong(durationSt);
                    setTime(duration,txtVideoLength);
                    mSlider.setValueTo(duration);
                }
                setTime(0,txtCurrentTime);

            } catch (IOException e) {
                Log.e(getPackageName(),e.getMessage(),e);
            }


    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {

        if (mVideoView !=null && mVideoView.isPlaying()){
            mVideoView.pause();
        }
        isPlaying=false;
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (mVideoView !=null ){
            mVideoView.stopPlayback();
        }
        isPlaying=false;
        isExiting=true;
        super.onDestroy();
    }


}