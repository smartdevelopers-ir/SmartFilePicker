package ir.smartdevelopers.smartfilebrowser.customClasses;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;

public class SFBFileFilter implements  Serializable {

    private static final long serialVersionUID = -5777924449051892924L;
    private final HashSet<String> mExtensionList;
    private final boolean mIsFolder;
    private final boolean mIsFile;


    private SFBFileFilter(HashSet<String> extensionList,
                         boolean isFolder, boolean isFile) {
        mExtensionList = extensionList;
        mIsFolder = isFolder;
        mIsFile = isFile;

    }

    public HashSet<String> getExtensionList() {
        return mExtensionList;
    }

    public boolean isFolder() {
        return mIsFolder;
    }

    public boolean isFile() {
        return mIsFile;
    }



    public static class Builder {
        private HashSet<String> mExtensionList=new HashSet<>();
        private boolean isFolder=true;
        private boolean isFile=true;


        public Builder isFolder(boolean folder) {
            isFolder = folder;
            return this;
        }

        public Builder isFile(boolean file) {
            isFile = file;
            return this;
        }



        public Builder setExtensionList(HashSet<String> extensionList) {
            mExtensionList = extensionList;
            return this;
        }
        public Builder setExtensionList(String[] extensionList) {
            mExtensionList.clear();
            mExtensionList.addAll(Arrays.asList(extensionList));
            return this;
        }
        public Builder includeExtension(String extension) {
            mExtensionList.add(extension.toLowerCase());
            return this;
        }

        public SFBFileFilter build(){
            return new SFBFileFilter(mExtensionList,isFolder,isFile);
        }
    }
}
