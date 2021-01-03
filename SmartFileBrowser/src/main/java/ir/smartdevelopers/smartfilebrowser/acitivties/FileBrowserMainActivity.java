package ir.smartdevelopers.smartfilebrowser.acitivties;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.io.FileFilter;
import java.util.List;

import ir.smartdevelopers.smartfilebrowser.R;
import ir.smartdevelopers.smartfilebrowser.adapters.AlbumAdapter;
import ir.smartdevelopers.smartfilebrowser.customClasses.FileUtil;
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
import it.sephiroth.android.library.bottomnavigation.BottomNavigation;

public class FileBrowserMainActivity extends AppCompatActivity {


    private GalleryFragment mGalleryFragment;
    private FileBrowserFragment mFileBrowserFragment;
    private AppBarLayout mAppBarLayout;
    private RoundLinearLayout mBottomSheetRoot;
    private BottomSheetBehavior<View> mBottomSheetBehavior;
    private BottomNavigation mBottomNavigationView;
    //    private BottomNavigationBar mBottomNavigationView;
//    private BottomNavigationView mBottomNavigationView;
    private View mMainRootView;
    private int mActionBarSize;
    private float mRadius;
    private View mDraggingLineView;
    private FileFilter mFileFilter;
    private BottomNavigation.OnMenuItemSelectionListener mOnMenuItemSelectionListener;
    private View mSelectionContainer;
    private TextView txtSelectionCount;
    private ImageView imgSelectionOk;
    private OnItemSelectListener<FileBrowserModel> mOnFileItemSelectListener;
    private OnItemSelectListener<GalleryModel> mOnGalleryItemSelectListener;

    private SearchView.OnVisibilityChangeListener mOnVisibilityChangeListener;
    private SelectionFileViewModel mSelectionFileViewModel;



    private PageType mPageType;
    private GalleryViewModel mGalleryViewModel;

    /*toolbars*/
    private View mGalleyToolbar, mFileBrowserToolbar;
    private FrameLayout mToolbarPlaceHolder;
    private FrameLayout mAlbumPlaceHolder;
    private View mAlbumListView;
    private boolean mAlbumListIsShowing=false;
    private ImageButton btnBack;

    /*Builder parameters*/
    private FileFilter mFileTabFileFilter;
    private boolean mShowVideosInGallery=true;
    private boolean mShowCamera=true;
    private boolean mCanSelectMultipleInGallery=true;
    private boolean mCanSelectMultipleInFiles=true;
    private boolean mShowPDFTab=true;
    private boolean mShowFilesTab=true;
    private boolean mShowAudioTab=true;
    private boolean mShowGalleryTab=true;


    public static class Builder{
        private SFBFileFilter fileTabFileFilter;
        private boolean showVideosInGallery=true;
        private boolean showCamera=true;
        private boolean canSelectMultipleInGallery=true;
        private boolean canSelectMultipleInFiles=true;
        private boolean showPDFTab=true;
        private boolean showFilesTab=true;
        private boolean showAudioTab=true;
        private boolean showGalleryTab=true;

        public Builder setFileTabFileFilter(@NonNull FileFilter fileTabFileFilter) {
            this.fileTabFileFilter = new SFBFileFilter(){
                @Override
                public boolean accept(File pathname) {
                    return fileTabFileFilter.accept(pathname);
                }
            };
            return this;
        }

        public Builder setShowVideosInGallery(boolean showVideosInGallery) {
            this.showVideosInGallery = showVideosInGallery;
            return this;
        }

        public Builder setShowCamera(boolean showCamera) {
            this.showCamera = showCamera;
            return this;
        }

        public Builder setCanSelectMultipleInGallery(boolean canSelectMultipleInGallery) {
            this.canSelectMultipleInGallery = canSelectMultipleInGallery;
            return this;
        }

        public Builder setCanSelectMultipleInFiles(boolean canSelectMultipleInFiles) {
            this.canSelectMultipleInFiles = canSelectMultipleInFiles;
            return this;
        }

        public Builder setShowPDFTab(boolean showPDFTab) {
            this.showPDFTab = showPDFTab;
            return this;
        }

        public Builder setShowFilesTab(boolean showFilesTab) {
            this.showFilesTab = showFilesTab;
            return this;
        }

        public Builder setShowAudioTab(boolean showAudioTab) {
            this.showAudioTab = showAudioTab;
            return this;
        }

        public Builder setShowGalleryTab(boolean showGalleryTab) {
            this.showGalleryTab = showGalleryTab;
            return this;
        }
        public void show(Context context){

            Intent filePickerIntent=new Intent(context,FileBrowserMainActivity.class);
            filePickerIntent.putExtra("mFileTabFileFilter",fileTabFileFilter);
            filePickerIntent.putExtra("mShowVideosInGallery",showVideosInGallery);
            filePickerIntent.putExtra("mShowCamera",showCamera);
            filePickerIntent.putExtra("mCanSelectMultipleInGallery",canSelectMultipleInGallery);
            filePickerIntent.putExtra("mCanSelectMultipleInFiles",canSelectMultipleInFiles);
            filePickerIntent.putExtra("mShowPDFTab",showPDFTab);
            filePickerIntent.putExtra("mShowFilesTab",showFilesTab);
            filePickerIntent.putExtra("mShowAudioTab",showAudioTab);
            filePickerIntent.putExtra("mShowGalleryTab",showGalleryTab);
            context.startActivity(filePickerIntent);

        }
    }

    private void getDataFromIntent(){
         mFileTabFileFilter= (FileFilter) getIntent().getSerializableExtra("mFileTabFileFilter");
         mShowVideosInGallery=getIntent().getBooleanExtra("mShowVideosInGallery",true);
         mShowCamera=getIntent().getBooleanExtra("mShowCamera",true);
         mCanSelectMultipleInGallery=getIntent().getBooleanExtra("mCanSelectMultipleInGallery",true);
         mCanSelectMultipleInFiles=getIntent().getBooleanExtra("mCanSelectMultipleInFiles",true);
         mShowPDFTab=getIntent().getBooleanExtra("mShowPDFTab",true);
         mShowFilesTab=getIntent().getBooleanExtra("mShowFilesTab",true);
         mShowAudioTab=getIntent().getBooleanExtra("mShowAudioTab",true);
         mShowGalleryTab=getIntent().getBooleanExtra("mShowGalleryTab",true);
    }
    //    private BottomNavigationBar.OnTabSelectedListener mOnTabSelectedListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_browser_main);
        mSelectionFileViewModel = new ViewModelProvider(this).get(SelectionFileViewModel.class);
        mGalleryViewModel=new ViewModelProvider(this,new ViewModelProvider.AndroidViewModelFactory(getApplication()))
                .get(GalleryViewModel.class);
        findViews();
        getDataFromIntent();
        initListeners();
        initViews(savedInstanceState);
        if (savedInstanceState==null) {
            mPageType = PageType.TYPE_GALLERY;
        }else {
            mPageType= (PageType) savedInstanceState.getSerializable("page_type");
        }
        showSuitableFragment(mPageType,true,false);

        if (savedInstanceState!=null){

            int bottomSheetState=savedInstanceState.getInt("bottomSheet_state");
            mBottomSheetBehavior.setState(bottomSheetState);
            if (bottomSheetState==BottomSheetBehavior.STATE_EXPANDED){
                showSuitableToolbar(mPageType);
                mAppBarLayout.setTranslationY(0);
            }
            boolean albumListIsVisible=savedInstanceState.getBoolean("album_list_visibility");
            if (albumListIsVisible){
                showAlbumsList(false);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("album_list_visibility",mAlbumListIsShowing);
        outState.putSerializable("page_type",mPageType);
        outState.putInt("bottomSheet_state",mBottomSheetBehavior.getState());
        AppCompatTextView spnSelectAlbum=findViewById(R.id.fileBrowser_activity_main_spnSelectAlbum);
        if (spnSelectAlbum!=null){
            int[] location=new int[2];
            spnSelectAlbum.getLocationInWindow(location);
            int x= location[0];
            int y= location[1];
            outState.putInt("album_list_window_x",x);
            outState.putInt("album_list_window_y",y);
        }
    }
    private void findViews() {
        mAppBarLayout = findViewById(R.id.fileBrowser_activity_main_appbar);
        mBottomSheetRoot = findViewById(R.id.fileBrowser_activity_main_contentRootLayout);
        mBottomNavigationView = findViewById(R.id.fileBrowser_activity_main_bottomNavigation);
        mDraggingLineView = findViewById(R.id.fileBrowser_activity_main_draggingLineView);
        mSelectionContainer = findViewById(R.id.fileBrowser_activity_main_selectionContainer);
        txtSelectionCount = findViewById(R.id.fileBrowser_activity_main_txtSelectionCount);
        imgSelectionOk = findViewById(R.id.fileBrowser_activity_main_imgOk);
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

        mOnMenuItemSelectionListener = new BottomNavigation.OnMenuItemSelectionListener() {
            @Override
            public void onMenuItemSelect(int itemId, int position, boolean b) {
                if (itemId == R.id.sfb_action_gallery) {

                    mPageType = PageType.TYPE_GALLERY;

                } else if (itemId == R.id.sfb_action_file) {
                    mPageType = PageType.TYPE_FILE_BROWSER;
                } else if (itemId == R.id.sfb_action_audio) {

                    mPageType = PageType.TYPE_AUDIO;
                } else if (itemId == R.id.sfb_action_PDF) {

                    mPageType = PageType.TYPE_PDF;
                }
                showSuitableFragment(mPageType,false,true);

            }

            @Override
            public void onMenuItemReselect(int i, int i1, boolean b) {

            }
        };
        mOnFileItemSelectListener = new OnItemSelectListener<FileBrowserModel>() {
            @Override
            public void onItemSelected(FileBrowserModel model, int position, int selectionCount) {
                if (selectionCount > 0) {
                    showSelectionOkButton();
                    changeSelectionCount(selectionCount);
                    setGalleryEnabled(false);
                } else {
                    hideSelectionOkButton();
                    setGalleryEnabled(true);
                }
                mSelectionFileViewModel.onItemSelected(selectionCount, selectionCount > 0);
            }
        };
        mOnGalleryItemSelectListener=new OnItemSelectListener<GalleryModel>() {
            @Override
            public void onItemSelected(GalleryModel model, int position, int selectionCount) {
                if (selectionCount>0){
                    showSelectionOkButton();
                    changeSelectionCount(selectionCount);
                    setFileBrowserEnabled(false);
                }else {
                    hideSelectionOkButton();
                    setFileBrowserEnabled(true);
                }

            }
        };
        mOnVisibilityChangeListener=new SearchView.OnVisibilityChangeListener() {
            @Override
            public void onVisibilityChanged(boolean isShowing) {
                if (isShowing){
                    mBottomSheetBehavior.setHideable(false);
                    mBottomSheetBehavior.setDraggable(false);
                }else {
                    mBottomSheetBehavior.setHideable(true);
                    mBottomSheetBehavior.setDraggable(true);
                }
            }
        };

    }

    private boolean mFileBrowserEnabled=true;
    private void setFileBrowserEnabled(boolean enabled) {
        if (mFileBrowserEnabled==enabled){
            return;
        }
        int menuItemCount=mBottomNavigationView.getMenuItemCount();
        for (int i=0;i<menuItemCount;i++){
            if (mBottomNavigationView.getMenuItemId(i)==R.id.sfb_action_gallery){
                continue;
            }
            mBottomNavigationView.setMenuItemEnabled(i,enabled);
        }
      mFileBrowserEnabled=enabled;
    }


    private void initViews(Bundle savedInstanceState) {

        mBottomNavigationView.setMenuItemSelectionListener(mOnMenuItemSelectionListener);
        int[] res = {R.attr.actionBarSize};
        TypedArray typedArray = obtainStyledAttributes(res);
        mActionBarSize = typedArray.getDimensionPixelSize(0, 56) +
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics());
        typedArray.recycle();
        ViewGroup.LayoutParams appBarParams = mAppBarLayout.getLayoutParams();
        appBarParams.height = mActionBarSize;
        mAppBarLayout.setLayoutParams(appBarParams);
        int screenHeight=getResources().getDisplayMetrics().heightPixels;
        mRadius = getResources().getDimension(R.dimen.bottom_sheet_top_radius);
        mAppBarLayout.setTranslationY(-mActionBarSize);
        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheetRoot);
        mBottomSheetBehavior.setHideable(true);
//        mBottomSheetBehavior.setFitToContents(false);
//        mBottomSheetBehavior.setHalfExpandedRatio(0.5f);
        mBottomSheetBehavior.setPeekHeight(screenHeight/2,true);
        mBottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                float translation = -mActionBarSize;
                float h = 0.05f;
                if (slideOffset > h && slideOffset <= 1) {
                    // show appbar
                    int currentTranslation = (int) convertOffsetToDimen(h, 1, translation, 0, slideOffset);
                    Log.v("TTT", "currentTranslation=" + currentTranslation);
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
                if (slideOffset>h){
                    showSuitableToolbar(mPageType);
                }
            }
        });
        mMainRootView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                finish();
                return true;
            }
        });

//        if (savedInstanceState==null){
//            mBottomSheetRoot.post(()->{
//                startFirstAnimation();
//            });
//        }
        btnBack.setOnClickListener(v->{
            onBackPressed();
        });
        if (savedInstanceState==null){
            mBottomSheetRoot.setTranslationY(screenHeight*0.6f);
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

    private void startFirstAnimation() {
        long duration=300;
        mBottomSheetRoot.animate().setDuration(duration).translationY(0)
                .setInterpolator(new OvershootInterpolator()).start();
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

    private void showSuitableFragment(PageType type,boolean selectItem,boolean animate){
        switch (type){
            case TYPE_AUDIO:

                showAudioFragment(animate);
                if (selectItem) {
                    mBottomNavigationView.setSelectedIndex(getNavigationItemPosition(R.id.sfb_action_audio));
                }
                mGalleryFragment=null;
                break;
            case TYPE_PDF:
                if (selectItem) {
                    mBottomNavigationView.setSelectedIndex(getNavigationItemPosition(R.id.sfb_action_PDF));
                }
                showPDFFragment(animate);
                mGalleryFragment=null;

                break;
            case TYPE_VIDEO:
                mGalleryFragment=null;

                break;
            case TYPE_FILE_BROWSER:
                if (selectItem) {
                    mBottomNavigationView.setSelectedIndex(getNavigationItemPosition(R.id.sfb_action_file));
                }
                showFilesFragment(animate);
                mGalleryFragment=null;
                break;
            case TYPE_GALLERY:
                if (selectItem) {
                    mBottomNavigationView.setSelectedIndex(getNavigationItemPosition(R.id.sfb_action_gallery));
                }
                showGallery(animate);
                mFileBrowserFragment=null;
                break;
        }
    }
    private int getNavigationItemPosition(int itemId){
        int childCount=mBottomNavigationView.getChildCount();
        for (int i=0;i<childCount;i++){
            if (mBottomNavigationView.getMenuItemId(i)==itemId){
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
        TextView txtToolbarTitle=findViewById(R.id.fileBrowser_activity_main_txtToolbarTitle);
        if (txtToolbarTitle!=null){
            txtToolbarTitle.setText(toolbarTitle);
        }
    }

    private void initFileBrowserToolbar() {
        ImageButton btnSearch=findViewById(R.id.fileBrowser_activity_main_btnSearch);
        SearchView searchView=findViewById(R.id.fileBrowser_activity_main_searchView);
        btnSearch.setOnClickListener(v->{
            searchView.show();
        });
        searchView.setOnQueryChangeListener(new SearchView.OnQueryChangeListener() {
            @Override
            public void onQueryChanged(String query) {
                if (mFileBrowserFragment!=null){
                    mFileBrowserFragment.getFileBrowserAdapter().getFilter().filter(query);
                }
            }
        });

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
            int[] location=new int[2];
            spnSelectAlbum.getLocationInWindow(location);
            int x= location[0];
            int y= location[1];
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

            RecyclerView albumRecyclerView=mAlbumListView.findViewById(R.id.fileBrowser_activity_main_albumRecyclerView);
            AlbumAdapter albumAdapter=new AlbumAdapter();
            albumAdapter.setOnItemClickListener(new OnItemClickListener<AlbumModel>() {
                @Override
                public void onItemClicked(AlbumModel model, int position) {
                    if (mGalleryFragment != null) {
                        mAlbumListIsShowing=false;
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
        mAlbumListIsShowing=true;
        int duration = animate ? 100 : 0;
        FrameLayout albumPlaceHolderRoot=findViewById(R.id.fileBrowser_activity_main_albumPlaceHolderRoot);

        mAlbumPlaceHolder.post(()->{
            AppCompatTextView spnSelectAlbum = findViewById(R.id.fileBrowser_activity_main_spnSelectAlbum);
            int[] location=new int[2];
            spnSelectAlbum.getLocationInWindow(location);
            int x= location[0];
            int y= location[1];
            mAlbumPlaceHolder.setX(x);
            mAlbumPlaceHolder.setY(y);
            mAlbumPlaceHolder.animate().setDuration(duration).scaleY(1).scaleX(1).alpha(1)
                    .withStartAction(()->{
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
        FrameLayout albumPlaceHolderRoot=findViewById(R.id.fileBrowser_activity_main_albumPlaceHolderRoot);
        mAlbumListIsShowing=false;
        int duration=animate?100:50;
        ViewPropertyAnimator animator=mAlbumPlaceHolder.animate().setDuration(duration);
        if (animate) {
            animator.scaleY(0.3f).scaleX(0.3f);
        }
        animator.alpha(0)
                .withEndAction(()->{
                    albumPlaceHolderRoot.setVisibility(View.GONE);
                    mAlbumPlaceHolder.setVisibility(View.INVISIBLE);
                    mAlbumListView=null;
                    mAlbumPlaceHolder.removeAllViews();
                })
                .start();

    }


    private boolean mGalleryEnabled = true;

    private void setGalleryEnabled(boolean enabled) {
        if (mGalleryEnabled == enabled) {
            return;
        }
        int menuCount = mBottomNavigationView.getMenuItemCount();
        int pos = -1;
        for (int i = 0; i < menuCount; i++) {
            if (mBottomNavigationView.getMenuItemId(i) == R.id.sfb_action_gallery) {
                pos = i;
                break;
            }
        }
        if (pos != -1) {
            mGalleryEnabled = enabled;
            mBottomNavigationView.setMenuItemEnabled(pos, enabled);
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
        mSelectionContainer.animate().setDuration(100).scaleX(1).scaleY(1)
                .withStartAction(() -> {
                    mSelectionContainer.setScaleX(0);
                    mSelectionContainer.setScaleY(0);
                    mSelectionContainer.setVisibility(View.VISIBLE);
                }).start();
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
        if (mFileBrowserFragment==null) {
            mFileBrowserFragment = FileBrowserFragment.getInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.fileBrowser_activity_main_contentFragment,
                    mFileBrowserFragment)
                    .commit();
        }else {
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
        if (mFileBrowserFragment==null) {
            mFileBrowserFragment = FileBrowserFragment.getInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.fileBrowser_activity_main_contentFragment,
                    mFileBrowserFragment )
                    .commit();
        }else {
            mFileBrowserFragment.changePages(mPageType);
        }

    }

    private void showFilesFragment(boolean animate) {
        if (animate) {
            animateBottomSheet();
        }
        mFileFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory() ||
                        pathname.isFile();
            }
        };
        if (mFileBrowserFragment==null) {
            mFileBrowserFragment = FileBrowserFragment.getInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.fileBrowser_activity_main_contentFragment,
                    mFileBrowserFragment)
                    .commit();
        }else {
            mFileBrowserFragment.changePages(mPageType);
        }

    }

    private void showGallery(boolean animate) {
        if (animate) {
            animateBottomSheet();
        }
        mGalleryFragment=new GalleryFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fileBrowser_activity_main_contentFragment,
                mGalleryFragment)
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
        super.finish();
        overridePendingTransition(R.anim.not_anim, R.anim.fade_out);

    }

    @Override
    public void onBackPressed() {

        if (mPageType!=PageType.TYPE_GALLERY){
            SearchView searchView=findViewById(R.id.fileBrowser_activity_main_searchView);
            if (searchView!=null && searchView.isShown()){
                searchView.close(true);
                return;
            }
        }
        if (mBottomSheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED){
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
            return;
        }
        if (mSelectionFileViewModel.getSelectionHelperLiveData().getValue().selectionCount>0){

            mSelectionFileViewModel.removeAllSelections();
            if (mFileBrowserFragment != null) {
                mOnFileItemSelectListener.onItemSelected(null,0,0);
                mFileBrowserFragment.removeAllSelection();
            }

            return;
        }

        if (mGalleryFragment!=null){
            if (mGalleryFragment.getSelectionCount()>0){

                mOnGalleryItemSelectListener.onItemSelected(null,0,0);
                mGalleryFragment.removeAllSelections();
                return;
            }
        }
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

    public OnItemSelectListener<FileBrowserModel> getOnFileItemSelectListener() {
        return mOnFileItemSelectListener;
    }

    public OnItemSelectListener<GalleryModel> getOnGalleryItemSelectListener() {
        return mOnGalleryItemSelectListener;
    }

    public enum PageType {
        TYPE_GALLERY, TYPE_FILE_BROWSER,TYPE_PDF,TYPE_AUDIO,TYPE_VIDEO
    }

}