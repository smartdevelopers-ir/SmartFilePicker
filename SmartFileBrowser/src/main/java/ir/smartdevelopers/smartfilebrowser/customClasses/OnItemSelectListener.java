package ir.smartdevelopers.smartfilebrowser.customClasses;

public interface OnItemSelectListener<T> {
    void onItemSelected(T t, int position, int selectionCount);
}
