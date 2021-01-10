package ir.smartdevelopers.smartfilebrowser.customClasses;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
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
//        if (Build.VERSION.SDK_INT>=21){
////            AnimatedStateListDrawableCompat animatedStateListDrawableCompat=
////                    AnimatedStateListDrawableCompat.create(context,R.drawable.check_box_drawable_with_tick,context.getTheme());
//            setButtonDrawable(R.drawable.check_box_drawable_with_tick);
//        }else {
            StateListDrawable stateListDrawable=new StateListDrawable();
            Drawable checkedDrawable= VectorDrawableCompat.create(getResources(),R.drawable.sfb_check_box_checked,context.getTheme());
            Drawable uncheckedDrawable= VectorDrawableCompat.create(getResources(),R.drawable.sfb_chech_box_unchecked,context.getTheme());
            stateListDrawable.addState(new int[]{android.R.attr.state_checked},checkedDrawable);
            stateListDrawable.addState(new int[]{},uncheckedDrawable);

            setButtonDrawable(stateListDrawable);
//        }

    }
}
