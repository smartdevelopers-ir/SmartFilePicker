package ir.smartdevelopers.smartfilebrowser.customClasses;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.graphics.drawable.AnimatedStateListDrawableCompat;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import ir.smartdevelopers.smartfilebrowser.R;

public class SFBCheckBox extends AppCompatCheckBox {
    public SFBCheckBox(@NonNull Context context) {
        this(context,null);
    }

    public SFBCheckBox(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SFBCheckBox(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }
    void init(@NonNull Context context, @Nullable AttributeSet attrs){
            AnimatedStateListDrawableCompat animatedStateListDrawableCompat=
                    AnimatedStateListDrawableCompat.create(context,R.drawable.sfb_check_box_drawable_without_tick,context.getTheme());
            setButtonDrawable(animatedStateListDrawableCompat);


    }
}
