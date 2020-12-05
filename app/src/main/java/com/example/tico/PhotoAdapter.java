package com.example.tico;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {
    private List<String> photoURLs;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView foto;
        public ViewHolder(View itemView) {
            super(itemView);
            this.foto = itemView.findViewById(R.id.foto);
        }
    }

    public PhotoAdapter(List<String> photoURLs, Context context) {
        this.photoURLs = photoURLs;
        this.context = context;
    }

    @NonNull
    @Override
    public PhotoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View photoView = inflater.inflate(R.layout.photos_layout, parent, false);
        return new PhotoAdapter.ViewHolder(photoView);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoAdapter.ViewHolder holder, int position) {
        ImageView photoImageView = holder.foto;
        String photoURL = photoURLs.get(position);
        Picasso.get().load(photoURL).resize(50, 50).centerCrop().into(photoImageView);
    }

    @Override
    public int getItemCount() {
        return photoURLs.size();
    }
}
