package com.example.mychat.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mychat.R;

import java.util.ArrayList;

public class GridViewAdapter extends RecyclerView.Adapter<GridViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> uriList;
    private ArrayList<String> typeList;
    //private MediaPlayer mediaPlayer;

    public GridViewAdapter(Context context, ArrayList<String> uriList, ArrayList<String> typeList) {
        this.context = context;
        this.uriList = uriList;
        this.typeList = typeList;
        //
        //this.mediaPlayer = new MediaPlayer();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String uri = uriList.get(position);
        if (isImageUri(position)) {
            holder.imageView.setVisibility(View.VISIBLE);
            holder.videoView.setVisibility(View.GONE);
            Glide.with(context).load(Uri.parse(uri)).into(holder.imageView);
        } else if (isVideoUri(position)) {
            holder.imageView.setVisibility(View.GONE);
            holder.videoView.setVisibility(View.VISIBLE);
            holder.videoView.setVideoURI(Uri.parse(uri));
            holder.videoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.videoView.isPlaying()) {
                        holder.videoView.pause();
                    } else {
                        holder.videoView.start();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return uriList.size();
    }

    private boolean isImageUri(int position) {
        return typeList.get(position).equals("image");
    }

    private boolean isVideoUri(int position) {
        return typeList.get(position).equals("video");
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        VideoView videoView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            videoView = itemView.findViewById(R.id.videoView);
        }
    }
}
