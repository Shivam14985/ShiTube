package com.example.shivambhardwaj.shitube.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shivambhardwaj.shitube.Activities.ViewVideoActivity;
import com.example.shivambhardwaj.shitube.Models.CreatersModel;
import com.example.shivambhardwaj.shitube.Models.VideoModel;
import com.example.shivambhardwaj.shitube.R;
import com.example.shivambhardwaj.shitube.databinding.UserHistoryLayoutBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.viewHolder> {
    ArrayList<VideoModel> list;
    Context context;

    public HistoryAdapter(ArrayList<VideoModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_history_layout, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, @SuppressLint("RecyclerView") int position) {
        VideoModel videoModel = list.get(position);
        holder.binding.VideoTitle.setText(videoModel.getTitle());
        holder.binding.channelName.setText(videoModel.getChannelName());
        FirebaseDatabase.getInstance().getReference().child("Videos").child(videoModel.getPostId()).child("lastSeen").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                   int lastWatched=snapshot.getValue().hashCode();
                    int percentage= (int) (lastWatched*100/ videoModel.getDuration());
                    holder.binding.progressBar.setProgress(percentage);
                }
                else {
                    holder.binding.progressBar.setProgress(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Picasso.get().load(videoModel.getThumbnail()).placeholder(R.drawable.bg_edittext).into(holder.binding.ThumnailImage);
        //Click To View Video
        holder.binding.recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewVideoActivity.class);
                intent.putExtra("VideoContent", list.get(position));  // Use model directly
                context.startActivity(intent);
                FirebaseDatabase.getInstance().getReference().child("Videos").child(videoModel.getPostId()).child("viewedBy").child(FirebaseAuth.getInstance().getUid()).setValue("true");
                FirebaseDatabase.getInstance().getReference().child("Videos").child(videoModel.getPostId()).child("viewsCount").setValue(videoModel.getViewsCount() + 1);

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        UserHistoryLayoutBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = UserHistoryLayoutBinding.bind(itemView);
        }
    }
}
