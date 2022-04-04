package ir.smartdevelopers.smartfilebrowser.customClasses;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

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

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {

        startSmoothScroll(new Scroller(recyclerView.getContext(),position));
    }

    public static class Scroller extends LinearSmoothScroller {

        public Scroller(Context context,int targetPosition) {
            super(context);
            setTargetPosition(targetPosition);

        }



        @Override
        protected int calculateTimeForScrolling(int dx) {
            return 800;
        }

        @Override
        protected int calculateTimeForDeceleration(int dx) {
            return 200;
        }



    }
}
