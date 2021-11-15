package ir.smartdevelopers.smartfilebrowser.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import ir.smartdevelopers.smartfilebrowser.R;
import ir.smartdevelopers.smartfilebrowser.customClasses.OnItemClickListener;
import ir.smartdevelopers.smartfilebrowser.models.AlbumModel;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {

    private List<AlbumModel> mAlbumModels=new ArrayList<>();
    private OnItemClickListener<AlbumModel> mOnItemClickListener;

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_image_album_layout,viewGroup,false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder albumViewHolder, int i) {
        albumViewHolder.bindView(mAlbumModels.get(i));
    }

    @Override
    public int getItemCount() {
        return mAlbumModels.size();
    }

    public void setAlbumModels(List<AlbumModel> albumModels) {
        mAlbumModels = albumModels;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener<AlbumModel> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    class AlbumViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAlbumIcon;
        TextView txtAlbumName;
        View root;
        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAlbumIcon=itemView.findViewById(R.id.item_album_list_imgAlbumIcon);
            txtAlbumName=itemView.findViewById(R.id.item_album_list_txtAlbumName);
            root=itemView.findViewById(R.id.item_album_list_root);
            root.setOnClickListener(v->{
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClicked(mAlbumModels.get(getAdapterPosition()),v,getAdapterPosition());
                }
            });
        }
        void bindView(AlbumModel model){
            Glide.with(itemView).load(model.getImagePath()).into(imgAlbumIcon);
            ViewCompat.setTransitionName(imgAlbumIcon,"T_N_"+getAdapterPosition());
            txtAlbumName.setText(model.getName());
        }
    }
}
