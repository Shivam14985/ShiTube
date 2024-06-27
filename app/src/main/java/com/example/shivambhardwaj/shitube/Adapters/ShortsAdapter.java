package com.example.shivambhardwaj.shitube.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shivambhardwaj.shitube.Models.CreatersModel;
import com.example.shivambhardwaj.shitube.Models.VideoModel;
import com.example.shivambhardwaj.shitube.R;
import com.example.shivambhardwaj.shitube.databinding.ShortsVideoRecyclerDesignBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ShortsAdapter extends RecyclerView.Adapter<ShortsAdapter.vieHolder> {
    ArrayList<VideoModel> list;
    Context context;
    ExoPlayer exoPlayer;

    public ShortsAdapter(ArrayList<VideoModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override

    public vieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.shorts_video_recycler_design, parent, false);
        return new vieHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull vieHolder holder, int position) {
        VideoModel model = list.get(position);
        // Prepare the media source
        exoPlayer = new ExoPlayer.Builder(context).build();
        holder.binding.ExoplayerView.setPlayer(exoPlayer);
        Uri uri = Uri.parse(model.getVideo());
        MediaItem mediaItem = MediaItem.fromUri(uri);
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
        exoPlayer.setPlayWhenReady(true);

        holder.binding.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int likes = model.getLikesCount() + 1;
                model.setLikesCount(likes);
                FirebaseDatabase.getInstance().getReference().child("Shorts").child(model.getPostId()).child("likesCount").setValue(likes);
            }
        });
        holder.binding.Title.setText(model.getTitle());
        FirebaseDatabase.getInstance().getReference().child("Creaters").child(model.getAddedBy()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                CreatersModel creatersModel = snapshot.getValue(CreatersModel.class);
                holder.binding.Name.setText(creatersModel.getName());
                Picasso.get().load(creatersModel.getProfileImage()).into(holder.binding.Profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class vieHolder extends RecyclerView.ViewHolder {
        ShortsVideoRecyclerDesignBinding binding;

        public vieHolder(@NonNull View itemView) {
            super(itemView);
            binding = ShortsVideoRecyclerDesignBinding.bind(itemView);
        }
    }
}
