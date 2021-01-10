package ir.smartdevelopers.smartfilebrowser.customClasses;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Filterable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.constraintlayout.widget.ConstraintLayout;

import ir.smartdevelopers.smartfilebrowser.R;

public class SearchView extends ConstraintLayout {
    private ImageView imgClear;
    private AppCompatEditText edtSearch;
    private boolean mClearIconIsShowing = false;
    private OnQueryChangeListener mOnQueryChangeListener;
    private OnVisibilityChangeListener mOnVisibilityChangeListener;
    private Filterable mFilterable;

    public SearchView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public SearchView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SearchView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public SearchView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.sfb_search_view_layout, this, true);
        imgClear = view.findViewById(R.id.sfb_searchView_imgClear);
        edtSearch = view.findViewById(R.id.sfb_searchView_edtSearch);
        imgClear.setVisibility(GONE);
        mClearIconIsShowing=false;
        imgClear.setOnClickListener(v->{
            edtSearch.setText("");
            hideClearButton();
        });
        TextWatcher searchQueryListener=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (getVisibility()!=VISIBLE){
                    return;
                }
                if (s != null && s.length() > 0) {
                    showClearButton();
                } else {
                    hideClearButton();
                }
                if (mOnQueryChangeListener != null) {
                    mOnQueryChangeListener.onQueryChanged(s==null?"" :s.toString());
                }
                if (mFilterable != null) {
                    mFilterable.getFilter().filter(s==null?"":s.toString());
                }
            }
        };
        edtSearch.addTextChangedListener(searchQueryListener);
    }

    private void hideClearButton() {
        if (!mClearIconIsShowing){
            return;
        }
        imgClear.animate().setDuration(150).rotation(0)
                .scaleX(0.5f).scaleY(0.5f).withEndAction(() -> {
            imgClear.setVisibility(View.GONE);
        }).start();
        mClearIconIsShowing=false;
    }

    private void showClearButton() {
        if (mClearIconIsShowing) {
            return;
        }
        imgClear.animate().setDuration(150).rotation(90)
                .scaleX(1).scaleY(1).withStartAction(() -> {
            imgClear.setScaleX(0.5f);
            imgClear.setScaleY(0.5f);
            imgClear.setVisibility(View.VISIBLE);
        }).start();
        mClearIconIsShowing=true;
    }

    public void setOnQueryChangeListener(OnQueryChangeListener onQueryChangeListener) {
        mOnQueryChangeListener = onQueryChangeListener;
    }

    public Filterable getFilterable() {
        return mFilterable;
    }

    public void setFilterable(Filterable filterable) {
        mFilterable = filterable;
    }

    public void setOnVisibilityChangeListener(OnVisibilityChangeListener onVisibilityChangeListener) {
        mOnVisibilityChangeListener = onVisibilityChangeListener;
    }

    public String getQuery() {
        return edtSearch.getText()==null?"":edtSearch.getText().toString();
    }

    public void setQuery(String query) {
        edtSearch.setText(query);
    }

    public interface OnQueryChangeListener{
        void onQueryChanged(String query);
    }
    public interface OnVisibilityChangeListener{
        void onVisibilityChanged(boolean isShowing);
    }
    public boolean isShowing(){
        return getVisibility()==VISIBLE;
    }
    public void show(){
        setVisibility(View.VISIBLE);
        post(()->{
            edtSearch.requestFocus();
        });
        if (mOnVisibilityChangeListener != null) {
            mOnVisibilityChangeListener.onVisibilityChanged(true);
        }
    }
    public void close(boolean clear){
        edtSearch.clearFocus();
        if (clear){
            edtSearch.setText("");
        }
        setVisibility(View.GONE);
        if (mOnVisibilityChangeListener != null) {
            mOnVisibilityChangeListener.onVisibilityChanged(false);
        }
    }
}
