package ir.smartdevelopers.smartfilebrowser.customClasses;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import ir.smartdevelopers.smartfilebrowser.R;

public class SquareImageView extends AppCompatImageView {
    private int size=0;
    public SquareImageView(@NonNull Context context) {
        this(context,null);
    }

    public SquareImageView(@NonNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SquareImageView(@NonNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
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
