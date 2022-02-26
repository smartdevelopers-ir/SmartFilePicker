package ir.smartdevelopers.smartfilebrowser.customClasses;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.VideoView;

public class MyVideoView extends VideoView {
    private int mVideoWidth;
    private int mVideoHeight;
    private int mVideoRotation;
    public MyVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MyVideoView(Context context) {
        super(context);
    }


    @Override
    public void setVideoURI(Uri uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
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
        super.setVideoURI(uri);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Log.i("@@@", "onMeasure");
        double deviceWidth = getDefaultSize(mVideoWidth, widthMeasureSpec);
        double deviceHeight = getDefaultSize(mVideoHeight, heightMeasureSpec);
        double scaleFactor=1;
        double finalWidth;
        double finalHeight;

        if ((Math.abs(mVideoRotation)==90 || Math.abs(mVideoRotation)==270) ){
            finalWidth=mVideoHeight;
            finalHeight=mVideoWidth;
        }else {
            finalHeight=mVideoHeight;
            finalWidth=mVideoWidth;
        }
        if (mVideoWidth > 0 && mVideoHeight > 0) {
            if (mVideoWidth >= mVideoHeight){
                scaleFactor=deviceWidth/finalWidth;
            }else {
                scaleFactor=deviceHeight/finalHeight;
            }
        }
        finalWidth=finalWidth*scaleFactor;
        finalHeight=finalHeight*scaleFactor;

//        if (mVideoWidth > 0 && mVideoHeight > 0) {
//            if (mVideoWidth * deviceHeight > deviceWidth * mVideoHeight) {
//                // Log.i("@@@", "image too tall, correcting");
//                deviceHeight = deviceWidth * mVideoHeight / mVideoWidth;
//            } else if (mVideoWidth * deviceHeight < deviceWidth * mVideoHeight) {
//                // Log.i("@@@", "image too wide, correcting");
//                deviceWidth = deviceHeight * mVideoWidth / mVideoHeight;
//            } else {
//                // Log.i("@@@", "aspect ratio is correct: " +
//                // width+"/"+height+"="+
//                // mVideoWidth+"/"+mVideoHeight);
//            }
//        }
//        if ((Math.abs(mVideoRotation)==90 || Math.abs(mVideoRotation)==270) ){
//            int tempWidth=deviceWidth;
//            deviceWidth=deviceHeight;
//            deviceHeight=tempWidth;
//        }
        // Log.i("@@@", "setting size: " + width + 'x' + height);
        setMeasuredDimension((int)finalWidth, (int)finalHeight);
    }
}