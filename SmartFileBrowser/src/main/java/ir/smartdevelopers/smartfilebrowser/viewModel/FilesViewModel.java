package ir.smartdevelopers.smartfilebrowser.viewModel;

import android.app.Application;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ir.smartdevelopers.smartfilebrowser.R;
import ir.smartdevelopers.smartfilebrowser.customClasses.FileUtil;
import ir.smartdevelopers.smartfilebrowser.models.FileBrowserModel;

public class FilesViewModel extends AndroidViewModel {
    private MutableLiveData<List<FileBrowserModel>> mFilesLiveData;
    private final Map<String, LiveData<List<FileBrowserModel>>> mAllLiveData;
    private Repository mRepository;

    public FilesViewModel(@NonNull Application application) {
        super(application);
        mRepository = new Repository(application);
        mAllLiveData = new HashMap<>();
        mFilesLiveData = new MutableLiveData<>();
    }

    public void getFirstPageFilesLiveData(FileFilter fileFilter) {
        mRepository.getFirstBrowserPageList(null, null,
                FileBrowserModel.MODEL_TYPE_FILE, fileFilter, mFilesLiveData);
    }

    public void getFirstPagePdfLiveData(FileFilter fileFilter) {
        String selection = MediaStore.Files.FileColumns.MIME_TYPE + " LIKE ?";
        String[] selectionArgs = new String[]{"%pdf%"};
        mRepository.getFirstBrowserPageList(selection, selectionArgs,
                FileBrowserModel.MODEL_TYPE_PDF, fileFilter, mFilesLiveData);
    }

    public void getFirstPageAudiosLiveData(FileFilter fileFilter) {
        String selection = MediaStore.Files.FileColumns.MIME_TYPE + " LIKE ?";
        String[] selectionArgs = new String[]{"%audio%"};
        mRepository.getFirstBrowserPageList(selection, selectionArgs,
                FileBrowserModel.MODEL_TYPE_AUDIO, fileFilter, mFilesLiveData);
    }

    public void getFirstPageVideosLiveData(FileFilter fileFilter) {
        String selection = MediaStore.Files.FileColumns.MIME_TYPE + " LIKE ?";
        String[] selectionArgs = new String[]{"%video%"};
        mRepository.getFirstBrowserPageList(selection, selectionArgs,
                FileBrowserModel.MODEL_TYPE_VIDEO, fileFilter, mFilesLiveData);
    }

    public void getFilesList(FileBrowserModel parentModel, FileFilter fileFilter) {

        List<FileBrowserModel> files = getFileList(parentModel, fileFilter);
        if (files != null) {
            mFilesLiveData.setValue(files);
        }

    }

    @Nullable
    private List<FileBrowserModel> getFileList(FileBrowserModel selectedModel, FileFilter fileFilter) {
        if (!selectedModel.getCurrentFile().isDirectory()) {
            return null;
        }
        String goBackSubTitle;
        String goingBackCurrentPath;

        File internalRoot = new File(FileUtil.getInternalStoragePath(getApplication()));
        String externalPath = FileUtil.getExternalStoragePath(getApplication());
        File externalRoot = null;
        if (!TextUtils.isEmpty(externalPath)) {
            //noinspection ConstantConditions
            externalRoot = new File(externalPath);
        }
        if (selectedModel.getParentPath() == null ||
                Objects.equals(internalRoot.getParentFile(), selectedModel.getParentFile()) ||
                Objects.equals(externalRoot == null ? null : externalRoot.getParentFile(), selectedModel.getParentFile())) {
            goBackSubTitle = getApplication().getString(R.string.home);
            goingBackCurrentPath = null;

        } else {
            goBackSubTitle = selectedModel.getParentPath();
            goingBackCurrentPath = selectedModel.getParentPath();

        }

//        if (selectedModel.getParentPath())
        FileBrowserModel goingToParent = new FileBrowserModel(-100, "...", goBackSubTitle,
                FileBrowserModel.MODEL_TYPE_GO_BACK, null, goingBackCurrentPath,
                selectedModel.getParentFile() == null ? null : selectedModel.getParentFile().getParent());
        List<FileBrowserModel> fileBrowserModels = new ArrayList<>();
        fileBrowserModels.add(goingToParent);

        File[] innerFiles = selectedModel.getCurrentFile().listFiles();
        int i = 1;
        for (File file : innerFiles) {
            if (!fileFilter.accept(file)) {
                continue;
            }
            String subTitle;
            if (file.isDirectory()) {
                String[] filesList = file.list();
                subTitle = getFilesCountSubtitle(filesList == null ? 0 : filesList.length);
            } else {
                subTitle = FileUtil.getFileSizeToString(file.length());
            }
            FileBrowserModel model = new FileBrowserModel(i++, file.getName(),
                    subTitle,
                    file.isDirectory() ? FileBrowserModel.MODEL_TYPE_FOLDER : FileBrowserModel.MODEL_TYPE_FILE,
                    FileUtil.getMimeTypeFromPath(file.getPath()), file.getPath(),
                    file.getParent());
            fileBrowserModels.add(model);
        }

        return fileBrowserModels;
    }

    private void observeLiveData(List<FileBrowserModel> fileBrowserModels) {
        for (LiveData<List<FileBrowserModel>> liveData : mAllLiveData.values()) {
            if (liveData.hasActiveObservers()) {
                ((MutableLiveData<List<FileBrowserModel>>) liveData).postValue(fileBrowserModels);
            }
        }
    }

    public MutableLiveData<List<FileBrowserModel>> getFilesLiveData() {
        if (mFilesLiveData == null) {
            mFilesLiveData = new MutableLiveData<>();
        }
        return mFilesLiveData;
    }

    private String getFilesCountSubtitle(int count) {
        if (count > 1) {
            return getApplication().getString(R.string.sfb_inner_more_than_one_file_count_text, count);

        } else {
            return getApplication().getString(R.string.sfb_innser_one_file_count_text, count);

        }
    }
}
