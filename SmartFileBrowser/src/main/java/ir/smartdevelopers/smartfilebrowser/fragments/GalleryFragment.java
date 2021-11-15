package ir.smartdevelopers.smartfilebrowser.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ir.smartdevelopers.smartfilebrowser.R;
import ir.smartdevelopers.smartfilebrowser.acitivties.FileBrowserMainActivity;
import ir.smartdevelopers.smartfilebrowser.adapters.GalleryAdapter;
import ir.smartdevelopers.smartfilebrowser.customClasses.FileUtil;
import ir.smartdevelopers.smartfilebrowser.customClasses.GalleyItemDecoration;
import ir.smartdevelopers.smartfilebrowser.customClasses.OnItemChooseListener;
import ir.smartdevelopers.smartfilebrowser.customClasses.OnItemClickListener;
import ir.smartdevelopers.smartfilebrowser.customClasses.OnItemLongClickListener;
import ir.smartdevelopers.smartfilebrowser.customClasses.OnItemSelectListener;
import ir.smartdevelopers.smartfilebrowser.models.AlbumModel;
import ir.smartdevelopers.smartfilebrowser.models.FileModel;
import ir.smartdevelopers.smartfilebrowser.models.GalleryModel;
import ir.smartdevelopers.smartfilebrowser.viewModel.GalleryViewModel;
import ir.smartdevelopers.smartfilebrowser.viewModel.SelectionFileViewModel;

public class GalleryFragment extends Fragment {
    public static final String FRAGMENT_TAG="gallery_fragment";

    public static final int REQ_CODE_TACK_PICTURE = 303;
    private RecyclerView mGalleryRecyclerView;
    private GalleryAdapter mGalleryAdapter;
    private GalleryViewModel mGalleryViewModel;
    private SelectionFileViewModel mSelectionFileViewModel;
    private GridLayoutManager mGridLayoutManager;
    private OnItemClickListener<GalleryModel> mGalleryModelItemClickListener;
    private OnItemLongClickListener<GalleryModel> mGalleryModelItemLongClickListener;
    private OnItemClickListener<GalleryModel> mOnZoomOutClickListener;
    private OnItemClickListener<GalleryModel> mOnGalleryItemClickListener_m;
    private OnItemChooseListener mOnItemChooseListener;
    private OnItemSelectListener<FileModel> mOnItemSelectListener;
    private String tackingPictureFilePath;
    private boolean mShowVideosInGallery;
    private boolean mShowCamera;
    private boolean mCanSelectMultipleInGallery;

    public static GalleryFragment getInstance(boolean showVideosInGallery,boolean showCamera,
                                              boolean canSelectMultipleInGallery) {
        GalleryFragment fragment=new GalleryFragment();
        Bundle bundle=new Bundle();
        bundle.putBoolean("mShowVideosInGallery",showVideosInGallery);

        bundle.putBoolean("mShowCamera",showCamera);
        bundle.putBoolean("mCanSelectMultipleInGallery",canSelectMultipleInGallery);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FileBrowserMainActivity activity=(FileBrowserMainActivity) getActivity();
        if (activity!=null){
            mOnItemSelectListener=activity.getOnFileItemSelectListener();
            mOnGalleryItemClickListener_m=activity.getOnGalleryItemClickListener();
            mGalleryModelItemLongClickListener=activity.getOnGalleryItemLongClickListener();
            mOnItemChooseListener=activity.getOnItemChooseListener();
            mOnZoomOutClickListener=activity.getOnZoomOutClickListener();
        }
        Bundle bundle=getArguments();
        if (bundle != null) {
            mShowCamera=bundle.getBoolean("mShowCamera",true);
            mShowVideosInGallery=bundle.getBoolean("mShowVideosInGallery",true);
            mCanSelectMultipleInGallery=bundle.getBoolean("mCanSelectMultipleInGallery",true);
        }
        if (savedInstanceState!=null){
            tackingPictureFilePath=savedInstanceState.getString("tackingPictureFilePath");
        }
        return inflater.inflate(R.layout.fragment_gallery_layout,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)){
            mShowCamera=false;
        }
        mGalleryViewModel=new ViewModelProvider(this,
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication()))
                .get(GalleryViewModel.class);
        mSelectionFileViewModel=new ViewModelProvider(getActivity()).get(SelectionFileViewModel.class);
        findViews(view);
        initListeners();
        int spanCount=getResources().getInteger(R.integer.sfb_gallery_grid);
        int gapSpace=getResources().getDimensionPixelSize(R.dimen.sfb_gallery_gap_size);
        mGridLayoutManager=new GridLayoutManager(getContext(),spanCount);
        mGalleryRecyclerView.setLayoutManager(mGridLayoutManager);
        mGalleryRecyclerView.addItemDecoration(new GalleyItemDecoration(spanCount,gapSpace,true));

        mGalleryAdapter=new GalleryAdapter(mSelectionFileViewModel.getSelectedFiles());
        mGalleryAdapter.setCanSelectMultiple(mCanSelectMultipleInGallery);
        mGalleryAdapter.setOnItemClickListener(mGalleryModelItemClickListener);
        mGalleryAdapter.setOnItemSelectListener(mOnItemSelectListener);
        mGalleryAdapter.setOnItemLongClickListener(mGalleryModelItemLongClickListener);
        mGalleryAdapter.setOnZoomOutClickListener(mOnZoomOutClickListener);
        mGalleryAdapter.setOnItemChooseListener(mOnItemChooseListener);
        mGalleryRecyclerView.setAdapter(mGalleryAdapter);

        LiveData<List<GalleryModel>> allGalleryModels= mGalleryViewModel
                .getAllGalleryModels(mShowCamera,mShowVideosInGallery,savedInstanceState==null);

        allGalleryModels.observe(getViewLifecycleOwner(), new Observer<List<GalleryModel>>() {
            @Override
            public void onChanged(List<GalleryModel> galleryModels) {
                mGalleryAdapter.setList(galleryModels);
            }
        });

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tackingPictureFilePath",tackingPictureFilePath);

    }

    private void initListeners() {
        mGalleryModelItemClickListener=new OnItemClickListener<GalleryModel>() {
            @Override
            public void onItemClicked(GalleryModel model, View view, int position) {
                if (model.getType()==GalleryModel.TYPE_CAMERA){
                    openCamera();
                }else {
                    if (mOnGalleryItemClickListener_m != null) {
                        mOnGalleryItemClickListener_m.onItemClicked(model,view,position);
                    }

                }
            }
        };
    }
    private void openCamera() {

        Intent cameraIntent=new Intent( MediaStore.ACTION_IMAGE_CAPTURE);
        File newPic= null;
        try {
            newPic= FileUtil.getImageFile(getContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (newPic!=null) {
            tackingPictureFilePath=newPic.getAbsolutePath();
            Uri uri = FileProvider.getUriForFile(getContext(),
                    getContext().getPackageName() + ".provider",
                    newPic);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (cameraIntent.resolveActivity(getContext().getPackageManager())==null){
                Toast.makeText(getContext(), R.string.this_device_dose_not_have_camera_app, Toast.LENGTH_SHORT).show();
                return;
            }
            List<ResolveInfo> resolveInfos=getContext().getPackageManager().queryIntentActivities(cameraIntent,
                    PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo info:resolveInfos){
                String packageName=info.activityInfo.packageName;
                getContext().grantUriPermission(packageName,uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION|
                        Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }

            getActivity().startActivityForResult(cameraIntent, REQ_CODE_TACK_PICTURE);
        }
    }

    private void findViews(View view) {
        mGalleryRecyclerView=view.findViewById(R.id.sfb_fragment_gallery_recyclerView);

    }
    public void updateGallery(AlbumModel albumModel){
        if (albumModel.getId()==-1){
            mGalleryViewModel.getAllGalleryModels(mShowCamera, mShowVideosInGallery,true);
        }else {
            mGalleryViewModel.getGalleryModelsByAlbumName(albumModel.getName(),mShowVideosInGallery);
        }
    }

    public List<File> getSelectedFiles(){
        if (mGalleryAdapter==null){
            return Collections.emptyList();
        }
        return mSelectionFileViewModel.getSelectedFiles();
//        List<File> selectedFiles=new ArrayList<>();
//        List<GalleryModel> selectedModels=mGalleryAdapter.getSelectedModels();
//       for (GalleryModel model:selectedModels){
//           selectedFiles.add(model.getCurrentFile());
//       }
//       return selectedFiles;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQ_CODE_TACK_PICTURE){
            if (resultCode==Activity.RESULT_OK){
                File takenPic=new File(tackingPictureFilePath);
                if (takenPic.isFile()){
                   FileUtil.scanMediaFile(getContext(),takenPic);
                   GalleryModel newPicModel=new GalleryModel();
                   newPicModel.setType(FileUtil.TYPE_IMAGE);
                   newPicModel.setSelected(true);
                   newPicModel.setName(takenPic.getName());
                   newPicModel.setPath(takenPic.getPath());
//                   mGalleryViewModel.insertModel(newPicModel);
                   mGalleryAdapter.addNewPic(newPicModel);

                }
            }
        }


    }

    public void removeAllSelections() {
        mGalleryAdapter.removeAllSelections();
    }

    public int getSelectionCount() {
        if (mGalleryAdapter!=null){
            return mGalleryAdapter.getSelectionCount();
        }
        return 0;
    }

    public void scrollToFirstPos() {
        mGalleryRecyclerView.smoothScrollToPosition(0);
    }

    public void imageUpdated( String newFilePath, int editedImagePosition) {
        if (mGalleryAdapter!=null){
            mGalleryAdapter.getItem(editedImagePosition).setPath(newFilePath);
//            mGalleryAdapter.notifyItemChanged(editedImagePosition);
        }
    }
}
