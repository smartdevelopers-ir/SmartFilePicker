package ir.smartdevelopers.smartfilebrowser.customClasses;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.GridLayoutManager;

public class GalleryLayoutManager extends GridLayoutManager {
    public GalleryLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public GalleryLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public GalleryLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return false;
    }
}
