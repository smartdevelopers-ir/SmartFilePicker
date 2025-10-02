package ir.smartdevelopers.smartfilebrowser.customClasses;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.graphics.drawable.AnimatedStateListDrawableCompat;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.widget.CompoundButtonCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import ir.smartdevelopers.smartfilebrowser.R;

public class SFBCheckBoxWithTick extends AppCompatCheckBox {
    public SFBCheckBoxWithTick(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public SFBCheckBoxWithTick(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SFBCheckBoxWithTick(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }
    void init(@NonNull Context context, @Nullable AttributeSet attrs){
        Drawable d = AppCompatResources.getDrawable(context, R.drawable.sfb_check_box_drawable_with_tick);
        if (d != null) {
            d = DrawableCompat.wrap(d).mutate();
            CompoundButtonCompat.setButtonTintList(this, null);
            setButtonDrawable(d);
        }


    }
}
