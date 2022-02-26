package ir.smartdevelopers.smartfilebrowser.acitivties;

import static ir.smartdevelopers.smartfilebrowser.customClasses.Utils.formatTime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.SharedElementCallback;
import androidx.core.view.ViewCompat;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.Slider;
import java.util.Locale;
import ir.smartdevelopers.smartfilebrowser.R;
import ir.smartdevelopers.smartfilebrowser.customClasses.MyVideoView;

public class VideoViewActivity extends AppCompatActivity {

    public static final String KEY_TRANSITION_NAME="transition_name";
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
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
            Transition transition= TransitionInflater.from(this).inflateTransition(R.transition.iten_transition_in);
            getWindow().setSharedElementEnterTransition(transition);
            getWindow().setSharedElementReturnTransition(transition);


        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);

       mVideoUri=getIntent().getData();
        String transitionName=getIntent().getStringExtra(KEY_TRANSITION_NAME);

        findViews();
        initTimer();
        ViewCompat.setTransitionName(imgThumbnailHolder,transitionName);

        btnPlay.setOnClickListener(v->{
            btnPlay.setVisibility(View.GONE);
            mVideoView.setVisibility(View.VISIBLE);
            playVideo();
            if (imgThumbnailHolder.getVisibility()==View.VISIBLE){
                new Handler().postDelayed(()->{
                    imgThumbnailHolder.setVisibility(View.INVISIBLE);
                },900);
            }

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

        mVideoView.setVideoURI(mVideoUri);
        mVideoView.seekTo(1);


        ActivityCompat.postponeEnterTransition(this);
        loadVideoThumbnail();

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
                    mVideoView.seekTo((int) slider.getValue());
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
        MediaMetadataRetriever retriever=new MediaMetadataRetriever();
        retriever.setDataSource(this,mVideoUri);
        Bitmap bitmap=retriever.getFrameAtTime();
        imgThumbnailHolder.setImageBitmap(bitmap);

        String durationSt=retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        if (durationSt!=null){
            long duration=Long.parseLong(durationSt);
            setTime(duration,txtVideoLength);
            mSlider.setValueTo(duration);
        }

        setTime(0,txtCurrentTime);
        ActivityCompat.startPostponedEnterTransition(VideoViewActivity.this);
        retriever.release();
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

    @Override
    public void onBackPressed() {
        if (mVideoView !=null ){
            mVideoView.pause();
        }

        isPlaying=false;
        imgThumbnailHolder.setVisibility(View.VISIBLE);
        imgThumbnailHolder.post(()->{
            mVideoView.setVisibility(View.GONE);
        });
        isExiting=true;
        super.onBackPressed();
    }
}