package ir.smartdevelopers.smartfilebrowser.customClasses;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SmartRoundDrawable extends Drawable {
    private final Paint mBackgroundPaint;
    private final Path mPath;
    private final float[] corners={
            30,30, // top left
            30,30, // top right
            0,0, // bottom right
            0,0 // bottom left
    };


    public SmartRoundDrawable() {
        mBackgroundPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setColor(Color.BLACK);
        mBackgroundPaint.setStyle(Paint.Style.FILL);

        mPath=new Path();



    }
    public void setRadius(float topLeft,float topRight,float bottomRight,float bottomLeft){
        corners[0]=topLeft;
        corners[1]=topLeft;
        corners[2]=topRight;
        corners[3]=topRight;
        corners[4]=bottomRight;
        corners[5]=bottomRight;
        corners[6]=bottomLeft;
        corners[7]=bottomLeft;
        invalidateSelf();
    }

    public void setColor(int color){
        mBackgroundPaint.setColor(color);
        invalidateSelf();
    }
    @Override
    public void draw(@NonNull Canvas canvas) {
        RectF rectF = new RectF(getBounds());
        mPath.reset();
        mPath.addRoundRect(rectF,corners, Path.Direction.CW);

        canvas.drawPath(mPath,mBackgroundPaint);
    }

    @Override
    public void setAlpha(int alpha) {

        mBackgroundPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mBackgroundPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
