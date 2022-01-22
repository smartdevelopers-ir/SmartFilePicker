package ir.smartdevelopers.smartfilebrowser.customClasses;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ir.smartdevelopers.smartfilebrowser.R;
import ir.smartdevelopers.smartfilebrowser.adapters.FileBrowserAdapter;

public class FileBrowserItemDecoration extends RecyclerView.ItemDecoration {

    private final Rect mBound=new Rect();
    private final Paint mPaint;
    private final Rect mRect;
    public FileBrowserItemDecoration() {
        mPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.parseColor("#D5D5D5"));
        mRect=new Rect();
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        RecyclerView.ViewHolder viewHolder=parent.getChildViewHolder(view);
        if (viewHolder.getItemViewType()== FileBrowserAdapter.VIEW_TYPE_ALL_FILE_TEXT){
            outRect.top= view.getResources().getDimensionPixelSize(R.dimen.sfb_fileBrowserListSpliterSize);
        }
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        c.save();
        int chileCount=parent.getChildCount();
        int size=parent.getResources().getDimensionPixelSize(R.dimen.sfb_fileBrowserListSpliterSize);
        int left=0;
        int right=parent.getWidth();
        for (int i=0;i<chileCount;i++){
            View child=parent.getChildAt(i);
            RecyclerView.ViewHolder viewHolder=parent.getChildViewHolder(child);
            if (viewHolder.getItemViewType()== FileBrowserAdapter.VIEW_TYPE_ALL_FILE_TEXT){
                parent.getDecoratedBoundsWithMargins(child,mBound);
                int bottom=mBound.top+Math.round(child.getTranslationY());
                int top=bottom-size;
                mRect.set(left,top,right,bottom);
                c.drawRect(mRect,mPaint);
                break;
            }
        }

        c.restore();
    }
}
