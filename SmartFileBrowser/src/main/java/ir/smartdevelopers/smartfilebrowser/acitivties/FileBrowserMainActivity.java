package ir.smartdevelopers.smartfilebrowser.acitivties;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.Group;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.app.SharedElementCallback;
import androidx.core.content.FileProvider;
import androidx.core.view.ViewCompat;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import ir.smartdevelopers.smartfilebrowser.R;
import ir.smartdevelopers.smartfilebrowser.adapters.AlbumAdapter;
import ir.smartdevelopers.smartfilebrowser.adapters.FileBrowserAdapter;
import ir.smartdevelopers.smartfilebrowser.adapters.GalleryAdapter;
import ir.smartdevelopers.smartfilebrowser.customClasses.GalleyItemDecoration;
import ir.smartdevelopers.smartfilebrowser.customClasses.MyBehavior;
import ir.smartdevelopers.smartfilebrowser.customClasses.OnItemLongClickListener;
import ir.smartdevelopers.smartfilebrowser.customClasses.OnSearchListener;
import ir.smartdevelopers.smartfilebrowser.customClasses.ResultListener;
import ir.smartdevelopers.smartfilebrowser.models.FileModel;
import ir.smartdevelopers.smartfilebrowser.customClasses.FileUtil;
import ir.smartdevelopers.smartfilebrowser.customClasses.OnItemChooseListener;
import ir.smartdevelopers.smartfilebrowser.customClasses.OnItemClickListener;
import ir.smartdevelopers.smartfilebrowser.customClasses.OnItemSelectListener;
import ir.smartdevelopers.smartfilebrowser.customClasses.RoundViewGroup;
import ir.smartdevelopers.smartfilebrowser.customClasses.SFBFileFilter;
import ir.smartdevelopers.smartfilebrowser.customClasses.SearchView;
import ir.smartdevelopers.smartfilebrowser.models.AlbumModel;
import ir.smartdevelopers.smartfilebrowser.models.FileBrowserModel;
import ir.smartdevelopers.smartfilebrowser.models.GalleryModel;
import ir.smartdevelopers.smartfilebrowser.viewModel.FilesViewModel;
import ir.smartdevelopers.smartfilebrowser.viewModel.GalleryViewModel;
import ir.smartdevelopers.smartfilebrowser.viewModel.SelectionFileViewModel;

public class FileBrowserMainActivity extends AppCompatActivity {

    public static final String EXTRA_RESULT = "file_browser_result";
    public static final String EXTRA_RESULT_URIS = "file_browser_result_uris";
    private static final int REQ_CODE_EDIT_IMAGE = 258;
    public static final int REQ_CODE_TACK_PICTURE = 303;
    public static final int REQ_CODE_PICK_BY_GALLEY = 305;
    private static final int REQ_CODE_SYSTEM_FILE_BROWSER = 6354;
    private AppBarLayout mAppBarLayout;
    private RoundViewGroup mBottomSheetRoot;
    private MyBehavior<View> mBottomSheetBehavior;
    private AHBottomNavigation mBottomNavigationView;
    private View mMainRootView;
    private int mActionBarSize;
    private float mRadius;
    private View mDraggingLineView;
    private FileFilter mFileFilter;
    private AHBottomNavigation.OnTabSelectedListener mOnMenuItemSelectionListener;
    private View mSelectionContainer;
    private TextView txtSelectionCount;
    private ImageView btnSelectionOk;
    private OnItemChooseListener mOnItemChooseListener;
    private SearchView.OnVisibilityChangeListener mOnVisibilityChangeListener;
    private SelectionFileViewModel mSelectionFileViewModel;
    private PageType mPageType;
    private GalleryViewModel mGalleryViewModel;
    /*toolbars*/
    private View mGalleyToolbar, mFileBrowserToolbar;
    private FrameLayout mToolbarPlaceHolder;
    private FrameLayout mAlbumPlaceHolder;
    private View mAlbumListView;
    private boolean mAlbumListIsShowing = false;
    private ImageView btnBack;
    private boolean mSearchViewIsShown = false;
    private String mSearchViewQuery = "";
    /*Builder parameters*/
    private FileFilter mFileTabFileFilter;
    private boolean mShowVideosInGallery = true;
    private boolean mShowCamera = true;
    private boolean mCanSelectMultipleInGallery = true;
    private boolean mCanSelectMultipleInFiles = true;
    private boolean mShowPDFTab = true;
    private boolean mShowFilesTab = true;
    private boolean mShowAudioTab = true;
    private boolean mShowGalleryTab = true;
    private boolean mShowPickFromSystemGalleryMenuButton = true;
    private String mEditedImagePath;
    private int mEditedImagePosition;
    private ResultListener mResultListener;
    private OnItemSelectListener<FileModel> mOnFileItemSelectListener;
    //<editor-fold desc="FileBrowser parameters">
    private View mFileBrowserContainer;
    private RecyclerView mFileBrowserRecyclerView;
    private LinearLayoutManager mFileBrowserLayoutManager;
    private FileBrowserAdapter mFileBrowserAdapter;
    private FilesViewModel mFilesViewModel;
    private OnItemClickListener<FileBrowserModel> mOnFileBrowserItemClickListener;
    private OnSearchListener mOnFileSearchListener;
    private Group mFileBrowserNoItemGroup;
    private TextView txtFileBrowserNotFoundSubTitle;
    private String mFileBrowserSearchPendingQuery = "";
    //</editor-fold>
    //<editor-fold desc="Gallery parameters">
    private RecyclerView mGalleryRecyclerView;
    private GalleryAdapter mGalleryAdapter;
    private GridLayoutManager mGalleryLayoutManager;
    /**
     * this listener is user listener
     */
    private OnItemClickListener<GalleryModel> mOnGalleryItemClickListener;
    /**
     * this listener is for handling camera click or item click internally
     */
    private OnItemClickListener<GalleryModel> mGalleryModelItemClickListener;
    private OnItemLongClickListener<GalleryModel> mOnGalleryItemLongClickListener;
    private OnItemClickListener<GalleryModel> mOnZoomOutClickListener;
    private String tackingPictureFilePath;
    private View mSystemBrowserButtonView;
    //</editor-fold>

    private void getDataFromIntent() {
        mShowVideosInGallery = getIntent().getBooleanExtra("mShowVideosInGallery", true);
        mShowCamera = getIntent().getBooleanExtra("mShowCamera", true);
        mCanSelectMultipleInGallery = getIntent().getBooleanExtra("mCanSelectMultipleInGallery", true);
        mCanSelectMultipleInFiles = getIntent().getBooleanExtra("mCanSelectMultipleInFiles", true);

        mShowFilesTab = getIntent().getBooleanExtra("mShowFilesTab", true);
        mShowGalleryTab = getIntent().getBooleanExtra("mShowGalleryTab", true);

        mShowPDFTab = getIntent().getBooleanExtra("mShowPDFTab", true);
        mShowAudioTab = getIntent().getBooleanExtra("mShowAudioTab", true);


        mShowPickFromSystemGalleryMenuButton = getIntent().getBooleanExtra("mShowPickFromSystemGalleryMenuButton", true);
        SFBFileFilter sfbFileFilter = (SFBFileFilter) getIntent().getSerializableExtra("mFileTabFileFilter");
        if (sfbFileFilter != null) {
            mFileTabFileFilter = getFileFilterFromSfbFileFilter(sfbFileFilter);
        }
    }

    private FileFilter getFileFilterFromSfbFileFilter(SFBFileFilter sfbFileFilter) {
        return new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname == null) {
                    return false;
                }
                if (pathname.isDirectory()) {
                    return sfbFileFilter.isFolder();
                } else {
                    if (pathname.isFile()) {
                        if (sfbFileFilter.getExtensionList().isEmpty()) {
                            return sfbFileFilter.isFile();
                        } else {
                            if (sfbFileFilter.isFile()) {
                                return sfbFileFilter.getExtensionList()
                                        .contains(FileUtil.getFileExtensionFromPath(pathname.getPath()));
                            }
                        }
                    }
                }
                return false;
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);

            getWindow().setAllowEnterTransitionOverlap(false);
            Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.iten_transition_in);
            getWindow().setSharedElementExitTransition(transition);
            setExitSharedElementCallback(new SharedElementCallback() {
                @Override
                public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                    for (View view : sharedElements.values()) {
                        if (view instanceof ImageView) {
                            if (mResultListener.getSavedBitmap() != null) {
                                ((ImageView) view).setImageBitmap(mResultListener.getSavedBitmap().copy(Bitmap.Config.ARGB_8888, true));
                                mResultListener.setSavedBitmap(null);
                            }
                        }
                    }
                    ActivityCompat.startPostponedEnterTransition(FileBrowserMainActivity.this);
                }
            });
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_browser_main);
        mSelectionFileViewModel = new ViewModelProvider(this).get(SelectionFileViewModel.class);
        mGalleryViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication()))
                .get(GalleryViewModel.class);
        mFilesViewModel = new ViewModelProvider(this,
                new ViewModelProvider.AndroidViewModelFactory(getApplication()))
                .get(FilesViewModel.class);
        mResultListener = ResultListener.getInstance();
        if (savedInstanceState == null) {
            if (mShowGalleryTab) {
                mPageType = PageType.TYPE_GALLERY;
            } else if (mShowPDFTab) {
                mPageType = PageType.TYPE_PDF;
            } else if (mShowAudioTab) {
                mPageType = PageType.TYPE_AUDIO;
            } else if (mShowFilesTab) {
                mPageType = PageType.TYPE_FILE_BROWSER;
            } else {
                finish();
            }

        } else {
            mPageType = (PageType) savedInstanceState.getSerializable("page_type");

        }
        findViews();
        getDataFromIntent();
        initListeners();
        initViews(savedInstanceState);

        // <editor-fold  desc=" FileBrowser adapter init">
        if (mShowAudioTab || mShowFilesTab || mShowPDFTab) {
            mFileBrowserLayoutManager = new LinearLayoutManager(this);
            mFileBrowserAdapter = new FileBrowserAdapter(mSelectionFileViewModel.getSelectedFiles());
            mFileBrowserAdapter.setOnItemClickListener(mOnFileBrowserItemClickListener);
            mFileBrowserAdapter.setOnItemSelectListener(mOnFileItemSelectListener);
            mFileBrowserAdapter.setOnItemChooseListener(mOnItemChooseListener);
            mFileBrowserAdapter.setOnSearchListener(mOnFileSearchListener);
            mFileBrowserAdapter.setCanSelectMultiple(mCanSelectMultipleInFiles);
            mFileBrowserRecyclerView.setLayoutManager(mFileBrowserLayoutManager);
            mFileBrowserRecyclerView.setAdapter(mFileBrowserAdapter);

            mFilesViewModel.getFilesLiveData().observe(this, new Observer<List<FileBrowserModel>>() {
                @Override
                public void onChanged(List<FileBrowserModel> fileBrowserModels) {
                    if (fileBrowserModels != null && mFileBrowserAdapter != null) {
                        mFileBrowserAdapter.setList(fileBrowserModels);
                        if (!TextUtils.isEmpty(mFileBrowserSearchPendingQuery)) {
                            mFileBrowserAdapter.getFilter().filter(mFileBrowserSearchPendingQuery);
                            mFileBrowserSearchPendingQuery = "";
                        }
                    }
                }
            });
        }
        // </editor-fold>
        // <editor-fold  desc=" FileBrowser adapter init">
        if (mShowGalleryTab) {
            int spanCount = getResources().getInteger(R.integer.sfb_gallery_grid);
            int gapSpace = getResources().getDimensionPixelSize(R.dimen.sfb_gallery_gap_size);

            mGalleryLayoutManager = new GridLayoutManager(this, spanCount);
            mGalleryAdapter = new GalleryAdapter(mSelectionFileViewModel.getSelectedFiles());
            mGalleryAdapter.setCanSelectMultiple(mCanSelectMultipleInGallery);
            mGalleryAdapter.setOnItemClickListener(mGalleryModelItemClickListener);
            mGalleryAdapter.setOnItemSelectListener(mOnFileItemSelectListener);
            mGalleryAdapter.setOnItemLongClickListener(mOnGalleryItemLongClickListener);
            mGalleryAdapter.setOnZoomOutClickListener(mOnZoomOutClickListener);
            mGalleryAdapter.setOnItemChooseListener(mOnItemChooseListener);
            int imageHeight = (getResources().getDisplayMetrics().widthPixels / spanCount) - (gapSpace * 2);
            mGalleryRecyclerView.setItemViewCacheSize(20);
            mGalleryRecyclerView.setHasFixedSize(true);
            mGalleryLayoutManager.setItemPrefetchEnabled(true);
            mGalleryLayoutManager.setInitialPrefetchItemCount(15);
            mGalleryRecyclerView.setLayoutManager(mGalleryLayoutManager);
            mGalleryRecyclerView.addItemDecoration(new GalleyItemDecoration(spanCount, gapSpace, true));
            mGalleryRecyclerView.setAdapter(mGalleryAdapter);

            LiveData<List<GalleryModel>> allGalleryModels = mGalleryViewModel.getGalleryModelsLiveData();
            allGalleryModels.observe(this, new Observer<List<GalleryModel>>() {
                @Override
                public void onChanged(List<GalleryModel> galleryModels) {

                    if (galleryModels != null) {
                        mGalleryAdapter.setList(galleryModels);
                    }
                }
            });
        }
        // </editor-fold>

        if (savedInstanceState != null) {
            showSuitableFragment(mPageType, true, false);
            int bottomSheetState = savedInstanceState.getInt("bottomSheet_state");
            mBottomSheetBehavior.setState(bottomSheetState);
            if (bottomSheetState == BottomSheetBehavior.STATE_EXPANDED) {
                mSearchViewQuery = savedInstanceState.getString("mSearchViewQuery");
                mSearchViewIsShown = savedInstanceState.getBoolean("mSearchViewIsShown");
                showSuitableToolbar(mPageType);
                mAppBarLayout.setTranslationY(0);
                float bottomNavTranslationY = savedInstanceState.getFloat("bottom_navigation_translation");
                mBottomNavigationView.setTranslationY(bottomNavTranslationY);
                mSelectionContainer.setTranslationY(bottomNavTranslationY);
            }
            tackingPictureFilePath = savedInstanceState.getString("tackingPictureFilePath");
            boolean albumListIsVisible = savedInstanceState.getBoolean("album_list_visibility");
            if (albumListIsVisible) {
                showAlbumsList(false);
            }
        } else {
            showSuitableFragment(mPageType, true, false);
        }

        mResultListener.registerResultListener(new ResultListener.OnResult() {
            @Override
            public void onResultSet(String path) {
                if (!TextUtils.isEmpty(path)) {
                    imageUpdated(path, mEditedImagePosition);
                }
            }
        });
        ActivityCompat.postponeEnterTransition(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mCallbackViewModel.clearCallback();
        mResultListener.clear();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("album_list_visibility", mAlbumListIsShowing);
        outState.putSerializable("page_type", mPageType);
        outState.putInt("bottomSheet_state", mBottomSheetBehavior.getState());
        outState.putFloat("bottom_navigation_translation", mBottomNavigationView.getTranslationY());
        SearchView searchView = findViewById(R.id.fileBrowser_activity_main_searchView);
        if (searchView != null) {
            outState.putBoolean("mSearchViewIsShown", searchView.isShowing());
            if (searchView.isShown()) {
                outState.putString("mSearchViewQuery", searchView.getQuery());
            }
        }
        outState.putString("tackingPictureFilePath", tackingPictureFilePath);
        AppCompatTextView spnSelectAlbum = findViewById(R.id.fileBrowser_activity_main_spnSelectAlbum);
        if (spnSelectAlbum != null) {
            int[] location = new int[2];
            spnSelectAlbum.getLocationInWindow(location);
            int x = location[0];
            int y = location[1];
            outState.putInt("album_list_window_x", x);
            outState.putInt("album_list_window_y", y);
        }
    }

    private void findViews() {
        mAppBarLayout = findViewById(R.id.fileBrowser_activity_main_appbar);
        mBottomSheetRoot = findViewById(R.id.fileBrowser_activity_main_contentRootLayout);
        mBottomNavigationView = findViewById(R.id.fileBrowser_activity_main_bottomNavigation);
        mDraggingLineView = findViewById(R.id.fileBrowser_activity_main_draggingLineView);
        mSelectionContainer = findViewById(R.id.fileBrowser_activity_main_selectionContainer);
        txtSelectionCount = findViewById(R.id.fileBrowser_activity_main_txtSelectionCount);
        btnSelectionOk = findViewById(R.id.fileBrowser_activity_main_imgOk);
        mToolbarPlaceHolder = findViewById(R.id.fileBrowser_activity_main_toolbarPlaceHolder);
        mAlbumPlaceHolder = findViewById(R.id.fileBrowser_activity_main_albumPlaceHolder);
        btnBack = findViewById(R.id.fileBrowser_activity_main_btnBack);
        mMainRootView = findViewById(R.id.fileBrowser_activity_main_windowRoot);
        mFileBrowserRecyclerView = findViewById(R.id.fragment_file_browser_recyclerView);
        mFileBrowserContainer = findViewById(R.id.fileBrowser_activity_main_fileBrowserContainer);
//        mGalleryContainer=findViewById(R.id.fileBrowser_activity_main_galleryContainer);
        mGalleryRecyclerView = findViewById(R.id.sfb_fragment_gallery_recyclerView);
        mFileBrowserNoItemGroup = findViewById(R.id.fragment_file_browser_noItemGroup);
        txtFileBrowserNotFoundSubTitle = findViewById(R.id.fragment_file_browser_txtNoItemFoundSubTitle);
    }

    private void initListeners() {
        mFileFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() || pathname.isDirectory();
//                return FileUtil.getFileExtensionFromPath(pathname.getPath()).equals("mp3") || pathname.isDirectory();
            }
        };

        mOnMenuItemSelectionListener = new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                String title = mBottomNavigationView.getItem(position).getTitle(getApplicationContext());

                PageType pageType = null;
                if (title.equals(getString(R.string.sfb_gallery))) {

                    pageType = PageType.TYPE_GALLERY;

                } else if (title.equals(getString(R.string.sfb_file))) {
                    pageType = PageType.TYPE_FILE_BROWSER;
                } else if (title.equals(getString(R.string.sfb_audio))) {

                    pageType = PageType.TYPE_AUDIO;
                } else if (title.equals(getString(R.string.sfb_PDF))) {

                    pageType = PageType.TYPE_PDF;
                }
                if (mPageType != null && mPageType == pageType) {
                    return false;
                }
                mPageType = pageType;
                showSuitableFragment(mPageType, false, true);
                return true;
            }
        };
        mOnFileItemSelectListener = new OnItemSelectListener<FileModel>() {
            @Override
            public void onItemSelected(FileModel model, int position, int selectionCount) {
                if (model instanceof FileBrowserModel) {
                    if (selectionCount > 0) {
                        showSelectionOkButton();
                        changeSelectionCount(selectionCount);
                        setGalleryEnabled(false);
                    } else {
                        hideSelectionOkButton();
                        setGalleryEnabled(true);
                    }
                }
                if (model instanceof GalleryModel) {
                    if (!mCanSelectMultipleInGallery) {
                        sendBackResult(model);
                        return;
                    }
                    if (selectionCount > 0) {
                        showSelectionOkButton();
                        changeSelectionCount(selectionCount);
                        setFileBrowserEnabled(false);
                    } else {
                        hideSelectionOkButton();
                        setFileBrowserEnabled(true);
                    }
                }
                mSelectionFileViewModel.onItemSelected(selectionCount, selectionCount > 0);
            }
        };
        mOnItemChooseListener = new OnItemChooseListener() {
            @Override
            public void onChoose(FileModel fileModel) {
                sendBackResult(fileModel);
            }
        };

        mOnGalleryItemClickListener = new OnItemClickListener<GalleryModel>() {
            @Override
            public void onItemClicked(GalleryModel model, View view, int position) {
                if (!mCanSelectMultipleInGallery) {
                    sendBackResult(model);
                } else {
                    if (model.getType() == FileUtil.TYPE_IMAGE) {
                        openImageEditor(model, view, position);
                    }
                }
            }
        };
        mOnZoomOutClickListener = new OnItemClickListener<GalleryModel>() {
            @Override
            public void onItemClicked(GalleryModel galleryModel, View view, int position) {
                if (galleryModel.getType() == FileUtil.TYPE_IMAGE) {
                    openImageEditor(galleryModel, view, position);
                } else if (galleryModel.getType() == FileUtil.TYPE_VIDEO) {
                    openVideoActivity(galleryModel, view, position);
                }

            }
        };
        mOnVisibilityChangeListener = new SearchView.OnVisibilityChangeListener() {
            @Override
            public void onVisibilityChanged(boolean isShowing) {
                if (isShowing) {
                    mBottomSheetBehavior.setHideable(false);
                    mBottomSheetBehavior.setDraggable(false);
                } else {
                    mBottomSheetBehavior.setHideable(true);
                    mBottomSheetBehavior.setDraggable(true);
                }
            }
        };
        mOnFileBrowserItemClickListener = new OnItemClickListener<FileBrowserModel>() {
            @Override
            public void onItemClicked(FileBrowserModel model, View view, int position) {
                if (model.getCurrentFile() != null) {
                    if (model.getCurrentFile().isDirectory()) {
                        if (isAndroid30AndAbove()){
                            if (model.getId() == FileBrowserModel.ID_EXTERNAL_STORAGE || model.getId() == FileBrowserModel.ID_INTERNAL_STORAGE){
                                String[] mimeTypes;
                                switch (mPageType){
                                    case TYPE_PDF:
                                        mimeTypes=new String[]{"application/pdf"};
                                        break;
                                    case TYPE_AUDIO:
                                        mimeTypes=new String[]{"audio/*"};
                                        break;
                                    case TYPE_VIDEO:
                                        mimeTypes=new String[]{"video/*"};
                                        break;
                                    case TYPE_FILE_BROWSER:
                                    default:
                                        mimeTypes=new String[]{"*/*"};

                                }
                                openSystemFileBrowser(mimeTypes);
                            }else {
                                mFilesViewModel.getFilesList(model, mFileFilter);
                            }
                        }else {
                            mFilesViewModel.getFilesList(model, mFileFilter);
                        }
                    }

                } else {
                    getFirstPageList();
                }
            }
        };
        mOnFileSearchListener = new OnSearchListener() {
            @Override
            public void onSearch(int count, String searchedText) {
                if (count == 0) {
                    showNoItem(searchedText);
                } else {
                    hideNoItem();
                }
            }
        };
        mGalleryModelItemClickListener = new OnItemClickListener<GalleryModel>() {
            @Override
            public void onItemClicked(GalleryModel model, View view, int position) {
                if (model.getType() == GalleryModel.TYPE_CAMERA) {
                    openCamera();
                } else {
                    if (mOnGalleryItemClickListener != null) {
                        mOnGalleryItemClickListener.onItemClicked(model, view, position);
                    }

                }
            }
        };
    }

    private void openVideoActivity(GalleryModel model, View view, int position) {
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".sfb_provider", model.getCurrentFile());
        Intent videoActivityIntent = new Intent(this, VideoViewActivity.class);
        videoActivityIntent.setData(uri);
        Bundle options = null;
        if (view != null) {
            String sharedName = ViewCompat.getTransitionName(view);
            if (sharedName == null) {
                sharedName = "T_N_" + position;
            }
            options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, sharedName).toBundle();
            videoActivityIntent.putExtra(VideoViewActivity.KEY_TRANSITION_NAME, sharedName);
        }
        ActivityCompat.startActivity(this, videoActivityIntent, options);
    }

    private void openImageEditor(GalleryModel model, View view, int position) {

        mEditedImagePath = FileUtil.getImageTempFile(getApplicationContext()).getPath();
        mEditedImagePosition = position;

        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".sfb_provider", model.getCurrentFile());
        Intent editorIntent = new Intent(this, PhotoEditorActivity.class);
        editorIntent.setData(uri);

        editorIntent.putExtra(PhotoEditorActivity.KEY_SAVE_PATH, mEditedImagePath);
        Bundle options = null;
        if (view != null) {
            String sharedName = ViewCompat.getTransitionName(view);
            if (sharedName == null) {
                sharedName = "T_N_" + position;
            }
            options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, sharedName).toBundle();
            editorIntent.putExtra(PhotoEditorActivity.KEY_TRANSITION_NAME, sharedName);
        }

        ActivityCompat.startActivityForResult(this, editorIntent, REQ_CODE_EDIT_IMAGE,
                options);


    }


    private boolean mFileBrowserEnabled = true;

    private void setFileBrowserEnabled(boolean enabled) {
        if (mFileBrowserEnabled == enabled) {
            return;
        }
        int menuItemCount = mBottomNavigationView.getItemsCount();
        for (int i = 0; i < menuItemCount; i++) {
            if (mBottomNavigationView.getItem(i).getTitle(getApplicationContext()).equals(getString(R.string.sfb_gallery))) {
                continue;
            }
            if (enabled) {
                mBottomNavigationView.enableItemAtPosition(i);
            } else {
                mBottomNavigationView.disableItemAtPosition(i);
            }

        }
        mFileBrowserEnabled = enabled;
    }


    private void initViews(Bundle savedInstanceState) {

        if (mShowGalleryTab) {
            AHBottomNavigationItem gallery = new AHBottomNavigationItem(R.string.sfb_gallery, R.drawable.sfb_ic_gallery, R.color.sfb_color_gallery);
            mBottomNavigationView.addItem(gallery);
        }
        if (mShowPDFTab) {
            AHBottomNavigationItem pdf = new AHBottomNavigationItem(R.string.sfb_PDF, R.drawable.sfb_ic_pdf, R.color.sfb_color_pdf);
            mBottomNavigationView.addItem(pdf);
        }
        if (mShowAudioTab) {
            AHBottomNavigationItem audio = new AHBottomNavigationItem(R.string.sfb_audio, R.drawable.sfb_ic_square_audio, R.color.sfb_color_audio);
            mBottomNavigationView.addItem(audio);
        }
        if (mShowFilesTab) {
            AHBottomNavigationItem files = new AHBottomNavigationItem(R.string.sfb_file, R.drawable.sfb_ic_file, R.color.sfb_color_file);
            mBottomNavigationView.addItem(files);
        }
        mBottomNavigationView.setOnTabSelectedListener(mOnMenuItemSelectionListener);
        mBottomNavigationView.setColored(true);
        mBottomNavigationView.setUseElevation(true);
        mBottomNavigationView.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);

        int[] res = {android.R.attr.actionBarSize};
        TypedArray typedArray = obtainStyledAttributes(res);
        mActionBarSize = typedArray.getDimensionPixelSize(0, 56) +
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics());
        typedArray.recycle();
        ViewGroup.LayoutParams appBarParams = mAppBarLayout.getLayoutParams();
        appBarParams.height = mActionBarSize;
        mAppBarLayout.setLayoutParams(appBarParams);
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        mRadius = getResources().getDimension(R.dimen.sfb_bottom_sheet_top_radius);
        mAppBarLayout.setTranslationY(-mActionBarSize);
        mBottomSheetBehavior = MyBehavior.from(mBottomSheetRoot);
        mBottomSheetBehavior.setHideable(true);
        mBottomSheetBehavior.setPeekHeight(screenHeight / 2, true);
        mBottomSheetBehavior.addBottomSheetCallback(new MyBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    setResult(RESULT_CANCELED);
                    finish();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                float translation = -mActionBarSize;
                float h = 0.05f;
                if (slideOffset > h && slideOffset <= 1) {
                    // show appbar
                    int currentTranslation = (int) convertOffsetToDimen(h, 1, translation, 0, slideOffset);
                    float appbarAlpha = convertOffsetToDimen(h, 1, 0, 1, slideOffset);
                    float draggingLineAlpha = convertOffsetToDimen(h, 1, 1, 0, slideOffset);
                    mAppBarLayout.setAlpha(appbarAlpha);
                    mDraggingLineView.setAlpha(draggingLineAlpha);
                    mAppBarLayout.setTranslationY(currentTranslation);
                    float radius = convertOffsetToDimen(h, 1, mRadius, 0, slideOffset);
                    mBottomSheetRoot.setRadius(radius, radius, 0, 0);
                    float bottomNavigationTranslation = Math.abs(convertOffsetToDimen(h, 1,
                            0, mBottomNavigationView.getMeasuredHeight(), slideOffset));
                    mBottomNavigationView.setTranslationY(bottomNavigationTranslation);

                    mSelectionContainer.setTranslationY(mBottomNavigationView.getTranslationY());
                } else if (slideOffset <= h && slideOffset >= 0) {
                    mAppBarLayout.setTranslationY(translation);
                } else if (slideOffset < -h) {
                    float bottomNavigationTranslation = Math.abs(convertOffsetToDimen(-h, -1,
                            0, mBottomNavigationView.getMeasuredHeight(), slideOffset));
                    mBottomNavigationView.setTranslationY(bottomNavigationTranslation);
                    mSelectionContainer.setTranslationY(mBottomNavigationView.getTranslationY());

                }
                if (slideOffset > h) {
                    showSuitableToolbar(mPageType);
                }
            }
        });

        mMainRootView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setResult(RESULT_CANCELED);
                finish();
                return true;
            }
        });

        if (mSelectionFileViewModel.getSelectionHelperLiveData().getValue().selectionCount > 0) {
            if (mPageType == PageType.TYPE_GALLERY) {
                mOnFileItemSelectListener.onItemSelected(new GalleryModel(), 0,
                        mSelectionFileViewModel.getSelectionHelperLiveData().getValue().selectionCount);
            } else {
                mOnFileItemSelectListener.onItemSelected(new FileBrowserModel(), 0,
                        mSelectionFileViewModel.getSelectionHelperLiveData().getValue().selectionCount);
            }
        }
        btnBack.setOnClickListener(v -> {
            onBackPressed();
        });
        btnSelectionOk.setOnClickListener(v -> {
            sendBackResult(null);
        });
        if (savedInstanceState == null) {
            mBottomSheetRoot.setTranslationY(screenHeight * 0.6f);
            mBottomNavigationView.setTranslationY(200);
            getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    startFirstAnimation();
                    getWindow().getDecorView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }
        if (isShowingGallery()) {
            mFileBrowserContainer.setVisibility(View.INVISIBLE);
            mGalleryRecyclerView.setVisibility(View.VISIBLE);

        } else {
            mGalleryRecyclerView.setVisibility(View.INVISIBLE);
            mFileBrowserContainer.setVisibility(View.VISIBLE);
        }

        mFileBrowserRecyclerView.setItemAnimator(null);

        try {
            if (mPageType == PageType.TYPE_GALLERY) {
                MyBehavior.from(mBottomSheetRoot).setScrollingView(mGalleryRecyclerView);
            } else {
                MyBehavior.from(mBottomSheetRoot).setScrollingView(mFileBrowserRecyclerView);
            }
        } catch (Exception ignore) {
        }

    }

    private void sendBackResult(FileModel model) {
        Intent result = new Intent();
        File[] resultFiles;
        if (mPageType == PageType.TYPE_GALLERY) {
            if (mCanSelectMultipleInGallery) {
                resultFiles = getSelectedGalleryFiles().toArray(new File[0]);
            } else {
                resultFiles = new File[]{model.getCurrentFile()};
            }
        } else {
            if (mCanSelectMultipleInFiles) {
                resultFiles = mSelectionFileViewModel.getSelectedFiles().toArray(new File[0]);
            } else {
                resultFiles = new File[]{model.getCurrentFile()};
            }
        }
        String[] filesPath = new String[resultFiles.length];
        for (int i = 0; i < resultFiles.length; i++) {
            filesPath[i] = resultFiles[i].getPath();
        }
        Uri[] filesUri = new Uri[resultFiles.length];
        for (int i = 0; i < resultFiles.length; i++) {
            filesUri[i] = FileProvider.getUriForFile(getApplicationContext(),
                    getPackageName() + ".sfb_provider", resultFiles[i]);
        }
        Bundle bundle = new Bundle();
        bundle.putStringArray(EXTRA_RESULT, filesPath);
        bundle.putParcelableArray(EXTRA_RESULT_URIS, filesUri);
        Bundle sfbExtra = getIntent().getBundleExtra("sfb_extra");
        if (sfbExtra != null) {
            bundle.putBundle("sfb_extra", sfbExtra);
        }
        result.putExtras(bundle);

        setResult(RESULT_OK, result);
        finish();
    }

    private void startFirstAnimation() {
        long duration = 300;
        mBottomSheetRoot.animate().setDuration(duration).translationY(0)
                .setInterpolator(new FastOutSlowInInterpolator()).start();
        mBottomNavigationView.animate().setDuration(duration).translationY(0)
                .setInterpolator(new FastOutSlowInInterpolator()).start();
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    private void showSuitableToolbar(PageType type) {
        switch (type) {
            case TYPE_AUDIO:
                showFileBrowserToolbar(getString(R.string.sfb_audio_file_broswer_toolbar_title));

                break;
            case TYPE_PDF:
                showFileBrowserToolbar(getString(R.string.sfb_pdf_file_broswer_toolbar_title));
                break;
            case TYPE_VIDEO:
                showFileBrowserToolbar(getString(R.string.sfb_video_file_broswer_toolbar_title));
                break;
            case TYPE_FILE_BROWSER:
                showFileBrowserToolbar(getString(R.string.sfb_file_broswer_toolbar_title));
                break;
            case TYPE_GALLERY:
                showGalleryToolbar();
                break;
        }
    }


    private boolean isAndroid30AndAbove(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R;
    }

    private boolean isShowingFileBrowser() {
        return mFileBrowserContainer.getVisibility() == View.VISIBLE;
    }

    private boolean isShowingGallery() {
        return mGalleryRecyclerView.getVisibility() == View.VISIBLE;
    }

    private void showSuitableFragment(PageType type, boolean selectItem, boolean animate) {

        switch (type) {
            case TYPE_AUDIO:

                if (selectItem) {
                    mBottomNavigationView.setCurrentItem(getNavigationItemPosition(getString(R.string.sfb_audio)), false);
                }

                showAudioFragment(animate);

                break;
            case TYPE_PDF:
                if (selectItem) {
                    mBottomNavigationView.setCurrentItem(getNavigationItemPosition(getString(R.string.sfb_PDF)), false);
                }

                showPDFFragment(animate);

                break;
            case TYPE_VIDEO:

                break;
            case TYPE_FILE_BROWSER:
                if (selectItem) {
                    mBottomNavigationView.setCurrentItem(getNavigationItemPosition(getString(R.string.sfb_file)), false);
                }

                showFilesFragment(animate);

                break;
            case TYPE_GALLERY:
                if (selectItem) {
                    mBottomNavigationView.setCurrentItem(getNavigationItemPosition(getString(R.string.sfb_gallery)), false);
                }
                showGallery(animate);
                break;
        }
    }

    private int getNavigationItemPosition(String title) {
        int childCount = mBottomNavigationView.getItemsCount();
        for (int i = 0; i < childCount; i++) {
            if (mBottomNavigationView.getItem(i).getTitle(getApplicationContext()).equals(title)) {
                return i;
            }
        }
        return 0;
    }

    private void showFileBrowserToolbar(String toolbarTitle) {

        if (mFileBrowserToolbar == null) {
            mToolbarPlaceHolder.removeAllViews();
            mGalleyToolbar = null;
            mFileBrowserToolbar = LayoutInflater.from(this)
                    .inflate(R.layout.file_browser_toolbar_layout, mToolbarPlaceHolder, true);
            initFileBrowserToolbar();
        }
        TextView txtToolbarTitle = findViewById(R.id.fileBrowser_activity_main_txtToolbarTitle);
        if (txtToolbarTitle != null) {
            txtToolbarTitle.setText(toolbarTitle);
        }

    }

    private void initFileBrowserToolbar() {
        ImageView btnSearch = findViewById(R.id.fileBrowser_activity_main_btnSearch);
        SearchView searchView = findViewById(R.id.fileBrowser_activity_main_searchView);

        btnSearch.setOnClickListener(v -> {
            searchView.show();
        });
        searchView.setOnQueryChangeListener(new SearchView.OnQueryChangeListener() {
            @Override
            public void onQueryChanged(String query) {
                if (isShowingFileBrowser() /*&& !shouldShowSystemFileBrowser()*/) {
                    searchFile(query);
                }
            }
        });
        if (mSearchViewIsShown) {
            searchView.show();
            searchView.setQuery(mSearchViewQuery);
        }
        searchView.setOnVisibilityChangeListener(mOnVisibilityChangeListener);


    }

    private void showGalleryToolbar() {

        if (mGalleyToolbar == null) {
            mToolbarPlaceHolder.removeAllViews();
            mFileBrowserToolbar = null;
            mGalleyToolbar = LayoutInflater.from(this).inflate(R.layout.gallery_toolbar_layout, mToolbarPlaceHolder, true);
            initGalleryToolbarViews();
        }

    }

    private void initGalleryToolbarViews() {
        AppCompatTextView spnSelectAlbum = findViewById(R.id.fileBrowser_activity_main_spnSelectAlbum);
        ImageButton btnVerticalDotsMore = findViewById(R.id.fileBrowser_activity_main_btnGalleryMenu);
        if (!mShowPickFromSystemGalleryMenuButton) {
            btnVerticalDotsMore.setVisibility(View.GONE);
        }
        spnSelectAlbum.setOnClickListener(v -> {
            int[] location = new int[2];
            spnSelectAlbum.getLocationInWindow(location);
            int x = location[0];
            int y = location[1];
            showAlbumsList(true);
        });
        spnSelectAlbum.setText(R.string.sfb_all_media);
        btnVerticalDotsMore.setOnClickListener(v -> {
            openSystemGalleryApp();
        });
    }

    private void openSystemGalleryApp() {
        Intent galleyIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        galleyIntent.setType("image/*");
        galleyIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, mCanSelectMultipleInGallery);
        galleyIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(galleyIntent, REQ_CODE_PICK_BY_GALLEY);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void showAlbumsList(boolean animate) {
        if (mAlbumListView == null) {
            mAlbumListView = LayoutInflater.from(this).inflate(R.layout.sfb_album_recyclerview_layout, mAlbumPlaceHolder, true);

            RecyclerView albumRecyclerView = mAlbumListView.findViewById(R.id.fileBrowser_activity_main_albumRecyclerView);
            AlbumAdapter albumAdapter = new AlbumAdapter();
            albumAdapter.setOnItemClickListener(new OnItemClickListener<AlbumModel>() {
                @Override
                public void onItemClicked(AlbumModel model, View view, int position) {
                    if (isShowingGallery()) {
                        mAlbumListIsShowing = false;
                        updateGallery(model);
                        hideAlbumList(false);
                        AppCompatTextView spnSelectAlbum = findViewById(R.id.fileBrowser_activity_main_spnSelectAlbum);
                        spnSelectAlbum.setText(model.getName());
                    }
                }
            });
            albumRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            albumRecyclerView.setAdapter(albumAdapter);
            LiveData<List<AlbumModel>> allImageAlbumLiveData = mGalleryViewModel.getAllImageAlbums();
            allImageAlbumLiveData.removeObservers(this);
            allImageAlbumLiveData.observe(this, new Observer<List<AlbumModel>>() {
                @Override
                public void onChanged(List<AlbumModel> albumModels) {

                    albumAdapter.setAlbumModels(albumModels);
                }
            });
        }
        mAlbumListIsShowing = true;
        int duration = animate ? 100 : 0;
        FrameLayout albumPlaceHolderRoot = findViewById(R.id.fileBrowser_activity_main_albumPlaceHolderRoot);

        mAlbumPlaceHolder.post(() -> {
            AppCompatTextView spnSelectAlbum = findViewById(R.id.fileBrowser_activity_main_spnSelectAlbum);
            int[] location = new int[2];
            spnSelectAlbum.getLocationInWindow(location);
            int x = location[0];
            int y = location[1];
            mAlbumPlaceHolder.setX(x);
            mAlbumPlaceHolder.setY(y);
            mAlbumPlaceHolder.animate().setDuration(duration).scaleY(1).scaleX(1).alpha(1)
                    .withStartAction(() -> {
                        mAlbumPlaceHolder.setAlpha(0);
                        mAlbumPlaceHolder.setScaleY(0);
                        mAlbumPlaceHolder.setScaleX(0);
                        albumPlaceHolderRoot.setVisibility(View.VISIBLE);
                        mAlbumPlaceHolder.setVisibility(View.VISIBLE);
                    })
                    .start();
        });

        albumPlaceHolderRoot.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideAlbumList(true);
                return true;
            }
        });


    }

    private void hideAlbumList(boolean animate) {
        FrameLayout albumPlaceHolderRoot = findViewById(R.id.fileBrowser_activity_main_albumPlaceHolderRoot);
        mAlbumListIsShowing = false;
        int duration = animate ? 100 : 50;
        ViewPropertyAnimator animator = mAlbumPlaceHolder.animate().setDuration(duration);
        if (animate) {
            animator.scaleY(0.3f).scaleX(0.3f);
        }
        animator.alpha(0)
                .withEndAction(() -> {
                    albumPlaceHolderRoot.setVisibility(View.GONE);
                    mAlbumPlaceHolder.setVisibility(View.INVISIBLE);
                    mAlbumListView = null;
                    mAlbumPlaceHolder.removeAllViews();
                })
                .start();

    }


    private boolean mGalleryEnabled = true;

    private void setGalleryEnabled(boolean enabled) {
        if (mGalleryEnabled == enabled) {
            return;
        }
        int menuCount = mBottomNavigationView.getItemsCount();
        int pos = -1;
        for (int i = 0; i < menuCount; i++) {
            if (mBottomNavigationView.getItem(i).getTitle(getApplicationContext())
                    .equals(getString(R.string.sfb_gallery))) {
                pos = i;
                break;
            }
        }
        if (pos != -1) {
            mGalleryEnabled = enabled;
            if (enabled) {
                mBottomNavigationView.enableItemAtPosition(pos);
            } else {
                mBottomNavigationView.disableItemAtPosition(pos);
            }

        }
    }

    private void changeSelectionCount(int selectionCount) {
        txtSelectionCount.animate().scaleY(1.2f).scaleX(1.2f).setDuration(80)
                .withEndAction(() -> {
                    txtSelectionCount.setText(String.valueOf(selectionCount));
                    txtSelectionCount.animate().scaleY(1f).scaleX(1f).setDuration(80)
                            .setInterpolator(new OvershootInterpolator())
                            .start();
                }).setInterpolator(new OvershootInterpolator())
                .start();
    }

    private void showSelectionOkButton() {
        if (mSelectionContainer.getVisibility() == View.VISIBLE) {
            return;
        }
        mSelectionContainer.post(() -> {
            mSelectionContainer.animate().setDuration(100).scaleX(1).scaleY(1)
                    .withStartAction(() -> {
                        mSelectionContainer.setScaleX(0);
                        mSelectionContainer.setScaleY(0);
                        mSelectionContainer.setVisibility(View.VISIBLE);
                    }).start();
        });
    }

    private void hideSelectionOkButton() {
        if (mSelectionContainer.getVisibility() != View.VISIBLE) {
            return;
        }
        mSelectionContainer.animate().setDuration(100).scaleX(0).scaleY(0)
                .withEndAction(() -> {
                    mSelectionContainer.setVisibility(View.INVISIBLE);
                }).start();
    }

    private void showPDFFragment(boolean animate) {
        if (animate) {
            animateBottomSheet();
            if (isShowingGallery()) {
                swapContainers(mGalleryRecyclerView, mFileBrowserContainer);
            }
        }
        mFileFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory() ||
                        FileUtil.getMimeTypeFromPath(pathname.getPath()).toLowerCase().contains("pdf");
            }
        };

//        initFileBrowserRecyclerView();
        changePages(mPageType);

    }


    private void showAudioFragment(boolean animate) {
        if (animate) {
            animateBottomSheet();
            if (isShowingGallery()) {
                swapContainers(mGalleryRecyclerView, mFileBrowserContainer);
            }
        }
        mFileFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory() ||
                        FileUtil.getMimeTypeFromPath(pathname.getPath()).toLowerCase().contains("audio");
            }
        };
//        initFileBrowserRecyclerView();
        changePages(mPageType);


    }

    private void showFilesFragment(boolean animate) {
        if (animate) {
            animateBottomSheet();
            if (isShowingGallery()) {
                swapContainers(mGalleryRecyclerView, mFileBrowserContainer);
            }
        }
        if (mFileTabFileFilter != null) {
            mFileFilter = mFileTabFileFilter;
        } else {
            mFileFilter = new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory() ||
                            pathname.isFile();
                }
            };
        }
//        initFileBrowserRecyclerView();
        changePages(mPageType);

    }

    private ValueAnimator mContainerAnimator;

    private void swapContainers(View visibleContainer, View hiddenContainer) {
        if (mContainerAnimator != null) {
            mContainerAnimator.cancel();
        }
        hiddenContainer.setAlpha(0);
        hiddenContainer.setVisibility(View.VISIBLE);
        mContainerAnimator = ValueAnimator.ofFloat(0, 1);
        mContainerAnimator.setDuration(100)
                .addUpdateListener((animation -> {
                    float f = (float) animation.getAnimatedValue();
                    visibleContainer.setAlpha(1 - f);
                    hiddenContainer.setAlpha(f);
                }));
        mContainerAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                try {
                    if (hiddenContainer == mGalleryRecyclerView) {
                        MyBehavior.from(mBottomSheetRoot).setScrollingView(mGalleryRecyclerView);
                    } else {
                        MyBehavior.from(mBottomSheetRoot).setScrollingView(mFileBrowserRecyclerView);
                    }
                } catch (Exception ignore) {
                }
                visibleContainer.setVisibility(View.INVISIBLE);
            }
        });
        mContainerAnimator.start();

    }

    private void showGallery(boolean animate) {
        if (animate) {
            animateBottomSheet();
            if (mFileBrowserContainer.getVisibility() == View.VISIBLE) {
                swapContainers(mFileBrowserContainer, mGalleryRecyclerView);
            }
        }
        if (mGalleryAdapter.getItemCount() == 0) {
            mGalleryViewModel
                    .getAllGalleryModels(mShowCamera, mShowVideosInGallery);
        }

    }


    private void animateBottomSheet() {
        mBottomSheetRoot.animate().translationY(100).setInterpolator(new OvershootInterpolator())
                .setDuration(150).withEndAction(() -> {
            mBottomSheetRoot.animate().translationY(0).setInterpolator(new OvershootInterpolator())
                    .setDuration(300).start();
        }).start();
    }


    private float convertOffsetToDimen(float offsetStart, float offsetEnd, float dimenStart, float dimenEnd, float offset) {
        return ((offset * (dimenStart - dimenEnd)) - (offsetEnd * dimenStart) + (offsetStart * dimenEnd)) / (offsetStart - offsetEnd);
    }

    public void scrollGalleryToFirstPos() {
        mGalleryRecyclerView.smoothScrollToPosition(0);
    }
    private boolean canGalleryScrollUp(){
        return mGalleryRecyclerView.canScrollVertically(-1);
    }
    private boolean canFileBrowserScrollUp(){
        return mFileBrowserRecyclerView.canScrollVertically(-1);
    }
    public void scrollFileBrowserToFirstPos() {
        mFileBrowserRecyclerView.smoothScrollToPosition(0);
    }

    //<editor-fold desc="FileBrowser codes">
    public boolean isFileBrowserInSubDirectory() {
        return mFileBrowserAdapter != null && mFileBrowserAdapter.isInSubDirectory();
    }

    public void goBackToFileBrowserParentDirectory() {
        mFileBrowserAdapter.goBackToParentDirectory();
    }

    private void hideNoItem() {
        mFileBrowserNoItemGroup.setVisibility(View.GONE);
        mFileBrowserRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showNoItem(String searchedText) {
        mFileBrowserRecyclerView.setVisibility(View.GONE);
        mFileBrowserNoItemGroup.setVisibility(View.VISIBLE);
        CharSequence text = Html.fromHtml(getString(R.string.sfb_no_results_found_sub_title, searchedText));
        txtFileBrowserNotFoundSubTitle.setText(text);
    }


    public void changePages(FileBrowserMainActivity.PageType pageType) {
        mPageType = pageType;
        getFirstPageList();
    }

    private void getFirstPageList() {
        switch (mPageType) {
            case TYPE_FILE_BROWSER:

                    mFilesViewModel.getFirstPageFilesLiveData(mFileFilter);
                break;
            case TYPE_VIDEO:
                mFilesViewModel.getFirstPageVideosLiveData(mFileFilter);
                break;
            case TYPE_PDF:

                    mFilesViewModel.getFirstPagePdfLiveData(mFileFilter);

                break;
            case TYPE_AUDIO:

                mFilesViewModel.getFirstPageAudiosLiveData(mFileFilter);
                hideSystemFileBrowserPage();

                break;
        }

    }

    private void hideSystemFileBrowserPage() {
        if (mSystemBrowserButtonView != null) {

            mSystemBrowserButtonView.setVisibility(View.GONE);
            mFileBrowserRecyclerView.setVisibility(View.VISIBLE);
        }

    }



    private void openSystemFileBrowser(String[] mimeTypes) {
        Intent systemFileBrowserIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        systemFileBrowserIntent.setType("*/*");
        systemFileBrowserIntent.addCategory(Intent.CATEGORY_OPENABLE);
        systemFileBrowserIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, mCanSelectMultipleInFiles);
        systemFileBrowserIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        systemFileBrowserIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        systemFileBrowserIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(systemFileBrowserIntent, REQ_CODE_SYSTEM_FILE_BROWSER);
    }

    public void removeAllFileBrowserSelection() {
        mFileBrowserAdapter.removeAllSelection();
    }


    public void searchFile(String query) {
        if (mFileBrowserAdapter == null) {
            mFileBrowserSearchPendingQuery = query;
        } else {
            mFileBrowserAdapter.getFilter().filter(query);
        }
    }
    //</editor-fold>


    //<editor-fold desc="Gallery codes">
    private void openCamera() {

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File newPic = null;
        try {
            newPic = FileUtil.getImageFile(getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (newPic != null) {
            tackingPictureFilePath = newPic.getAbsolutePath();
            Uri uri = FileProvider.getUriForFile(getApplicationContext(),
                    getPackageName() + ".provider",
                    newPic);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (cameraIntent.resolveActivity(getPackageManager()) == null) {
                Toast.makeText(this, R.string.this_device_dose_not_have_camera_app, Toast.LENGTH_SHORT).show();
                return;
            }
            List<ResolveInfo> resolveInfos = getPackageManager().queryIntentActivities(cameraIntent,
                    PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo info : resolveInfos) {
                String packageName = info.activityInfo.packageName;
                grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
                        Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }

            startActivityForResult(cameraIntent, REQ_CODE_TACK_PICTURE);
        }
    }

    public void updateGallery(AlbumModel albumModel) {
        if (albumModel.getId() == -1) {
            mGalleryViewModel.getAllGalleryModels(mShowCamera, mShowVideosInGallery);
        } else {
            mGalleryViewModel.getGalleryModelsByAlbumName(albumModel.getName(), mShowVideosInGallery);
        }
    }

    public List<File> getSelectedGalleryFiles() {
        if (mGalleryAdapter == null) {
            return Collections.emptyList();
        }
        return mSelectionFileViewModel.getSelectedFiles();
    }

    public void removeAllGallerySelections() {
        mGalleryAdapter.removeAllSelections();
    }

    public int getGalleryItemSelectionCount() {
        if (mGalleryAdapter != null) {
            return mGalleryAdapter.getSelectionCount();
        }
        return 0;
    }


    public void imageUpdated(String newFilePath, int editedImagePosition) {
        if (mGalleryAdapter != null) {
            Uri updatedFileUri=FileProvider.getUriForFile(getApplicationContext(),
                    getPackageName()+".sfb_provider",new File(newFilePath));
            mGalleryAdapter.updateSelectedFile(newFilePath,editedImagePosition,updatedFileUri);
            if (Build.VERSION.SDK_INT < 21) {
                mGalleryAdapter.notifyItemChanged(editedImagePosition);
            }
        }
    }

    //</editor-fold>
    @Override
    public void finish() {
        try {
            mBottomSheetBehavior.setHideable(true);
            mBottomSheetBehavior.setState(MyBehavior.STATE_HIDDEN);
        } catch (Exception ignore) {
        }
        super.finish();
        overridePendingTransition(R.anim.sfb_not_anim, R.anim.sfb_fade_out);

    }

    @Override
    public void onBackPressed() {

        if (mPageType != PageType.TYPE_GALLERY) {
            SearchView searchView = findViewById(R.id.fileBrowser_activity_main_searchView);
            if (searchView != null && searchView.isShown()) {
                searchView.close(true);
                return;
            }
            if (isShowingFileBrowser() /*&& !shouldShowSystemFileBrowser()*/) {
                if (isFileBrowserInSubDirectory()) {
                    goBackToFileBrowserParentDirectory();
                    return;
                }
            }
        }
        if (mBottomSheetBehavior.getState() == MyBehavior.STATE_EXPANDED) {
            if (isShowingGallery() && canGalleryScrollUp()) {
                scrollGalleryToFirstPos();
                return;
            } else if (isShowingFileBrowser() && canFileBrowserScrollUp()){
                scrollFileBrowserToFirstPos();
                return;
            }
            mBottomSheetBehavior.setState(MyBehavior.STATE_HALF_EXPANDED);
            return;
        }
        //noinspection ConstantConditions
        if (mSelectionFileViewModel.getSelectionHelperLiveData().getValue().selectionCount > 0) {

            mSelectionFileViewModel.removeAllSelections();
            if (isShowingFileBrowser()) {
                mOnFileItemSelectListener.onItemSelected(new FileBrowserModel(), 0, 0);
                removeAllFileBrowserSelection();
            }

            return;
        }

        if (isShowingGallery()) {
            if (getGalleryItemSelectionCount() > 0) {

                mOnFileItemSelectListener.onItemSelected(new GalleryModel(), 0, 0);
                removeAllGallerySelections();
                return;
            }
        }
        if (!TextUtils.isEmpty(mEditedImagePath)) {
            File editedImageTempFile = new File(mEditedImagePath);
            if (editedImageTempFile.exists()) {
                editedImageTempFile.delete();
            }
        }
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }


    public FileFilter getFileFilter() {
        return mFileFilter;
    }

    public void setFileFilter(FileFilter fileFilter) {
        mFileFilter = fileFilter;
    }

    public PageType getPageType() {
        return mPageType;
    }

    public OnItemSelectListener<FileModel> getOnFileItemSelectListener() {
        return mOnFileItemSelectListener;
    }

    public OnItemClickListener<GalleryModel> getOnGalleryItemClickListener() {
        return mOnGalleryItemClickListener;
    }

    public OnItemChooseListener getOnItemChooseListener() {
        return mOnItemChooseListener;
    }

    public OnItemLongClickListener<GalleryModel> getOnGalleryItemLongClickListener() {
        return mOnGalleryItemLongClickListener;
    }

    public OnItemClickListener<GalleryModel> getOnZoomOutClickListener() {
        return mOnZoomOutClickListener;
    }

    public enum PageType {
        TYPE_GALLERY, TYPE_FILE_BROWSER, TYPE_PDF, TYPE_AUDIO, TYPE_VIDEO
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_TACK_PICTURE) {
            if (resultCode == Activity.RESULT_OK) {
                File takenPic = new File(tackingPictureFilePath);
                if (takenPic.isFile()) {
                    FileUtil.scanMediaFile(this, takenPic);
                    GalleryModel newPicModel = new GalleryModel();
                    newPicModel.setType(FileUtil.TYPE_IMAGE);
                    newPicModel.setSelected(true);
                    newPicModel.setName(takenPic.getName());
                    newPicModel.setPath(takenPic.getPath());
                    newPicModel.setUri(FileProvider.getUriForFile(getApplicationContext(),
                            getPackageName()+".sfb_provider",takenPic));
                    mGalleryAdapter.addNewPic(newPicModel);

                }
            }
        } else if (requestCode == REQ_CODE_PICK_BY_GALLEY) {
            if (resultCode == RESULT_OK && data != null) {
                List<File> selectedFiles= getSelectedGalleryFiles();
                Bundle bundle = new Bundle();
                bundle.putParcelableArray(EXTRA_RESULT_URIS, getSelectedFilesUriFromIntent(selectedFiles,data));
                Intent resultIntent = new Intent();
                resultIntent.putExtras(bundle);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        }else if (requestCode == REQ_CODE_SYSTEM_FILE_BROWSER){
            if (resultCode == RESULT_OK && data !=null){

                List<File> selectedFiles= mSelectionFileViewModel.getSelectedFiles();

                Bundle bundle = new Bundle();
                bundle.putParcelableArray(EXTRA_RESULT_URIS, getSelectedFilesUriFromIntent(selectedFiles,data));
                Intent resultIntent = new Intent();
                resultIntent.putExtras(bundle);
                setResult(RESULT_OK, resultIntent);
                finish();

            }
        }
    }
    private Uri[] getSelectedFilesUriFromIntent(List<File> selectedFiles,Intent data){
        ClipData clipData= data.getClipData();
        List<Uri> uris=new ArrayList<>();
        if (selectedFiles!=null){
            for (File file:selectedFiles){
                uris.add(FileProvider.getUriForFile(getApplicationContext(),
                        getPackageName()+".sfb_provider",file));
            }
        }
        if (clipData!=null){
            int count=clipData.getItemCount();
            for (int i=0;i<count;i++){
                Uri uri=clipData.getItemAt(i).getUri();
                try {
                    getContentResolver().takePersistableUriPermission(uri,Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }catch (Exception e){
                    Log.e("TTT",e.getMessage(),e);
                }
                uris.add(uri);
            }
        }else {
            Uri uri=data.getData();
            if (uri!=null){
                try {
                    getContentResolver().takePersistableUriPermission(uri,Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    getContentResolver().takePersistableUriPermission(uri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }catch (Exception e){
                    Log.e("TTT",e.getMessage(),e);
                }
                uris.add(uri);
            }
        }
        return uris.toArray(new Uri[0]);
    }
}