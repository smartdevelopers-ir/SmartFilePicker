package ir.smartdevelopers.smartfilebrowser.viewModel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import ir.smartdevelopers.smartfilebrowser.models.AlbumModel;
import ir.smartdevelopers.smartfilebrowser.models.GalleryModel;

public class GalleryViewModel extends AndroidViewModel {
    private  LiveData<List<GalleryModel>> mGalleryModelsLiveData;
    private  LiveData<List<AlbumModel>> mAlbumLiveData;
    private Repository mRepository;
    public GalleryViewModel(@NonNull Application application) {
        super(application);
        mRepository = new Repository(application);

    }

    public LiveData<List<GalleryModel>> getAllGalleryModels(boolean addCamera, boolean showVideosInGallery){

        mGalleryModelsLiveData=mRepository.getGalleryMediaList(null,null,addCamera,showVideosInGallery);
        return mGalleryModelsLiveData;
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

}
