package ir.smartdevelopers.smartfilebrowser.adapters;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ir.smartdevelopers.smartfilebrowser.R;
import ir.smartdevelopers.smartfilebrowser.customClasses.FileUtil;
import ir.smartdevelopers.smartfilebrowser.customClasses.OnItemChooseListener;
import ir.smartdevelopers.smartfilebrowser.customClasses.OnItemClickListener;
import ir.smartdevelopers.smartfilebrowser.customClasses.OnItemLongClickListener;
import ir.smartdevelopers.smartfilebrowser.customClasses.OnItemSelectListener;
import ir.smartdevelopers.smartfilebrowser.customClasses.SFBCountingCheckBox;
import ir.smartdevelopers.smartfilebrowser.models.FileModel;
import ir.smartdevelopers.smartfilebrowser.models.GalleryModel;

public class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<GalleryModel> mGalleryModels;
    private List<File> mSelectedFiles;
    private boolean mCanSelectMultiple=true;
    private OnItemSelectListener<FileModel> mOnItemSelectListener;
    private OnItemClickListener<GalleryModel> mOnItemClickListener;
    private OnItemClickListener<GalleryModel> mOnZoomOutClickListener;
    private OnItemLongClickListener<GalleryModel> mOnItemLongClickListener;
    private OnItemChooseListener mOnItemChooseListener;

    public GalleryAdapter(List<File> selectedFiles) {
       mGalleryModels=new ArrayList<>();
        mSelectedFiles=selectedFiles;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        if (viewType==GalleryModel.TYPE_CAMERA){
            view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_camera_layout,viewGroup,false);
            return new CameraViewHolder(view);
        }
         view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_gallery_layout,viewGroup,false);
        return new GalleryViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
       return mGalleryModels.get(position).getType();

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.size()>0){
            String command= (String) payloads.get(0);
            if (command.equals("checked_changed")){
                if (holder instanceof GalleryViewHolder){
                    ((GalleryViewHolder) holder).checkSelection(mGalleryModels.get(position),true);

                }
                return;
            }else if (command.equals("remove_all_selections")){
                if (holder instanceof GalleryViewHolder){
                    ((GalleryViewHolder) holder).chbSelection.setChecked(false);
                    ((GalleryViewHolder) holder).scaleImageView(false,true);
                }
                return;
            }
        }
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder.getItemViewType()==GalleryModel.TYPE_CAMERA){

        }else {
            ((GalleryViewHolder)holder).bindView(mGalleryModels.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mGalleryModels.size();
    }

    public void setList(List<GalleryModel> galleryModels) {


        for (GalleryModel model:galleryModels){
            if (model.getCurrentFile()==null){
                continue;
            }
            if (mSelectedFiles.contains(model.getCurrentFile())){
                model.setSelected(true);
            }
        }
        /*if there is just camera item in it*/
        boolean firstItemIsCamera=false;
        if (mGalleryModels.size()>0 && mGalleryModels.get(0).getId()==0){
            notifyItemRangeRemoved(1,mGalleryModels.size()-1);
            firstItemIsCamera=true;
        }else if (mGalleryModels.size()>0){
            notifyItemRangeRemoved(0,mGalleryModels.size());
        }
        mGalleryModels=galleryModels;
        if (firstItemIsCamera){
            notifyItemRangeInserted(1,galleryModels.size()-1);
        }else {
            notifyItemRangeInserted(0,galleryModels.size());
        }

    }

    public boolean canSelectMultiple() {
        return mCanSelectMultiple;
    }

    public void setCanSelectMultiple(boolean canSelectMultiple) {
        mCanSelectMultiple = canSelectMultiple;
    }

    public void setOnItemSelectListener(OnItemSelectListener<FileModel> onItemSelectListener) {
        mOnItemSelectListener = onItemSelectListener;
    }

    public void setOnItemClickListener(OnItemClickListener<GalleryModel> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void addNewPic(GalleryModel newPicModel) {
        if (!mCanSelectMultiple){
            if (mOnItemClickListener!=null){
                mOnItemClickListener.onItemClicked(newPicModel,null,0);
                return;
            }
        }
        mSelectedFiles.add(newPicModel.getCurrentFile());
        newPicModel.setNumber(mSelectedFiles.size());
        newPicModel.setId(System.currentTimeMillis());
        mGalleryModels.add(1,newPicModel);
        notifyItemInserted(1);
        if (mOnItemSelectListener != null) {
            mOnItemSelectListener.onItemSelected(newPicModel,1,mSelectedFiles.size());
        }
    }

    public void removeAllSelections() {
        mSelectedFiles.clear();
        int size=mGalleryModels.size();
        for (int i=size-1;i>=0;i--){
            mGalleryModels.get(i).setSelected(false);
            notifyItemChanged(i,"remove_all_selections");
        }


    }

    public int getSelectionCount() {
        return mSelectedFiles.size();
    }

    public GalleryAdapter setOnItemChooseListener(OnItemChooseListener onItemChooseListener) {
        mOnItemChooseListener = onItemChooseListener;
        return this;
    }

    public GalleryModel getItem(int editedImagePosition) {
        return mGalleryModels.get(editedImagePosition);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener<GalleryModel> onItemLongClickListener) {
        mOnItemLongClickListener = onItemLongClickListener;
    }

    public void setOnZoomOutClickListener(OnItemClickListener<GalleryModel> onZoomOutClickListener) {
        mOnZoomOutClickListener = onZoomOutClickListener;
    }

    class CameraViewHolder extends RecyclerView.ViewHolder {
        AppCompatImageView mImageView;
        public CameraViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView=itemView.findViewById(R.id.item_gallery_camera);
            mImageView.setOnClickListener(v->{
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClicked(mGalleryModels.get(getAdapterPosition()),v,getAdapterPosition());
                }
            });
        }
    }

    class GalleryViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        SFBCountingCheckBox chbSelection;
        ImageView imgPlay;
        ImageButton btnZoomOut;
        public GalleryViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView=itemView.findViewById(R.id.item_gallery_image);
            imgPlay=itemView.findViewById(R.id.item_gallery_imgPlayIcon);
            chbSelection=itemView.findViewById(R.id.item_gallery_chbSelection);
            btnZoomOut=itemView.findViewById(R.id.item_gallery_btnZoomOut);
            if (canSelectMultiple()) {
                chbSelection.setVisibility(View.VISIBLE);
                chbSelection.setOnClickListener(v -> {
//                    GalleryModel model=mGalleryModels.get(getAdapterPosition());
                    setImageSelected(mGalleryModels.get(getAdapterPosition()), chbSelection.isChecked());

                });
            }else {
                chbSelection.setVisibility(View.GONE);
            }
            mImageView.setOnClickListener(v->{
                if (mCanSelectMultiple){
                    if(mGalleryModels.get(getAdapterPosition()).getType()==GalleryModel.TYPE_CAMERA){
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onItemClicked(mGalleryModels.get(getAdapterPosition()),v,getAdapterPosition());
                        }
                    }else {
                        setImageSelected(mGalleryModels.get(getAdapterPosition()), !mGalleryModels.get(getAdapterPosition()).isSelected());
                    }


                }else {
                    if (mOnItemChooseListener != null) {
                        mOnItemChooseListener.onChoose(mGalleryModels.get(getAdapterPosition()));
                    }
                }
            });
            mImageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mOnItemLongClickListener != null) {
                        mOnItemLongClickListener.onLongClicked(mGalleryModels.get(getAdapterPosition()),v,getAdapterPosition());
                        return true;
                    }
                    return false;
                }
            });
            btnZoomOut.setOnClickListener(v->{
                if (mOnZoomOutClickListener != null) {
                    mOnZoomOutClickListener.onItemClicked(mGalleryModels.get(getAdapterPosition()),mImageView,getAdapterPosition());
                }
            });

        }
        void bindView(GalleryModel model){
//            Picasso.get().load(model.getPath()).into(mImageView);

            
            Glide.with(mImageView).load(model.getPath())
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(mImageView);
            if (model.getType()== FileUtil.TYPE_VIDEO){
                imgPlay.setVisibility(View.VISIBLE);
            }else {
                imgPlay.setVisibility(View.GONE);
            }

            if (mCanSelectMultiple) {
                checkSelection(model, false);
            }
        }

        public void checkSelection(GalleryModel model, boolean animate) {
            chbSelection.setCounter(model.getNumber());
            chbSelection.setChecked(model.isSelected());

            if (!animate){
                chbSelection.jumpDrawablesToCurrentState();
            }
            scaleImageView(model.isSelected(),animate);

        }
        public void scaleImageView(boolean selected,boolean animate){
            int duration;
            float scale;
            if (selected){
                scale=0.85f;
            }else {
                scale=1;
            }
            if (!animate){
                duration=0;
            }else {
                duration=100;
            }
            ViewPropertyAnimator animator=mImageView.animate().scaleY(scale).scaleX(scale).setDuration(duration);
            if (animate){
                animator.setInterpolator(new FastOutSlowInInterpolator());
            }
            animator.start();
        }
    }

    private void setImageSelected(GalleryModel model, boolean selected) {
        model.setSelected(selected);

        int pos=mGalleryModels.indexOf(model);
        int previousSize=mSelectedFiles.size();
        if (selected){
            mSelectedFiles.add(model.getCurrentFile());
        }else {
            mSelectedFiles.remove(model.getCurrentFile());
        }
        if (selected) {
            model.setNumber(mSelectedFiles.size());
            notifyItemChanged(pos,"checked_changed");
        }else {
            notifyItemChanged(pos,"checked_changed");
            if (model.getNumber()<previousSize){

                int selectionCount=mGalleryModels.size();
                for (int i=0;i<selectionCount;i++){
                    GalleryModel m=mGalleryModels.get(i);
                   if (m.isSelected() && m.getNumber()>model.getNumber()){
                       m.setNumber(m.getNumber()-1);
                       notifyItemChanged(i,"checked_changed");
                   }
                }
            }else {
                notifyItemChanged(pos,"checked_changed");
            }
        }

        if (mOnItemSelectListener != null) {
            mOnItemSelectListener.onItemSelected(model,pos,mSelectedFiles.size());
        }
    }
    public List<GalleryModel> getSelectedModels(){
        List<GalleryModel> selected=new ArrayList<>();
        for (GalleryModel model:mGalleryModels){
            if (model.isSelected()){
                selected.add(model);
            }
        }
        return selected;
    }
}
