package ir.smartdevelopers.smartfilebrowser.customClasses;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import ir.smartdevelopers.smartfilebrowser.R;

public class SquareLayout extends FrameLayout {
    private int size=0;

    public SquareLayout(@NonNull Context context) {
        this(context,null);
    }

    public SquareLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SquareLayout(@NonNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        DisplayMetrics metrics=getResources().getDisplayMetrics();
        int deviceWidth=metrics.widthPixels;
        int gapSize=getResources().getDimensionPixelSize(R.dimen.sfb_gallery_gap_size);
        int imageCount=getResources().getInteger(R.integer.sfb_gallery_grid);
        int gapCount=imageCount+1;
        size=(deviceWidth-(gapSize*gapCount))/imageCount;
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(size,MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(size,MeasureSpec.EXACTLY));
    }
}
