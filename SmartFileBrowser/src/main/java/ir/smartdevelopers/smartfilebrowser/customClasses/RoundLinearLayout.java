package ir.smartdevelopers.smartfilebrowser.customClasses;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import ir.smartdevelopers.smartfilebrowser.R;

public class RoundLinearLayout extends LinearLayout {
    private SmartRoundDrawable mSmartRoundDrawable;
    private int mBackgroundColor=Color.WHITE;
    public RoundLinearLayout(Context context) {
        super(context);
        init(context, null);
    }

    public RoundLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public RoundLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RoundLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){

        mSmartRoundDrawable=new SmartRoundDrawable();
        if (attrs!=null){
            TypedArray typedArray=context.obtainStyledAttributes(attrs,R.styleable.RoundLinearLayout);
            mBackgroundColor=typedArray.getColor(R.styleable.RoundLinearLayout_backgroundColor,Color.WHITE);
            float topLeftRadius=typedArray.getDimension(R.styleable.RoundLinearLayout_android_topLeftRadius,30f);
            float topRightRadius=typedArray.getDimension(R.styleable.RoundLinearLayout_android_topRightRadius,30f);
            float bottomRightRadius=typedArray.getDimension(R.styleable.RoundLinearLayout_android_bottomRightRadius,30f);
            float bottomLeftRadius=typedArray.getDimension(R.styleable.RoundLinearLayout_android_bottomLeftRadius,30f);
            mSmartRoundDrawable.setRadius(topLeftRadius,topRightRadius,bottomRightRadius,bottomLeftRadius);
        }
        mSmartRoundDrawable.setColor(mBackgroundColor);
        setBackground(mSmartRoundDrawable);
    }
    public void setRadius(float topLeft,float topRight,float bottomRight,float bottomLeft){
        mSmartRoundDrawable.setRadius(topLeft,topRight,bottomRight,bottomLeft);

    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        mBackgroundColor = backgroundColor;
        mSmartRoundDrawable.setColor(mBackgroundColor);
    }
}
