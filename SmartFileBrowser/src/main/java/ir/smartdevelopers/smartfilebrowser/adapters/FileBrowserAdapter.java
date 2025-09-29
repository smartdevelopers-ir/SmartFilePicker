package ir.smartdevelopers.smartfilebrowser.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ir.smartdevelopers.smartfilebrowser.R;
import ir.smartdevelopers.smartfilebrowser.customClasses.FileUtil;
import ir.smartdevelopers.smartfilebrowser.customClasses.OnItemChooseListener;
import ir.smartdevelopers.smartfilebrowser.customClasses.OnItemClickListener;
import ir.smartdevelopers.smartfilebrowser.customClasses.OnItemSelectListener;
import ir.smartdevelopers.smartfilebrowser.customClasses.OnSearchListener;
import ir.smartdevelopers.smartfilebrowser.customClasses.SFBCheckBoxWithTick;
import ir.smartdevelopers.smartfilebrowser.customClasses.Utils;
import ir.smartdevelopers.smartfilebrowser.models.FileBrowserModel;
import ir.smartdevelopers.smartfilebrowser.models.FileModel;

public class FileBrowserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    public static int VIEW_TYPE_NORMAL = 1;
    public static int VIEW_TYPE_ALL_FILE_TEXT = 2;

    private List<FileBrowserModel> mFileBrowserModels = new ArrayList<>();
    private List<FileBrowserModel> mFileBrowserModelsCopy = new ArrayList<>();
    private OnItemClickListener<FileBrowserModel> mOnItemClickListener;
    private OnItemChooseListener mOnItemChooseListener;
    private OnSearchListener mOnSearchListener;
    private OnItemSelectListener<FileModel> mOnItemSelectListener;
//    private boolean isMultiSelect=false;
    private  List<File> mSelectedFiles;
    private boolean mCanSelectMultiple=true;

    public FileBrowserAdapter(List<File> selectedFiles) {
        mSelectedFiles=selectedFiles;
    }

    class DiffCallback extends DiffUtil.Callback{
        List<FileBrowserModel> oldList;
        List<FileBrowserModel> newList;

        public DiffCallback(List<FileBrowserModel> oldList, List<FileBrowserModel> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            if (oldList.get(oldItemPosition).getId()==FileBrowserModel.ID_RECENT_FILES ||
                    newList.get(newItemPosition).getId() == FileBrowserModel.ID_RECENT_FILES){
                return false;
            }
            return Objects.equals(oldList.get(oldItemPosition),newList.get(newItemPosition));
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return Objects.equals(oldList.get(oldItemPosition),newList.get(newItemPosition));
        }
    }
    public void setList(List<FileBrowserModel> fileBrowserModels) {
//        notifyItemRangeRemoved(0,mFileBrowserModelsCopy.size());

//        notifyItemRangeInserted(0,mFileBrowserModelsCopy.size());
        DiffUtil.DiffResult result=DiffUtil.calculateDiff(new DiffCallback(mFileBrowserModelsCopy,fileBrowserModels),false);
        mFileBrowserModels = fileBrowserModels;
        mFileBrowserModelsCopy = fileBrowserModels;
        result.dispatchUpdatesTo(this);
        //notifyDataSetChanged();
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return mFileBrowserModels.get(position).getId();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ALL_FILE_TEXT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file_browser_all_file_title_layout, parent, false);
            return new RecentViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file_browser_layout, parent, false);
            return new FileBrowserViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mFileBrowserModels.get(position).getModelType() == FileBrowserModel.MODEL_TYPE_ALL_FILE_TITLE) {
            return VIEW_TYPE_ALL_FILE_TEXT;
        }
        return VIEW_TYPE_NORMAL;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
       if (payloads.size()>0){
           String command=(String)payloads.get(0);
           FileBrowserViewHolder viewHolder= ((FileBrowserViewHolder)holder);
           switch (command){
               case "item_selected":
                   viewHolder.itemSelected(mFileBrowserModels.get(position),true);
                   return;
           }
       }
       super.onBindViewHolder(holder, position, payloads);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder.getItemViewType() == VIEW_TYPE_NORMAL) {
            ((FileBrowserViewHolder) viewHolder).bindView(mFileBrowserModels.get(position));
        } else {
            ((RecentViewHolder) viewHolder).bindView(mFileBrowserModels.get(position));

        }
    }

    @Override
    public int getItemCount() {
        return mFileBrowserModels.size();
    }

    public void setOnItemClickListener(OnItemClickListener<FileBrowserModel> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }



    public void setOnItemSelectListener(OnItemSelectListener<FileModel> onItemSelectListener) {
        mOnItemSelectListener = onItemSelectListener;
    }

    public boolean isCanSelectMultiple() {
        return mCanSelectMultiple;
    }

    public void setCanSelectMultiple(boolean canSelectMultiple) {
        mCanSelectMultiple = canSelectMultiple;
    }

//    public void setMultiSelectEnabled(boolean isMultiSelectionEnabled) {
//        isMultiSelect=isMultiSelectionEnabled;
//    }
//
//    public boolean isMultiSelectEnabled() {
//        return isMultiSelect;
//    }

    public void removeAllSelection() {
        for (FileBrowserModel model:mFileBrowserModels){
            model.setSelected(false);
        }
        mSelectedFiles.clear();
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<FileBrowserModel> filtered=new ArrayList<>();
                if (constraint==null || constraint.length()==0){
                    filtered=mFileBrowserModelsCopy;
                }else {
                    for (FileBrowserModel model:mFileBrowserModelsCopy){
                        if (model.getCurrentFile()!=null && model.getCurrentFile().isFile()){
                            if (Utils.contains(model.getTitle(),constraint.toString())){
                                filtered.add(model);
                            }
                        }
                    }
                }
                FilterResults results=new FilterResults();
                results.values=filtered;
                results.count=filtered.size();
                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mFileBrowserModels=(List<FileBrowserModel>) results.values;

                if (results.count>0) {
                    notifyDataSetChanged();
                }
                if (mOnSearchListener != null) {
                    mOnSearchListener.onSearch(results.count , constraint==null? "" :constraint.toString());
                }
            }
        };
    }

    public void setOnSearchListener(OnSearchListener onSearchListener) {
        mOnSearchListener = onSearchListener;
    }

    public boolean isInSubDirectory() {
        return mFileBrowserModelsCopy.get(0).getModelType() == FileBrowserModel.MODEL_TYPE_GO_BACK;
    }

    public void goBackToParentDirectory() {
        FileBrowserModel model=mFileBrowserModelsCopy.get(0);
        if (model!=null){
            if (model.getModelType()==FileBrowserModel.MODEL_TYPE_GO_BACK){
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClicked(model,null,0);
                }
            }
        }
    }

    public FileBrowserAdapter setOnItemChooseListener(OnItemChooseListener onItemChooseListener) {
        mOnItemChooseListener = onItemChooseListener;
        return this;
    }

    public class FileBrowserViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIcon;
        TextView txtTitle, txtSubTitle;
        View divider;
        View root;
        SFBCheckBoxWithTick chbSelected;
        TextView txtExtension;

        public FileBrowserViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.item_file_browser_imgIcon);
            txtTitle = itemView.findViewById(R.id.item_file_browser_txtTitle);
            txtSubTitle = itemView.findViewById(R.id.item_file_browser_txtSubTitle);
            divider = itemView.findViewById(R.id.item_file_browser_divider);
            root = itemView.findViewById(R.id.item_file_browser_root);
            txtExtension = itemView.findViewById(R.id.item_file_browser_txtExtension);
            chbSelected = itemView.findViewById(R.id.item_file_browser_chbSelected);
            root.setOnClickListener(v -> {
                FileBrowserModel model=mFileBrowserModels.get(getAdapterPosition());
                if (!mCanSelectMultiple && model.getCurrentFile()!=null && model.getCurrentFile().isFile()){
                    if (mOnItemChooseListener != null) {
                        mOnItemChooseListener.onChoose(model);

                    }
                    return;
                }
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClicked(model,v, getAdapterPosition());
                }
//                if (isMultiSelect){
                    if (!FileUtil.isDirectory(model.getCurrentFile())) {
                        addSelection(model, !model.isSelected());
                    }
//                }
            });
            root.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
//                    if (!mCanSelectMultiple){
//                        return false;
//                    }
//                    FileBrowserModel model=mFileBrowserModels.get(getAdapterPosition());
//                    if (!FileUtil.isDirectory(model.getCurrentFile())){
//                        if (!isMultiSelect){
//                            isMultiSelect=true;
//                            addSelection(model,true);
//                            return true;
//                        }
//                    }
                    return true;
                }
            });
        }

        void bindView(FileBrowserModel model) {
            if (getAdapterPosition() == mFileBrowserModels.size() - 1 ||
                    model.getModelType() == FileBrowserModel.MODEL_TYPE_DOWNLOAD_FOLDER) {
                divider.setVisibility(View.GONE);
            } else {
                divider.setVisibility(View.VISIBLE);
            }
            switch (model.getModelType()) {
                case FileBrowserModel.MODEL_TYPE_EXTERNAL_STORAGE:
                    imgIcon.setImageResource(R.drawable.sfb_ic_sdcard);
                    hideExtensionText();
                    break;
                case FileBrowserModel.MODEL_TYPE_INTERNAL_STORAGE:
                    imgIcon.setImageResource(R.drawable.sfb_ic_storage);
                    hideExtensionText();
                    break;
                case FileBrowserModel.MODEL_TYPE_DOWNLOAD_FOLDER:
                    imgIcon.setImageResource(R.drawable.sfb_ic_download_folder);
                    divider.setVisibility(View.GONE);
                    hideExtensionText();
                    break;
                case FileBrowserModel.MODEL_TYPE_FOLDER:
                    imgIcon.setImageResource(R.drawable.sfb_ic_folder_circle_blue);
                    hideExtensionText();
                    break;
                case FileBrowserModel.MODEL_TYPE_IMAGE:
                case FileBrowserModel.MODEL_TYPE_VIDEO:
                    Glide.with(imgIcon).load(model.getPath()).into(imgIcon);
                    hideExtensionText();
                    break;
                case FileBrowserModel.MODEL_TYPE_AUDIO:
                    imgIcon.setImageResource(R.drawable.sfb_ic_file_icon_blue_audio);
                    showExtensionText(model.getExtension());
                    break;
                case FileBrowserModel.MODEL_TYPE_FILE:
                    if ("apk".equalsIgnoreCase(model.getExtension())){
                        imgIcon.setImageResource(R.drawable.sfb_ic_file_icon_green);
                    }else {
                        imgIcon.setImageResource(R.drawable.sfb_ic_file_icon_cerulean_file);
                    }
                    showExtensionText(model.getExtension());
                    break;
                case FileBrowserModel.MODEL_TYPE_PDF:
                    imgIcon.setImageResource(R.drawable.sfb_ic_file_icon_red_pdf);
                    showExtensionText(model.getExtension());
                    break;
                case FileBrowserModel.MODEL_TYPE_GO_BACK:
                    imgIcon.setImageResource(R.drawable.sfb_ic_folder_back_blue);
                    hideExtensionText();
                    break;
            }
            txtTitle.setText(model.getTitle());
            txtSubTitle.setText(model.getSubTitle());
            /*check selection*/
           if (mCanSelectMultiple){
               itemSelected(model,false);
           }
        }
        public void showExtensionText(String ext){
            txtExtension.setVisibility(View.VISIBLE);
            txtExtension.setText(ext);
        }
        public void hideExtensionText(){
            txtExtension.setVisibility(View.GONE);
        }
        public void itemSelected(FileBrowserModel model,boolean animate) {
            if (mSelectedFiles.contains(model.getCurrentFile())){
//                chbSelected.setVisibility(View.VISIBLE);
                chbSelected.setChecked(true);
                if (!model.isSelected()){
                    model.setSelected(true);
                }
            }else {
                chbSelected.setChecked(false);
//                chbSelected.setVisibility(View.GONE);
                if (model.isSelected()){
                    model.setSelected(false);
                }
            }
            if (!animate){
                chbSelected.jumpDrawablesToCurrentState();
            }
        }
    }

    private void addSelection(FileBrowserModel model, boolean selected) {
        int pos=mFileBrowserModels.indexOf(model);
        model.setSelected(selected);

        if (selected){
            mSelectedFiles.add(model.getCurrentFile());
        }else {
            mSelectedFiles.remove(model.getCurrentFile());
        }
        int size=mSelectedFiles.size();
        if (mOnItemSelectListener!=null){
            mOnItemSelectListener.onItemSelected(model,pos,size);
        }
//        if (size==0){
//            isMultiSelect=false;
//        }
        notifyItemChanged(pos,"item_selected");
    }

    public class RecentViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle;

        public RecentViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.item_file_browser_txtAllFileTitle);
        }

        void bindView(FileBrowserModel model) {
            if (getAdapterPosition()==mFileBrowserModelsCopy.size()-1){
                itemView.setVisibility(View.GONE);
                return;
            }else {
                itemView.setVisibility(View.VISIBLE);
            }
            txtTitle.setText(model.getTitle());
        }
    }
    public void setSelectedFiles(List<File> selectedFiles) {
        mSelectedFiles = selectedFiles;
    }
}
