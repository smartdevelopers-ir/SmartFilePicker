package ir.smartdevelopers.smartfilebrowser.customClasses;

import android.view.View;

public interface OnItemLongClickListener<T> {
    void onLongClicked(T t, View view,int position);
}
