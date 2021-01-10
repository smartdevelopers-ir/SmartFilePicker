package ir.smartdevelopers.smartfilebrowser.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ir.smartdevelopers.smartfilebrowser.R;
import ir.smartdevelopers.smartfilebrowser.customClasses.FileUtil;
import ir.smartdevelopers.smartfilebrowser.customClasses.OnItemChooseListener;
import ir.smartdevelopers.smartfilebrowser.customClasses.OnItemClickListener;
import ir.smartdevelopers.smartfilebrowser.customClasses.OnItemSelectListener;
import ir.smartdevelopers.smartfilebrowser.customClasses.SFBCountingCheckBox;
import ir.smartdevelopers.smartfilebrowser.models.GalleryModel;

public class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<GalleryModel> mGalleryModels;
    private List<File> mSelectedFiles=new ArrayList<>();
    private boolean mCanSelectMultiple=true;
    private OnItemSelectListener<GalleryModel> mOnItemSelectListener;
    private OnItemClickListener<GalleryModel> mOnItemClickListener;
    private OnItemChooseListener mOnItemChooseListener;

    public GalleryAdapter() {
       mGalleryModels=new ArrayList<>();
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
        mGalleryModels=galleryModels;
        for (GalleryModel model:mGalleryModels){
            if (model.getCurrentFile()==null){
                continue;
            }
            if (mSelectedFiles.contains(model.getCurrentFile())){
                model.setSelected(true);
            }
        }
        notifyDataSetChanged();
    }

    public boolean canSelectMultiple() {
        return mCanSelectMultiple;
    }

    public void setCanSelectMultiple(boolean canSelectMultiple) {
        mCanSelectMultiple = canSelectMultiple;
    }

    public void setOnItemSelectListener(OnItemSelectListener<GalleryModel> onItemSelectListener) {
        mOnItemSelectListener = onItemSelectListener;
    }

    public void setOnItemClickListener(OnItemClickListener<GalleryModel> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void addNewPic(GalleryModel newPicModel) {
        if (!mCanSelectMultiple){
            if (mOnItemClickListener!=null){
                mOnItemClickListener.onItemClicked(newPicModel,0);
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

    class CameraViewHolder extends RecyclerView.ViewHolder {
        AppCompatImageView mImageView;
        public CameraViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView=itemView.findViewById(R.id.item_gallery_camera);
            mImageView.setOnClickListener(v->{
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClicked(mGalleryModels.get(getAdapterPosition()),getAdapterPosition());
                }
            });
        }
    }

    class GalleryViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        SFBCountingCheckBox chbSelection;
        ImageView imgPlay;
        public GalleryViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView=itemView.findViewById(R.id.item_gallery_image);
            imgPlay=itemView.findViewById(R.id.item_gallery_imgPlayIcon);
            chbSelection=itemView.findViewById(R.id.item_gallery_chbSelection);
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
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClicked(mGalleryModels.get(getAdapterPosition()),getAdapterPosition());
                    }
                }else {
                    if (mOnItemChooseListener != null) {
                        mOnItemChooseListener.onChoose(mGalleryModels.get(getAdapterPosition()));
                    }
                }
            });

        }
        void bindView(GalleryModel model){
//            Picasso.get().load(model.getPath()).into(mImageView);
            Glide.with(mImageView).load(model.getPath()).placeholder(R.drawable.sfb_placeholder).into(mImageView);
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
