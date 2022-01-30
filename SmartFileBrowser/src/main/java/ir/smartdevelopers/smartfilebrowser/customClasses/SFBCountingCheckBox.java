package ir.smartdevelopers.smartfilebrowser.customClasses;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import ir.smartdevelopers.smartfilebrowser.R;

public class SFBCountingCheckBox extends FrameLayout {
    private SFBCheckBox mCheckBox;
    private TextView mTextView;

    private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener;
    public SFBCountingCheckBox(@NonNull Context context) {
        this(context,null);

    }

    public SFBCountingCheckBox(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);

    }

    public SFBCountingCheckBox(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {

            super(context, attrs, defStyleAttr);
            init(context, attrs);

    }


    private void init(@NonNull Context context, @Nullable AttributeSet attrs){
        LayoutInflater.from(context).inflate( R.layout.sfb_countering_check_box_layout,this);
        mCheckBox=findViewById(R.id.sfb_counting_checkbox_checkBox);
        mTextView=findViewById(R.id.sfb_counting_checkbox_txtCounter);
        mTextView.setVisibility(INVISIBLE);

        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    animateText();
                }else {
                    mTextView.animate().withEndAction(()->{
                            mTextView.setVisibility(INVISIBLE);
                    }).setDuration(1).start();
                }
                if (mOnCheckedChangeListener != null) {
                    mOnCheckedChangeListener.onCheckedChanged(buttonView,isChecked);
                }
            }
        });
    }

    public void setCounter(int number){
        mTextView.setText(String.valueOf(number));
    }
    private void animateText() {
        long delay=Build.VERSION.SDK_INT>=21 ? 150: 2;
        mTextView.animate().setDuration(100).setStartDelay(delay)
                .withStartAction(()->{
                    mTextView.setScaleX(0.2f);
                    mTextView.setScaleY(0.2f);
                    mTextView.setVisibility(VISIBLE);

                })
                .scaleX(1).scaleY(1).start();
    }

    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener onCheckedChangeListener){
        mOnCheckedChangeListener=onCheckedChangeListener;
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        mCheckBox.setOnClickListener(l);
    }

    public boolean isChecked() {
        return mCheckBox.isChecked();
    }

    public void setChecked(boolean selected) {
        mCheckBox.setChecked(selected);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void jumpDrawablesToCurrentState() {
        mCheckBox.jumpDrawablesToCurrentState();
    }

}
