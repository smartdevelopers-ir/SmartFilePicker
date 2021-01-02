package ir.smartdevelopers.smartfilebrowser.customClasses;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;

public class SFBFileFilter implements FileFilter, Serializable {

    private static final long serialVersionUID = -5777924449051892924L;

    @Override
    public boolean accept(File pathname) {
        return false;
    }
}
