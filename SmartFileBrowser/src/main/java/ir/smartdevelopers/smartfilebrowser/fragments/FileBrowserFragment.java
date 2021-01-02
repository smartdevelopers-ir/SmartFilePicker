package ir.smartdevelopers.smartfilebrowser.fragments;

import android.app.Application;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileFilter;
import java.util.List;

import ir.smartdevelopers.smartfilebrowser.R;
import ir.smartdevelopers.smartfilebrowser.acitivties.FileBrowserMainActivity;
import ir.smartdevelopers.smartfilebrowser.adapters.FileBrowserAdapter;
import ir.smartdevelopers.smartfilebrowser.customClasses.OnItemClickListener;
import ir.smartdevelopers.smartfilebrowser.customClasses.OnItemSelectListener;
import ir.smartdevelopers.smartfilebrowser.customClasses.OnSearchListener;
import ir.smartdevelopers.smartfilebrowser.customClasses.SelectionHelper;
import ir.smartdevelopers.smartfilebrowser.models.FileBrowserModel;
import ir.smartdevelopers.smartfilebrowser.viewModel.FilesViewModel;
import ir.smartdevelopers.smartfilebrowser.viewModel.SelectionFileViewModel;

public class FileBrowserFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private FileBrowserAdapter mFileBrowserAdapter;
    private FilesViewModel mFilesViewModel;
    private OnItemClickListener<FileBrowserModel> mOnItemClickListener;
    private FileFilter mFileFilter;
    private OnItemSelectListener<FileBrowserModel> mOnItemSelectListener;
    private SelectionFileViewModel mSelectionFileViewModel;
    private FileBrowserMainActivity.PageType mPageType;
    private OnSearchListener mOnSearchListener;
    private Group mNoItemGroup;
    private TextView txtNotFoundSubTitle;

    public static FileBrowserFragment getInstance() {
        FileBrowserFragment fragment=new FileBrowserFragment();

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FileBrowserMainActivity activity= (FileBrowserMainActivity) getActivity();
        if (activity!=null) {
            mFileFilter = activity.getFileFilter();
            mOnItemSelectListener = activity.getOnFileItemSelectListener();
            mPageType=activity.getPageType();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_file_browser,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFilesViewModel=new ViewModelProvider(this,
                new ViewModelProvider.AndroidViewModelFactory((Application)getContext().getApplicationContext()))
                .get(FilesViewModel.class);
        mSelectionFileViewModel=new ViewModelProvider(getActivity()).get(SelectionFileViewModel.class);
       findViews(view);
        initListeners();
       mLinearLayoutManager=new LinearLayoutManager(getContext());
       mRecyclerView.setLayoutManager(mLinearLayoutManager);
//       mRecyclerView.addItemDecoration(new FileBrowserItemDecoration());
        mFileBrowserAdapter=new FileBrowserAdapter(mSelectionFileViewModel.getSelectedFiles());
        mFileBrowserAdapter.setOnItemClickListener(mOnItemClickListener);
        mFileBrowserAdapter.setOnItemSelectListener(mOnItemSelectListener);
        mFileBrowserAdapter.setOnSearchListener(mOnSearchListener);
        mRecyclerView.setAdapter(mFileBrowserAdapter);
        getFirstPageList();
        mSelectionFileViewModel.getSelectionHelperLiveData().observe(this, new Observer<SelectionHelper>() {
            @Override
            public void onChanged(SelectionHelper selectionHelper) {
                mFileBrowserAdapter.setMultiSelectEnabled(selectionHelper.isMultiSelectionEnabled);
            }
        });
    }

    private void initListeners() {
        mOnItemClickListener=new OnItemClickListener<FileBrowserModel>() {
            @Override
            public void onItemClicked(FileBrowserModel model, int position) {
                if (model.getCurrentFile()!=null){
                    mFilesViewModel.getFilesList(model, mFileFilter).observe(getViewLifecycleOwner(), new Observer<List<FileBrowserModel>>() {
                        @Override
                        public void onChanged(List<FileBrowserModel> fileBrowserModels) {

                            if (fileBrowserModels!=null){
                                mFileBrowserAdapter.setList(fileBrowserModels);
                            }

                        }
                    });
                }else {
                    getFirstPageList();
                }
            }
        };
        mOnSearchListener=new OnSearchListener() {
            @Override
            public void onSearch(int count,String searchedText) {
                if (count==0){
                    showNoItem(searchedText);
                }else {
                    hideNoItem();
                }
            }
        };
    }

    private void hideNoItem() {
        mNoItemGroup.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showNoItem(String searchedText) {
        mRecyclerView.setVisibility(View.GONE);
        mNoItemGroup.setVisibility(View.VISIBLE);
        CharSequence text= Html.fromHtml(getString(R.string.sfb_no_results_found_sub_title,searchedText));
        txtNotFoundSubTitle.setText(text);
    }

    @SuppressWarnings("ConstantConditions")
    public void changePages(FileBrowserMainActivity.PageType pageType){
        mFileFilter=((FileBrowserMainActivity)getActivity()).getFileFilter();
        mPageType=pageType;
        getFirstPageList();
    }
    private void getFirstPageList() {
        LiveData<List<FileBrowserModel>> liveData=null;
        switch (mPageType){
            case TYPE_FILE_BROWSER:
                liveData= mFilesViewModel.getFirstPageFilesLiveData(mFileFilter);
                break;
            case TYPE_VIDEO:
                liveData=mFilesViewModel.getFirstPageVideosLiveData(mFileFilter);
                break;
            case TYPE_PDF:
                liveData=mFilesViewModel.getFirstPagePdfLiveData(mFileFilter);
                break;
            case TYPE_AUDIO:
                liveData=mFilesViewModel.getFirstPageAudiosLiveData(mFileFilter);
                break;
        }

        if (liveData.hasObservers()){
            liveData.removeObservers(this);
        }
        liveData.observe(this, new Observer<List<FileBrowserModel>>() {
            @Override
            public void onChanged(List<FileBrowserModel> fileBrowserModels) {
                mFileBrowserAdapter.setList(fileBrowserModels);

            }
        });
    }


    private void findViews(View view) {
        mRecyclerView=view.findViewById(R.id.fragment_file_browser_recyclerView);
        mNoItemGroup=view.findViewById(R.id.fragment_file_browser_noItemGroup);
        txtNotFoundSubTitle=view.findViewById(R.id.fragment_file_browser_txtNoItemFoundSubTitle);
    }

    public void removeAllSelection() {
        mFileBrowserAdapter.removeAllSelection();
    }

    public FileBrowserAdapter getFileBrowserAdapter() {
        return mFileBrowserAdapter;
    }
}
