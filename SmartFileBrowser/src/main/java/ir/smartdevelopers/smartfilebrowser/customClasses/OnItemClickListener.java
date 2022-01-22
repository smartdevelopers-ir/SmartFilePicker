package ir.smartdevelopers.smartfilebrowser.customClasses;

import android.view.View;

public interface OnItemClickListener<T> {
    void onItemClicked(T t, View view, int position);
}
