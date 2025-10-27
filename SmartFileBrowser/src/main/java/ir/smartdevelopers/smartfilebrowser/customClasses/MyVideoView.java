package ir.smartdevelopers.smartfilebrowser.customClasses;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.VideoView;

import java.io.IOException;

public class MyVideoView extends TextureVideoView {
    private int mVideoWidth;
    private int mVideoHeight;
    private int mVideoRotation;
    public MyVideoView(Context context, AttributeSet attrs) {
        this(context, attrs,0);

    }

    public MyVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MyVideoView(Context context) {
        this(context,null);

    }


    @Override
    public void setVideoURI(Uri uri) {

        try(MediaMetadataRetriever retriever = new MediaMetadataRetriever();){
            retriever.setDataSource(this.getContext(), uri);
            String width=retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            String height=retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            String rotation=retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
            if (!TextUtils.isEmpty(width) && !TextUtils.isEmpty(height) && !TextUtils.isEmpty(rotation)){
                assert width != null;
                mVideoWidth = Integer.parseInt(width);
                assert height != null;
                mVideoHeight = Integer.parseInt(height);
                mVideoRotation=Integer.parseInt(rotation);
            }else {
                DisplayMetrics metrics=getContext().getResources().getDisplayMetrics();
                mVideoWidth=metrics.widthPixels;
                mVideoHeight=metrics.heightPixels;
                mVideoRotation=0;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        super.setVideoURI(uri);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Log.i("@@@", "onMeasure");
        double deviceWidth = getDefaultSize(mVideoWidth, widthMeasureSpec);
        double deviceHeight = getDefaultSize(mVideoHeight, heightMeasureSpec);
        double finalWidth;
        double finalHeight;

        if ((Math.abs(mVideoRotation)==90 || Math.abs(mVideoRotation)==270) ){
            finalWidth=mVideoHeight;
            finalHeight=mVideoWidth;
        }else {
            finalHeight=mVideoHeight;
            finalWidth=mVideoWidth;
        }
        double width ,height;
        double factor = 1;

        if (finalWidth >= finalHeight){
            if (finalWidth > deviceWidth){
                factor =  finalWidth / deviceWidth;
            } else if (finalHeight> deviceHeight) {
                factor =  finalHeight / deviceHeight;
            }

        }else{
            if (finalHeight > deviceHeight) {
                factor =  finalHeight / deviceHeight;
            }else if (finalWidth > deviceWidth){
                factor =  finalWidth / deviceWidth;
            }
        }
        width =  (finalWidth / factor);
        height =  (finalHeight / factor);

        // Log.i("@@@", "setting size: " + width + 'x' + height);
        setMeasuredDimension((int)width, (int)height);
    }
}