package ir.smartdevelopers.smartfilebrowser.fragments;

import androidx.fragment.app.Fragment;

import java.io.FileFilter;

public class UtilFragment extends Fragment {
    private FileFilter mFileFilter;

    public FileFilter getFileFilter() {
        return mFileFilter;
    }

    public void setFileFilter(FileFilter fileFilter) {
        mFileFilter = fileFilter;
    }
}
