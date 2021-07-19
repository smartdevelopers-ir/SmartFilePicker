package ir.smartdevelopers.smartfilebrowser.acitivties;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.io.FileFilter;
import java.util.List;

import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity;
import iamutkarshtiwari.github.io.ananas.editimage.ImageEditorIntentBuilder;
import ir.smartdevelopers.smartfilebrowser.R;
import ir.smartdevelopers.smartfilebrowser.adapters.AlbumAdapter;
import ir.smartdevelopers.smartfilebrowser.customClasses.OnItemLongClickListener;
import ir.smartdevelopers.smartfilebrowser.models.FileModel;
import ir.smartdevelopers.smartfilebrowser.customClasses.FileUtil;
import ir.smartdevelopers.smartfilebrowser.customClasses.OnItemChooseListener;
import ir.smartdevelopers.smartfilebrowser.customClasses.OnItemClickListener;
import ir.smartdevelopers.smartfilebrowser.customClasses.OnItemSelectListener;
import ir.smartdevelopers.smartfilebrowser.customClasses.RoundLinearLayout;
import ir.smartdevelopers.smartfilebrowser.customClasses.SFBFileFilter;
import ir.smartdevelopers.smartfilebrowser.customClasses.SearchView;
import ir.smartdevelopers.smartfilebrowser.fragments.FileBrowserFragment;
import ir.smartdevelopers.smartfilebrowser.fragments.GalleryFragment;
import ir.smartdevelopers.smartfilebrowser.models.AlbumModel;
import ir.smartdevelopers.smartfilebrowser.models.FileBrowserModel;
import ir.smartdevelopers.smartfilebrowser.models.GalleryModel;
import ir.smartdevelopers.smartfilebrowser.viewModel.GalleryViewModel;
import ir.smartdevelopers.smartfilebrowser.viewModel.SelectionFileViewModel;

public class FileBrowserMainActivity extends AppCompatActivity {

    public static final String EXTRA_RESULT = "file_browser_result";
    private static final int REQ_CODE_EDIT_IMAGE = 258;
    private GalleryFragment mGalleryFragment;
    private FileBrowserFragment mFileBrowserFragment;
    private AppBarLayout mAppBarLayout;
    private RoundLinearLayout mBottomSheetRoot;
    private BottomSheetBehavior<View> mBottomSheetBehavior;
    private AHBottomNavigation mBottomNavigationView;
    //    private BottomNavigationBar mBottomNavigationView;
//    private BottomNavigationView mBottomNavigationView;
    private View mMainRootView;
    private int mActionBarSize;
    private float mRadius;
    private View mDraggingLineView;
    private FileFilter mFileFilter;
    //    private BottomNavigation.OnMenuItemSelectionListener mOnMenuItemSelectionListener;
    private AHBottomNavigation.OnTabSelectedListener mOnMenuItemSelectionListener;
    private View mSelectionContainer;
    private TextView txtSelectionCount;
    private ImageView btnSelectionOk;
    private OnItemSelectListener<FileModel> mOnFileItemSelectListener;
    private OnItemChooseListener mOnItemChooseListener;
    //    private OnItemSelectListener<GalleryModel> mOnGalleryItemSelectListener;
    private OnItemClickListener<GalleryModel> mOnGalleryItemClickListener;
    private OnItemLongClickListener<GalleryModel> mOnGalleryItemLongClickListener;

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
    private String mEditedImagePath;
    private int mEditedImagePosition;

    private void getDataFromIntent() {
        mShowVideosInGallery = getIntent().getBooleanExtra("mShowVideosInGallery", true);
        mShowCamera = getIntent().getBooleanExtra("mShowCamera", true);
        mCanSelectMultipleInGallery = getIntent().getBooleanExtra("mCanSelectMultipleInGallery", true);
        mCanSelectMultipleInFiles = getIntent().getBooleanExtra("mCanSelectMultipleInFiles", true);
        mShowPDFTab = getIntent().getBooleanExtra("mShowPDFTab", true);
        mShowFilesTab = getIntent().getBooleanExtra("mShowFilesTab", true);
        mShowAudioTab = getIntent().getBooleanExtra("mShowAudioTab", true);
        mShowGalleryTab = getIntent().getBooleanExtra("mShowGalleryTab", true);
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

    //    private BottomNavigationBar.OnTabSelectedListener mOnTabSelectedListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_browser_main);
        mSelectionFileViewModel = new ViewModelProvider(this).get(SelectionFileViewModel.class);
        mGalleryViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication()))
                .get(GalleryViewModel.class);
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



        if (savedInstanceState != null) {
            showSuitableFragment(mPageType, true, false, false);
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
            boolean albumListIsVisible = savedInstanceState.getBoolean("album_list_visibility");
            if (albumListIsVisible) {
                showAlbumsList(false);
            }
        }else {
            showSuitableFragment(mPageType, true, false, true);
        }
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

                if (title.equals(getString(R.string.sfb_gallery))) {

                    mPageType = PageType.TYPE_GALLERY;

                } else if (title.equals(getString(R.string.sfb_file))) {
                    mPageType = PageType.TYPE_FILE_BROWSER;
                } else if (title.equals(getString(R.string.sfb_audio))) {

                    mPageType = PageType.TYPE_AUDIO;
                } else if (title.equals(getString(R.string.sfb_PDF))) {

                    mPageType = PageType.TYPE_PDF;
                }
                showSuitableFragment(mPageType, false, true, true);
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
                if (model instanceof GalleryModel){
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
            public void onItemClicked(GalleryModel model, int position) {
                if (!mCanSelectMultipleInGallery) {
                    sendBackResult(model);
                } else {
                    if (model.getType()==FileUtil.TYPE_IMAGE){
                        openImageEditor(model,position);
                    }
                }
            }
        };
        mOnGalleryItemLongClickListener=new OnItemLongClickListener<GalleryModel>() {
            @Override
            public void onLongClicked(GalleryModel model, int position) {

                    if (model.getType()==FileUtil.TYPE_IMAGE){
                        openImageEditor(model,position);
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


    }

    private void openImageEditor(GalleryModel model, int position) {
        try {
            mEditedImagePath=FileUtil.getImageTempFile(getApplicationContext()).getPath();
            mEditedImagePosition=position;
            Intent intent = new ImageEditorIntentBuilder(this,
                    model.getPath()
                    ,mEditedImagePath)
                    .withAddText() // Add the features you need
                    .withPaintFeature()

//                            .withFilterFeature()
                    .withRotateFeature()
                    .withCropFeature()
//                            .withBrightnessFeature()
//                            .withSaturationFeature()
//                            .withBeautyFeature()
                    .withStickerFeature()
                      // Add this to force portrait mode (It's set to false by default)
                    .build();

            EditImageActivity.start(this, intent, REQ_CODE_EDIT_IMAGE);
        } catch (Exception e) {
            Log.e("ttt", e.getMessage()); // This could throw if either `sourcePath` or `outputPath` is blank or Null
        }
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

//        mBottomNavigationView.setMenuItemSelectionListener(mOnMenuItemSelectionListener);
        int[] res = {R.attr.actionBarSize};
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
        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheetRoot);
        mBottomSheetBehavior.setHideable(true);
//        mBottomSheetBehavior.setFitToContents(false);
//        mBottomSheetBehavior.setHalfExpandedRatio(0.5f);
        mBottomSheetBehavior.setPeekHeight(screenHeight / 2, true);
        mBottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
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
//                    Log.v("TTT", "currentTranslation=" + currentTranslation);
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
            if (mPageType==PageType.TYPE_GALLERY){
                mOnFileItemSelectListener.onItemSelected(new GalleryModel(), 0,
                        mSelectionFileViewModel.getSelectionHelperLiveData().getValue().selectionCount);
            }else {
                mOnFileItemSelectListener.onItemSelected(new FileBrowserModel(), 0,
                        mSelectionFileViewModel.getSelectionHelperLiveData().getValue().selectionCount);
            }
        }
//        if (savedInstanceState==null){
//            mBottomSheetRoot.post(()->{
//                startFirstAnimation();
//            });
//        }
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
    }

    private void sendBackResult(FileModel model) {
        Intent result = new Intent();
        File[] resultFiles;
        if (mPageType == PageType.TYPE_GALLERY) {
            if (mCanSelectMultipleInGallery) {
                resultFiles = mGalleryFragment.getSelectedFiles().toArray(new File[0]);
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
        String[] filesPath=new String[resultFiles.length];
        for (int i=0;i<resultFiles.length;i++){
            filesPath[i]=resultFiles[i].getPath();
        }
        Bundle bundle=new Bundle();
        bundle.putStringArray(EXTRA_RESULT,filesPath);
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

    private void showSuitableFragment(PageType type, boolean selectItem, boolean animate, boolean create) {
        switch (type) {
            case TYPE_AUDIO:


                if (selectItem) {
                    mBottomNavigationView.setCurrentItem(getNavigationItemPosition(getString(R.string.sfb_audio)), false);
                }
                if (create) {
                    showAudioFragment(animate);
                } else {
                    mFileBrowserFragment = (FileBrowserFragment) getSupportFragmentManager().findFragmentByTag(FileBrowserFragment.FRAGMENT_TAG);
                }

                mGalleryFragment = null;
                break;
            case TYPE_PDF:
                if (selectItem) {
                    mBottomNavigationView.setCurrentItem(getNavigationItemPosition(getString(R.string.sfb_PDF)), false);
                }
                if (create) {
                    showPDFFragment(animate);
                } else {
                    mFileBrowserFragment = (FileBrowserFragment) getSupportFragmentManager().findFragmentByTag(FileBrowserFragment.FRAGMENT_TAG);
                }

                mGalleryFragment = null;

                break;
            case TYPE_VIDEO:
                mGalleryFragment = null;

                break;
            case TYPE_FILE_BROWSER:
                if (selectItem) {
                    mBottomNavigationView.setCurrentItem(getNavigationItemPosition(getString(R.string.sfb_file)), false);
                }
                if (create) {
                    showFilesFragment(animate);
                } else {
                    mFileBrowserFragment = (FileBrowserFragment) getSupportFragmentManager().findFragmentByTag(FileBrowserFragment.FRAGMENT_TAG);
                }

                mGalleryFragment = null;
                break;
            case TYPE_GALLERY:
                if (selectItem) {
                    mBottomNavigationView.setCurrentItem(getNavigationItemPosition(getString(R.string.sfb_gallery)), false);
                }
                if (create) {
                    showGallery(animate);
                } else {
                    mGalleryFragment = (GalleryFragment) getSupportFragmentManager().findFragmentByTag(GalleryFragment.FRAGMENT_TAG);
                }

                mFileBrowserFragment = null;
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
                if (mFileBrowserFragment != null) {
                    mFileBrowserFragment.search(query);
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
        ImageButton btnVerticalDotsMore = findViewById(R.id.fileBrowser_activity_main_imgVerticalDotsMore);
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

    }

    @SuppressLint("ClickableViewAccessibility")
    private void showAlbumsList(boolean animate) {
        if (mAlbumListView == null) {
            mAlbumListView = LayoutInflater.from(this).inflate(R.layout.sfb_album_recyclerview_layout, mAlbumPlaceHolder, true);

            RecyclerView albumRecyclerView = mAlbumListView.findViewById(R.id.fileBrowser_activity_main_albumRecyclerView);
            AlbumAdapter albumAdapter = new AlbumAdapter();
            albumAdapter.setOnItemClickListener(new OnItemClickListener<AlbumModel>() {
                @Override
                public void onItemClicked(AlbumModel model, int position) {
                    if (mGalleryFragment != null) {
                        mAlbumListIsShowing = false;
                        mGalleryFragment.updateGallery(model);
                        hideAlbumList(false);
                        AppCompatTextView spnSelectAlbum = findViewById(R.id.fileBrowser_activity_main_spnSelectAlbum);
                        spnSelectAlbum.setText(model.getName());
                    }
                }
            });
            albumRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            albumRecyclerView.setAdapter(albumAdapter);
            mGalleryViewModel.getAllImageAlbums().observe(mGalleryFragment, new Observer<List<AlbumModel>>() {
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
        }
        mFileFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory() ||
                        FileUtil.getMimeTypeFromPath(pathname.getPath()).toLowerCase().contains("pdf");
            }
        };
        if (mFileBrowserFragment == null) {
            mFileBrowserFragment = FileBrowserFragment.getInstance(mCanSelectMultipleInFiles);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fileBrowser_activity_main_contentFragment,
                            mFileBrowserFragment, FileBrowserFragment.FRAGMENT_TAG)
                    .commit();
        } else {
            mFileBrowserFragment.changePages(mPageType);
        }
    }

    private void showAudioFragment(boolean animate) {
        if (animate) {
            animateBottomSheet();
        }
        mFileFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory() ||
                        FileUtil.getMimeTypeFromPath(pathname.getPath()).toLowerCase().contains("audio");
            }
        };
        if (mFileBrowserFragment == null) {
            mFileBrowserFragment = FileBrowserFragment.getInstance(mCanSelectMultipleInFiles);
            getSupportFragmentManager().beginTransaction().replace(R.id.fileBrowser_activity_main_contentFragment,
                    mFileBrowserFragment, FileBrowserFragment.FRAGMENT_TAG)
                    .commit();
        } else {
            mFileBrowserFragment.changePages(mPageType);
        }

    }

    private void showFilesFragment(boolean animate) {
        if (animate) {
            animateBottomSheet();
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
        if (mFileBrowserFragment == null) {
            mFileBrowserFragment = FileBrowserFragment.getInstance(mCanSelectMultipleInFiles);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fileBrowser_activity_main_contentFragment,
                            mFileBrowserFragment, FileBrowserFragment.FRAGMENT_TAG)
                    .commit();
        } else {
            mFileBrowserFragment.changePages(mPageType);
        }

    }

    private void showGallery(boolean animate) {
        if (animate) {
            animateBottomSheet();
        }
        mGalleryFragment = GalleryFragment.getInstance(mShowVideosInGallery, mShowCamera, mCanSelectMultipleInGallery);
        getSupportFragmentManager().beginTransaction().replace(R.id.fileBrowser_activity_main_contentFragment,
                mGalleryFragment, GalleryFragment.FRAGMENT_TAG)
                .commit();
    }

    private void animateBottomSheet() {
        mBottomSheetRoot.animate().translationY(100).setInterpolator(new OvershootInterpolator())
                .setDuration(300).withEndAction(() -> {
            mBottomSheetRoot.animate().translationY(0).setInterpolator(new OvershootInterpolator())
                    .setDuration(300).start();
        }).start();
    }


    private float convertOffsetToDimen(float offsetStart, float offsetEnd, float dimenStart, float dimenEnd, float offset) {
        return ((offset * (dimenStart - dimenEnd)) - (offsetEnd * dimenStart) + (offsetStart * dimenEnd)) / (offsetStart - offsetEnd);
    }


    @Override
    public void finish() {
        try {
            mBottomSheetBehavior.setHideable(true);
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }catch (Exception ignore){}
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
            if (mFileBrowserFragment != null) {
                if (mFileBrowserFragment.isInSubDirectory()) {
                    mFileBrowserFragment.goBackToParentDirectory();
                    return;
                }
            }
        }
        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            if (mGalleryFragment != null) {
                mGalleryFragment.scrollToFirstPos();
            } else if (mFileBrowserFragment != null) {
                mFileBrowserFragment.scrollToFirstPos();
            }
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
            return;
        }
        if (mSelectionFileViewModel.getSelectionHelperLiveData().getValue().selectionCount > 0) {

            mSelectionFileViewModel.removeAllSelections();
            if (mFileBrowserFragment != null) {
                mOnFileItemSelectListener.onItemSelected(new FileBrowserModel(), 0, 0);
                mFileBrowserFragment.removeAllSelection();
            }

            return;
        }

        if (mGalleryFragment != null) {
            if (mGalleryFragment.getSelectionCount() > 0) {

                mOnFileItemSelectListener.onItemSelected(new GalleryModel(), 0, 0);
                mGalleryFragment.removeAllSelections();
                return;
            }
        }
        if (!TextUtils.isEmpty(mEditedImagePath)){
            File editedImageTempFile=new File(mEditedImagePath);
        if (editedImageTempFile.exists()){
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

//    public OnItemSelectListener<GalleryModel> getOnGalleryItemSelectListener() {
//        return mOnGalleryItemSelectListener;
//    }

    public OnItemClickListener<GalleryModel> getOnGalleryItemClickListener() {
        return mOnGalleryItemClickListener;
    }

    public OnItemChooseListener getOnItemChooseListener() {
        return mOnItemChooseListener;
    }

    public OnItemLongClickListener<GalleryModel> getOnGalleryItemLongClickListener() {
        return mOnGalleryItemLongClickListener;
    }

    public enum PageType {
        TYPE_GALLERY, TYPE_FILE_BROWSER, TYPE_PDF, TYPE_AUDIO, TYPE_VIDEO
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GalleryFragment.REQ_CODE_TACK_PICTURE) {
            if (mGalleryFragment != null) {
                mGalleryFragment.onActivityResult(requestCode, resultCode, data);
            }
        }
        if (requestCode==REQ_CODE_EDIT_IMAGE){
            if (mGalleryFragment!=null){
                if (data != null) {
                    boolean isImageEdit = data.getBooleanExtra("is_image_edited", false);
                    String sourcePath = data.getStringExtra("source_path");
                    String newFilePath = data.getStringExtra("output_path");
                   if (isImageEdit){
                       mGalleryFragment.imageUpdated(sourcePath,newFilePath,mEditedImagePosition);
                   }
                }
            }
        }
    }
}