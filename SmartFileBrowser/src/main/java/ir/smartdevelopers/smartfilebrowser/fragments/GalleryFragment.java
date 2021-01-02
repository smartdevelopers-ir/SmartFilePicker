package ir.smartdevelopers.smartfilebrowser.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ir.smartdevelopers.smartfilebrowser.BuildConfig;
import ir.smartdevelopers.smartfilebrowser.R;
import ir.smartdevelopers.smartfilebrowser.acitivties.FileBrowserMainActivity;
import ir.smartdevelopers.smartfilebrowser.adapters.GalleryAdapter;
import ir.smartdevelopers.smartfilebrowser.customClasses.FileUtil;
import ir.smartdevelopers.smartfilebrowser.customClasses.GalleyItemDecoration;
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
    private OnItemSelectListener<GalleryModel> mOnItemSelectListener;
    private String tackingPictureFilePath;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FileBrowserMainActivity activity=(FileBrowserMainActivity) getActivity();
        if (activity!=null){
            mOnItemSelectListener=activity.getOnGalleryItemSelectListener();
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
        mGalleryAdapter.setOnItemClickListener(mGalleryModelItemClickListener);
        mGalleryAdapter.setOnItemSelectListener(mOnItemSelectListener);
        mGalleryRecyclerView.setAdapter(mGalleryAdapter);

        mGalleryViewModel.getAllGalleryModels(true).observe(this, new Observer<List<GalleryModel>>() {
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
                    //TODO : select item
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
            mGalleryViewModel.getAllGalleryModels(true);
        }else {
            mGalleryViewModel.getGalleryModelsByAlbumName(albumModel.getName());
        }
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
