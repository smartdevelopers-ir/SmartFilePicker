package ir.smartdevelopers.smartfilebrowser.viewModel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.File;
import java.io.FileFilter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ir.smartdevelopers.smartfilebrowser.R;
import ir.smartdevelopers.smartfilebrowser.customClasses.FileUtil;
import ir.smartdevelopers.smartfilebrowser.customClasses.Utils;
import ir.smartdevelopers.smartfilebrowser.models.AlbumModel;
import ir.smartdevelopers.smartfilebrowser.models.FileBrowserModel;
import ir.smartdevelopers.smartfilebrowser.models.GalleryModel;

public class Repository {
    private ExecutorService mExecutorService;
    private ContentResolver mContentResolver;
    private WeakReference<Context> wContext;
    private MutableLiveData<List<GalleryModel>> galleryList;
    private MutableLiveData<List<FileBrowserModel>> mFileBrowserFirstPageListLiveData;
    public Repository(Application application) {
        mExecutorService= Executors.newCachedThreadPool();
        mContentResolver=application.getContentResolver();
        wContext=new WeakReference<>(application);
    }
    public LiveData<List<GalleryModel>> getGalleryMediaList(String selection, String[] selectionArgs, boolean addCameraItem){
       if (galleryList==null){
           galleryList=new MutableLiveData<>();
       }
        mExecutorService.execute(()->{
            List<GalleryModel> galleryModelList=new ArrayList<>();
            String[] imageProjection={MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED,
                    MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.MIME_TYPE};
            String[] videoProjection={MediaStore.Video.Media._ID,
                    MediaStore.Video.Media.DATA, MediaStore.Video.Media.DATE_ADDED,
                    MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.MIME_TYPE};
            Cursor externalImageCursor=mContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,imageProjection,
                    selection,selectionArgs,MediaStore.Images.Media.DATE_ADDED+" DESC");

            Cursor externalVideoCursor=mContentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,videoProjection,
                    selection,selectionArgs, MediaStore.Video.Media.DATE_ADDED+" DESC");

            galleryModelList.addAll(getGalleryModel(externalImageCursor,imageProjection));
            galleryModelList.addAll(getGalleryModel(externalVideoCursor,videoProjection));
            if (addCameraItem){
                GalleryModel cameraModel=new GalleryModel();
                cameraModel.setType(GalleryModel.TYPE_CAMERA);
                galleryModelList.add(0,cameraModel);
            }
            galleryList.postValue(galleryModelList);

        });

        return galleryList;
    }
    @SuppressLint("InlinedApi")
    public LiveData<List<AlbumModel>> getAlbums(){
        MutableLiveData<List<AlbumModel>> listMutableLiveData=new MutableLiveData<>();
        mExecutorService.execute(()->{

            String[] projection= {MediaStore.Files.FileColumns.BUCKET_ID,
                    MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,
                    MediaStore.Files.FileColumns.DATE_ADDED,
                    MediaStore.Files.FileColumns.DATA};
            String selection=MediaStore.Files.FileColumns.MIME_TYPE+" NOT NULL ";
            String[] mediaTypes={String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
                    String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)};
//            String[] selectionArgs={Utils.join(mediaTypes,",")};
            String orderBy=MediaStore.Files.FileColumns.DATE_ADDED+" DESC";
           Cursor imageCursor= mContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,selection,null,orderBy);
            Cursor videoCursor= mContentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    projection,selection,null,orderBy);

            Set<AlbumModel> imageModelSet=getAlbumModels(imageCursor,projection);
            Set<AlbumModel> videoModelSet=getAlbumModels(videoCursor,projection);
            /*get newest item to set its path for allMedia item*/
            List<AlbumModel> temp=new ArrayList<>(imageModelSet);
            temp.addAll(videoModelSet);
            AlbumModel newest= Collections.max(temp, new Comparator<AlbumModel>() {
                @Override
                public int compare(AlbumModel o1, AlbumModel o2) {
                    return Long.compare(o1.getTimeTaken(),o2.getTimeTaken());
                }
            });
            AlbumModel allMedia=new AlbumModel(-1,wContext.get().getString(R.string.sfb_all_media),newest.getImagePath(),0);
            Set<AlbumModel> albumModelSet=new HashSet<>(temp);
            List<AlbumModel> albumModelList=new ArrayList<>(albumModelSet);
            albumModelList.add(0,allMedia);

            listMutableLiveData.postValue(albumModelList);
        });
        return listMutableLiveData;
    }
    private Set<AlbumModel> getAlbumModels(Cursor cursor,String[] projection){
        Set<AlbumModel> albumModels=new HashSet<>();
        if (cursor==null){
            return albumModels;
        }
        int bucketIdIndex=cursor.getColumnIndex(projection[0]);
        int bucketNameIndex=cursor.getColumnIndex(projection[1]);
        int dateTakenIndex=cursor.getColumnIndex(projection[2]);
        int pathIndex=cursor.getColumnIndex(projection[3]);
        while (cursor.moveToNext()){
            String path=cursor.getString(pathIndex);
            long id=cursor.getLong(bucketIdIndex);
            long timeTaken=cursor.getLong(dateTakenIndex);
            String bucketName=cursor.getString(bucketNameIndex);
            AlbumModel albumModel=new AlbumModel(id,bucketName,path, timeTaken);
            albumModels.add(albumModel);
        }
        cursor.close();
        return albumModels;
    }
    private List<GalleryModel> getGalleryModel(Cursor cursor,String[] projection){

        List<GalleryModel> galleryModelList=new ArrayList<>();
        if (cursor==null){
            return galleryModelList;
        }
        int idIndex=cursor.getColumnIndex(projection[0]);
        int pathIndex=cursor.getColumnIndex(projection[1]);
        int dateIndex=cursor.getColumnIndex(projection[2]);
        int nameIndex=cursor.getColumnIndex(projection[3]);
        int mimeTypeIndex=cursor.getColumnIndex(projection[4]);
        while (cursor.moveToNext()){
            GalleryModel model=new GalleryModel();
            model.setId(cursor.getLong(idIndex));
            model.setPath(cursor.getString(pathIndex));
            model.setName(cursor.getString(nameIndex));
            model.setDateAdded(cursor.getLong(dateIndex));
            model.setType(FileUtil.getFileTypeCode(cursor.getString(mimeTypeIndex)));
            galleryModelList.add(model);
        }
        cursor.close();
        return galleryModelList;
    }

    /**@param modelType is one of {@link FileBrowserModel} model types*/
    public LiveData<List<FileBrowserModel>> getFirstBrowserPageList(String selection,
                                                                    String[] selectionArgs,int modelType,
                                                                    FileFilter fileFilter){

        if (mFileBrowserFirstPageListLiveData==null){
            mFileBrowserFirstPageListLiveData=new MutableLiveData<>();
        }
        String extraQuery=MediaStore.Files.FileColumns.MIME_TYPE+" IS NOT NULL AND " +
                MediaStore.Files.FileColumns.DATA+" NOT LIKE '%.thumbnail%'";
        selection= TextUtils.isEmpty(selection) ? extraQuery
                : selection+" AND "+extraQuery;

        String finalSelection = selection ;
        mExecutorService.execute(()->{

            String[] projection={MediaStore.Files.FileColumns._ID,
                    MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.DATE_ADDED,
                    MediaStore.Files.FileColumns.DISPLAY_NAME, MediaStore.Files.FileColumns.MIME_TYPE};

            Cursor cursor=mContentResolver.query(MediaStore.Files.getContentUri("external"),projection, finalSelection,
                    selectionArgs, MediaStore.MediaColumns.DATE_ADDED+" ASC LIMIT 20");
            int idIndex=cursor.getColumnIndex(projection[0]);
            int pathIndex=cursor.getColumnIndex(projection[1]);
            int dateIndex=cursor.getColumnIndex(projection[2]);
            int nameIndex=cursor.getColumnIndex(projection[3]);
            int mimeTypeIndex=cursor.getColumnIndex(projection[4]);
            List<FileBrowserModel> fileBrowserModels=new ArrayList<>();
            while (cursor.moveToNext()){
                String path=cursor.getString(pathIndex);
                File file=new File(path);
                if (file.isDirectory()){
                    continue;
                }
                if (!fileFilter.accept(file)){
                    continue;
                }
                long id=cursor.getLong(idIndex);

                long date=cursor.getLong(dateIndex);
                String name=file.getName();
                String fileMimeType=cursor.getString(mimeTypeIndex);

                String fileSizeSt=FileUtil.getFileSizeToString(file.length());
                FileBrowserModel model=new FileBrowserModel(id,name,fileSizeSt,modelType,
                        fileMimeType,path,null);
                fileBrowserModels.add(model);
            }
            cursor.close();
            if (wContext.get()==null){return;}
            mFileBrowserFirstPageListLiveData.postValue(Utils.generateFirstPageList(wContext.get(),fileBrowserModels));

        });
        return mFileBrowserFirstPageListLiveData;


    }
}
