package ir.smartdevelopers.smartfilebrowser.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
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
import ir.smartdevelopers.smartfilebrowser.customClasses.OnItemSelectListener;
import ir.smartdevelopers.smartfilebrowser.models.AlbumModel;
import ir.smartdevelopers.smartfilebrowser.models.GalleryModel;
import ir.smartdevelopers.smartfilebrowser.viewModel.GalleryViewModel;

public class GalleryFragment extends Fragment {

    private static final int REQ_CODE_TACK_PICTURE = 303;
    private RecyclerView mGalleryRecyclerView;
    private GalleryAdapter mGalleryAdapter;
    private GalleryViewModel mGalleryViewModel;
    private GridLayoutManager mGridLayoutManager;
    private OnItemClickListener<GalleryModel> mGalleryModelItemClickListener;
    private OnItemClickListener<GalleryModel> mOnGalleryItemClickListener_m;
    private OnItemChooseListener mOnItemChooseListener;
    private OnItemSelectListener<GalleryModel> mOnItemSelectListener;
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
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FileBrowserMainActivity activity=(FileBrowserMainActivity) getActivity();
        if (activity!=null){
            mOnItemSelectListener=activity.getOnGalleryItemSelectListener();
            mOnGalleryItemClickListener_m=activity.getOnGalleryItemClickListener();
            mOnItemChooseListener=activity.getOnItemChooseListener();
        }
        Bundle bundle=getArguments();
        if (bundle != null) {
            mShowCamera=bundle.getBoolean("mShowCamera",true);
            mShowVideosInGallery=bundle.getBoolean("mShowVideosInGallery",true);
            mCanSelectMultipleInGallery=bundle.getBoolean("mCanSelectMultipleInGallery",true);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery_layout,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mGalleryViewModel=new ViewModelProvider(this,
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication()))
                .get(GalleryViewModel.class);
        findViews(view);
        initListeners();
        int spanCount=getResources().getInteger(R.integer.sfb_gallery_grid);
        int gapSpace=getResources().getDimensionPixelSize(R.dimen.sfb_gallery_gap_size);
        mGridLayoutManager=new GridLayoutManager(getContext(),spanCount);
        mGalleryRecyclerView.setLayoutManager(mGridLayoutManager);
        mGalleryRecyclerView.addItemDecoration(new GalleyItemDecoration(spanCount,gapSpace,true));

        mGalleryAdapter=new GalleryAdapter();
        mGalleryAdapter.setCanSelectMultiple(mCanSelectMultipleInGallery);
        mGalleryAdapter.setOnItemClickListener(mGalleryModelItemClickListener);
        mGalleryAdapter.setOnItemSelectListener(mOnItemSelectListener);
        mGalleryAdapter.setOnItemChooseListener(mOnItemChooseListener);
        mGalleryRecyclerView.setAdapter(mGalleryAdapter);

        mGalleryViewModel.getAllGalleryModels(mShowCamera,mShowVideosInGallery).observe(this, new Observer<List<GalleryModel>>() {
            @Override
            public void onChanged(List<GalleryModel> galleryModels) {
                mGalleryAdapter.setList(galleryModels);
            }
        });

    }

    private void initListeners() {
        mGalleryModelItemClickListener=new OnItemClickListener<GalleryModel>() {
            @Override
            public void onItemClicked(GalleryModel model, int position) {
                if (model.getType()==GalleryModel.TYPE_CAMERA){
                    openCamera();
                }else {
                    if (mOnGalleryItemClickListener_m != null) {
                        mOnGalleryItemClickListener_m.onItemClicked(model,position);
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
            startActivityForResult(cameraIntent, REQ_CODE_TACK_PICTURE);
        }
    }

    private void findViews(View view) {
        mGalleryRecyclerView=view.findViewById(R.id.sfb_fragment_gallery_recyclerView);

    }
    public void updateGallery(AlbumModel albumModel){
        if (albumModel.getId()==-1){
            mGalleryViewModel.getAllGalleryModels(mShowCamera, mShowVideosInGallery);
        }else {
            mGalleryViewModel.getGalleryModelsByAlbumName(albumModel.getName(),mShowVideosInGallery);
        }
    }

    public List<File> getSelectedFiles(){
        if (mGalleryAdapter==null){
            return Collections.emptyList();
        }
        List<File> selectedFiles=new ArrayList<>();
        List<GalleryModel> selectedModels=mGalleryAdapter.getSelectedModels();
       for (GalleryModel model:selectedModels){
           selectedFiles.add(model.getCurrentFile());
       }
       return selectedFiles;
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
}
