package ir.smartdevelopers.smartfilebrowser.customClasses;

import java.io.File;

public interface OnGalleryItemClickListener {
    void onItemClicked(File clickedFile,File[] allFiles);
}
