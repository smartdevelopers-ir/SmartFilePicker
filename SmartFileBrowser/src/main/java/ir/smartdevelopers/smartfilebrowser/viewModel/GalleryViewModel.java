package ir.smartdevelopers.smartfilebrowser.viewModel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import ir.smartdevelopers.smartfilebrowser.models.AlbumModel;
import ir.smartdevelopers.smartfilebrowser.models.GalleryModel;

public class GalleryViewModel extends AndroidViewModel {
    private MutableLiveData<List<GalleryModel>> mGalleryModelsLiveData;
    private  LiveData<List<AlbumModel>> mAlbumLiveData;
    private Repository mRepository;
    public GalleryViewModel(@NonNull Application application) {
        super(application);
        mRepository = new Repository(application);
        mGalleryModelsLiveData=mRepository.getGalleryListLiveData();

    }

    private boolean mustFetchNewData(){
        if (mGalleryModelsLiveData==null){
            return true;
        }
        if (mGalleryModelsLiveData.getValue()==null){
            return true;
        }
        return  mGalleryModelsLiveData.getValue().size() == 0;
    }
    public void getAllGalleryModels(boolean addCamera, boolean showVideosInGallery,boolean forceFetchNewData){

        if (mustFetchNewData() || forceFetchNewData) {
            mRepository.getGalleryMediaList(null,null,addCamera,showVideosInGallery);
        }else {
            mGalleryModelsLiveData.setValue(mRepository.getGalleryListLiveData().getValue());
        }

    }
    public void  getGalleryModelsByAlbumName(String albumName,boolean showVideosInGallery) {
        @SuppressLint("InlinedApi")
        String selections = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + "=?";
        String[] selectionArgs = new String[]{albumName};
        mRepository.getGalleryMediaList(selections, selectionArgs, false, showVideosInGallery);
    }
    public LiveData<List<AlbumModel>> getAllImageAlbums(){
        if (mAlbumLiveData == null) {
            mAlbumLiveData=mRepository.getAlbums();
        }
        return mAlbumLiveData;
    }

    public LiveData<List<GalleryModel>> getGalleryModelsLiveData() {
        return mGalleryModelsLiveData;
    }

    public void insertModel(GalleryModel newPicModel) {
        mGalleryModelsLiveData.getValue().add(1,newPicModel);
    }
}
