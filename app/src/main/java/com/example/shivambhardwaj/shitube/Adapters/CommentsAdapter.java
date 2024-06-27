package com.example.shivambhardwaj.shitube.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shivambhardwaj.shitube.Models.CommentsModel;
import com.example.shivambhardwaj.shitube.Models.CreatersModel;
import com.example.shivambhardwaj.shitube.Models.UsersModel;
import com.example.shivambhardwaj.shitube.R;
import com.example.shivambhardwaj.shitube.databinding.CommentsRecyclerViewBinding;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.viewHolder> {
    ArrayList<CommentsModel> list;
    Context context;

    public CommentsAdapter(ArrayList<CommentsModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comments_recycler_view, parent, false);
        viewHolder viewHolder = new viewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        CommentsModel commentsmodel = list.get(position);
        holder.binding.comment.setText(commentsmodel.getComment());
        String time = TimeAgo.using(commentsmodel.getCommentedAt());
        FirebaseDatabase.getInstance().getReference().child("Creaters").child(commentsmodel.getCommentedBy()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    CreatersModel model = snapshot.getValue(CreatersModel.class);
                    holder.binding.Name.setText(model.getName()+" • "+time);
                    Picasso.get().load(model.getProfileImage()).placeholder(R.drawable.profileuser).into(holder.binding.ProfileImage);

                } else {
                    FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            UsersModel model = snapshot.getValue(UsersModel.class);

                            holder.binding.Name.setText(model.getUserName()+"•"+time);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
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

    public class viewHolder extends RecyclerView.ViewHolder {
        CommentsRecyclerViewBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CommentsRecyclerViewBinding.bind(itemView);
        }
    }
}
