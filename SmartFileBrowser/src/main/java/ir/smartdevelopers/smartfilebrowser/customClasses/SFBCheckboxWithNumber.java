package ir.smartdevelopers.smartfilebrowser.customClasses;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.color.MaterialColors;

import ir.smartdevelopers.smartfilebrowser.R;

public class SFBCheckboxWithNumber extends View {
    private boolean mChecked;
    private Paint mBlueCheckBoxPaint;
    private Paint mWhiteCheckBoxPaint;
    private Paint mStrokePaint;
    private Paint mTextPaint;
    private float mBlueCircleRadius;
    private float mWhiteCircleMaskRadius;
    private float mRadius;
    private Rect mBound;
    private ValueAnimator mAnimator;
    private Bitmap whiteCircle;
    private Paint whitCircleMaskPaint;
    private Canvas whitCircleCanvas;
    private float mStrokeWidth = 12;
    private long mAnimationDuration = 300;
    private int mNumber = 0;
    private Bitmap mTextBitmap;
    private Canvas mTextCanvas;
    private float mTextScaleFactor;
    private Paint mAnimatingBitmapPaint;
    private Paint clearPaint;
    private float mTextSize;
    private int mFillColor, mStrokeColor, mTextColor;

    public SFBCheckboxWithNumber(Context context) {
        this(context, null);
    }

    public SFBCheckboxWithNumber(Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SFBCheckboxWithNumber(Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attr) {
        mBlueCheckBoxPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBlueCheckBoxPaint.setStyle(Paint.Style.FILL);

        mWhiteCheckBoxPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mWhiteCheckBoxPaint.setStyle(Paint.Style.FILL);

        mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokePaint.setStyle(Paint.Style.STROKE);


        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);


        whitCircleMaskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        whitCircleMaskPaint.setColor(Color.WHITE);
        whitCircleMaskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        mAnimatingBitmapPaint = new Paint();
        mAnimatingBitmapPaint.setAntiAlias(true);
        mAnimatingBitmapPaint.setFilterBitmap(true);
        mAnimatingBitmapPaint.setDither(true);

        clearPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        clearPaint.setColor(Color.WHITE);
        if (attr != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attr, R.styleable.SFBCheckboxWithNumber);
            try {
                mAnimationDuration = typedArray.getInteger(R.styleable.SFBCheckboxWithNumber_android_animationDuration, 300);
                mTextSize = typedArray.getDimension(R.styleable.SFBCheckboxWithNumber_android_textSize, getResources().getDimension(R.dimen.sfb_text_size_14));

                mFillColor = Utils.resolveColor(context,typedArray, R.styleable.SFBCheckboxWithNumber_fillColor, Color.parseColor("#00b8e9"));
                mStrokeColor = Utils.resolveColor(context,typedArray, R.styleable.SFBCheckboxWithNumber_strokeColor, Color.WHITE);
                mStrokeWidth = typedArray.getDimension(R.styleable.SFBCheckboxWithNumber_strokeWidth, 12);
                mTextColor = Utils.resolveColor(context,typedArray, R.styleable.SFBCheckboxWithNumber_android_textColor, Color.WHITE);
                boolean checked = typedArray.getBoolean(R.styleable.SFBCheckboxWithNumber_android_checked, false);
                mBlueCheckBoxPaint.setColor(mFillColor);
                mWhiteCheckBoxPaint.setColor(Color.WHITE);
                mStrokePaint.setColor(mStrokeColor);
                mStrokePaint.setStrokeWidth(mStrokeWidth);
                mTextPaint.setColor(mTextColor);
                mTextPaint.setTextSize(mTextSize);
                setChecked(checked, false, false);
            } finally {
                typedArray.recycle();
            }

        }


    }



    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBound = new Rect(0, 0, w, h);
        float width = Math.min(w, h);
        whiteCircle = Bitmap.createBitmap((int) width, (int) width, Bitmap.Config.ARGB_8888);
        whitCircleCanvas = new Canvas(whiteCircle);
        mRadius = (width / 2) - mStrokeWidth;
        if (isChecked()) {
            mBlueCircleRadius = mRadius;
            mWhiteCircleMaskRadius = 0;
        } else {
            mBlueCircleRadius = 0;
            mWhiteCircleMaskRadius = mRadius;
        }
        mTextBitmap = Bitmap.createBitmap((int) width, (int) width, Bitmap.Config.ARGB_8888);
        mTextCanvas = new Canvas(mTextBitmap);


    }

    @Override
    protected void onDraw(Canvas canvas) {

        float cx = mBound.width() / 2f;
        float cy = mBound.height() / 2f;

        if (mWhiteCircleMaskRadius != mRadius) {
            whitCircleCanvas.drawCircle(cx, cy, mRadius, mWhiteCheckBoxPaint);
            whitCircleCanvas.drawCircle(cx, cy, mWhiteCircleMaskRadius, whitCircleMaskPaint);

            canvas.drawBitmap(whiteCircle, 0, 0, mAnimatingBitmapPaint);
        }
        if (mBlueCircleRadius != 0) {
            canvas.drawCircle(cx, cy, mBlueCircleRadius, mBlueCheckBoxPaint);
        }
        canvas.drawCircle(cx, cy, mRadius + (mStrokeWidth / 2), mStrokePaint);
        mTextCanvas.drawPaint(clearPaint);
        if (mTextScaleFactor != 0) {
            mTextCanvas.save();
            mTextCanvas.scale(mTextScaleFactor, mTextScaleFactor, cx, cy);
            mTextCanvas.drawText(String.valueOf(mNumber), cx, cy, mTextPaint);
            mTextCanvas.restore();
        }
        canvas.drawBitmap(mTextBitmap, 0, mRadius / 2 - mStrokeWidth / 2, mAnimatingBitmapPaint);

    }

    public void setChecked(boolean checked) {
        setChecked(checked, true);
    }

    public void setChecked(boolean checked, boolean animate) {
        setChecked(checked, animate, true);
    }

    private void setChecked(boolean checked, boolean animate, boolean invalidate) {
        if (mChecked == checked) {
            return;
        }
        mChecked = checked;
        if (mChecked) {
            if (animate) {
                animateToCheck();
            } else {
                mBlueCircleRadius = mRadius;
                mWhiteCircleMaskRadius = 0;
                mTextScaleFactor = 1;
                if (invalidate) {
                    invalidate();
                }
            }
        } else {
            if (animate) {
                animateToUncheck();
            } else {
                mBlueCircleRadius = 0;
                mWhiteCircleMaskRadius = mRadius;
                mTextScaleFactor = 0;
                if (invalidate) {
                    invalidate();
                }
            }
        }

    }


    public boolean isChecked() {
        return mChecked;
    }

    public void toggle(boolean animate) {
        setChecked(!mChecked, animate);
    }

    public void toggle() {
        toggle(true);
    }

    private void animateToCheck() {
        if (mAnimator != null) {
            mAnimator.cancel();
        }
        mAnimator = ValueAnimator.ofFloat(0, 1);
        mAnimator.setDuration(mAnimationDuration);
        //mAnimator.setInterpolator(new DecelerateInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (float) animation.getAnimatedValue();
                if (animation.getCurrentPlayTime() < mAnimationDuration / 2) {
                    mWhiteCircleMaskRadius = mRadius - (mRadius * (f * 2));
                    mTextScaleFactor = 0;
                } else {
                    if (mWhiteCircleMaskRadius != 0) {
                        mWhiteCircleMaskRadius = 0;
                    }
                    mBlueCircleRadius = mRadius - (mRadius * (1 - f) * 2);
                    mTextScaleFactor = (f * 2) - 1;
                    if (mTextScaleFactor > 1) {
                        mTextScaleFactor = 1;
                    }
                }

                invalidate();

            }
        });
        mAnimator.start();
    }

    private void animateToUncheck() {
        if (mAnimator != null) {
            mAnimator.cancel();
        }
        mAnimator = ValueAnimator.ofFloat(0, 1);

        mAnimator.setDuration(mAnimationDuration);
        //mAnimator.setInterpolator(new DecelerateInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (float) animation.getAnimatedValue();
                if (animation.getCurrentPlayTime() < mAnimationDuration / 2) {
                    mWhiteCircleMaskRadius = 0;
                    mBlueCircleRadius = mRadius - (mRadius * f * 2);
                    mTextScaleFactor = 1 - (f * 2);

                } else {
                    if (mTextScaleFactor != 0) {
                        mTextScaleFactor = 0;
                    }
                    if (mBlueCircleRadius != 0) {
                        mBlueCircleRadius = 0;
                    }
                    mWhiteCircleMaskRadius = mRadius - (mRadius * ((1 - f) * 2));
                }

                invalidate();
            }
        });
        mAnimator.start();

    }

    public int getNumber() {
        return mNumber;
    }

    public void setNumber(int number) {
        boolean inval = mNumber != number;
        if (inval) {
            mNumber = number;
            invalidate();
        }
    }

    public void setStrokeWidth(float strokeWidth) {
        boolean inval = mStrokeWidth != strokeWidth;
        if (inval) {
            mStrokeWidth = strokeWidth;
            mStrokePaint.setColor(mStrokeColor);
            invalidate();
        }
    }

    public void setAnimationDuration(long animationDuration) {
        mAnimationDuration = animationDuration;
    }

    public void setTextSize(float textSize) {
        boolean inval = mTextSize != textSize;
        if (inval) {
            mTextSize = textSize;
            mTextPaint.setTextSize(mTextSize);
            invalidate();
        }
    }

    public void setFillColor(int fillColor) {
        boolean inval = mFillColor != fillColor;
        if (inval) {
            mFillColor = fillColor;
            mBlueCheckBoxPaint.setColor(mFillColor);
            invalidate();
        }
    }

    public void setStrokeColor(int strokeColor) {
        boolean inval = mStrokeColor != strokeColor;
        if (inval) {
            mStrokeColor = strokeColor;
            mStrokePaint.setColor(mStrokeColor);
            invalidate();
        }
    }

    public void setTextColor(int textColor) {
        boolean inval = mTextColor != textColor;
        if (inval) {
            mTextColor = textColor;
            mTextPaint.setColor(mTextColor);
            invalidate();
        }
    }
}
