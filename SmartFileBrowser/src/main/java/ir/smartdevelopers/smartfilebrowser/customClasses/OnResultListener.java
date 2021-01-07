package ir.smartdevelopers.smartfilebrowser.customClasses;

import java.io.File;
import java.io.Serializable;

public abstract class OnResultListener implements Serializable {
    private static final long serialVersionUID=4525632455245521L;
    private int value;

    public abstract void onResult(File[] selectedFiles);

}
