package ir.smartdevelopers.smartfilebrowser.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ir.smartdevelopers.smartfilebrowser.customClasses.SelectionHelper;

public class SelectionFileViewModel extends ViewModel {
    private MutableLiveData<List<File>> mSelectedFilesLiveData;
    private MutableLiveData<SelectionHelper> mSelectionHelperLiveData;

    public SelectionFileViewModel() {
        mSelectedFilesLiveData = new MutableLiveData<>();
        mSelectedFilesLiveData.setValue(new ArrayList<>());
        mSelectionHelperLiveData=new MutableLiveData<>();
        mSelectionHelperLiveData.setValue(new SelectionHelper());
    }

    public void addSelectedFile(File file){
        mSelectedFilesLiveData.getValue().add(file);
    }
    public void removeSelectedFile(File file){
        mSelectedFilesLiveData.getValue().remove(file);
    }
    public List<File> getSelectedFiles(){
        return mSelectedFilesLiveData.getValue();
    }

    public MutableLiveData<SelectionHelper> getSelectionHelperLiveData() {
        return mSelectionHelperLiveData;
    }
    public void onItemSelected(int count,boolean isMultiSelectEnabled){
        SelectionHelper helper=mSelectionHelperLiveData.getValue();
        helper.isMultiSelectionEnabled=isMultiSelectEnabled;
        helper.selectionCount=count;
        mSelectionHelperLiveData.setValue(helper);

    }
    public boolean isMultiSelectionEnabled(){
        return mSelectionHelperLiveData.getValue().isMultiSelectionEnabled;
    }

    public void removeAllSelections() {
        mSelectedFilesLiveData.setValue(new ArrayList<>());
        mSelectionHelperLiveData.setValue(new SelectionHelper());
    }
}
