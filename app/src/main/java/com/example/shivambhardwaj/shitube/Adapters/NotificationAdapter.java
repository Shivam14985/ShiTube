package com.example.shivambhardwaj.shitube.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shivambhardwaj.shitube.Activities.CommonActivity;
import com.example.shivambhardwaj.shitube.Activities.NotificationVideoViewActivity;
import com.example.shivambhardwaj.shitube.Models.CreatersModel;
import com.example.shivambhardwaj.shitube.Models.NotificationModel;
import com.example.shivambhardwaj.shitube.Models.UsersModel;
import com.example.shivambhardwaj.shitube.Models.VideoModel;
import com.example.shivambhardwaj.shitube.R;
import com.example.shivambhardwaj.shitube.databinding.NotificationRecyclerViewBinding;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.viewHolder> {
    ArrayList<NotificationModel> list;
    Context context;

    public NotificationAdapter(ArrayList<NotificationModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_recycler_view, parent, false);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        NotificationModel model = list.get(position);
        String time = TimeAgo.using(model.getNotificationAt());
        holder.binding.time.setText(time);
        if (model.isNotificationOpened()) {
            holder.binding.clickable.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
        } else {
        }
        // Loading Video Thumbnail
        FirebaseDatabase.getInstance().getReference().child("Videos").child(model.getVideoId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                VideoModel videoModel = snapshot.getValue(VideoModel.class);
                Picasso.get().load(videoModel.getThumbnail()).into(holder.binding.imageView3);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //fetching Data
        if (model.getNotificationType().equals("Approved")) {
            FirebaseDatabase.getInstance().getReference().child("Creaters").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    CreatersModel creatersModel = snapshot.getValue(CreatersModel.class);
                    Picasso.get().load(creatersModel.getProfileImage()).placeholder(R.drawable.profileuser).into(holder.binding.profile);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            holder.binding.NotificationText.setTextColor(Color.parseColor("#178A01"));
            holder.binding.NotificationText.setText(model.getNotificationText());
            holder.binding.NotificationText.setTypeface(null, Typeface.BOLD);
        }
        if (model.getNotificationType().equals("Rejected")) {
            FirebaseDatabase.getInstance().getReference().child("Creaters").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    CreatersModel creatersModel = snapshot.getValue(CreatersModel.class);
                    Picasso.get().load(creatersModel.getProfileImage()).placeholder(R.drawable.profileuser).into(holder.binding.profile);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            holder.binding.NotificationText.setTextColor(Color.parseColor("#D30000"));
            holder.binding.NotificationText.setText(model.getNotificationText());
            holder.binding.NotificationText.setTypeface(null, Typeface.BOLD);
        }
        if (model.getNotificationType().equals("Likes")) {
            FirebaseDatabase.getInstance().getReference().child("Creaters").child(model.getNotificationBy()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        CreatersModel creatersModel = snapshot.getValue(CreatersModel.class);
                        Picasso.get().load(creatersModel.getProfileImage()).placeholder(R.drawable.profileuser).into(holder.binding.profile);
                        holder.binding.NotificationText.setText(creatersModel.getName() + " " + model.getNotificationText());
                    } else {
                        FirebaseDatabase.getInstance().getReference().child("Users").child(model.getNotificationBy()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                UsersModel usersModel = snapshot.getValue(UsersModel.class);
                                holder.binding.NotificationText.setText(usersModel.getUserName() + " " + model.getNotificationText());
                                holder.binding.profile.setImageResource(R.drawable.profileuser);
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
        if (model.getNotificationType().equals("Subscribers")) {
            FirebaseDatabase.getInstance().getReference().child("Creaters").child(model.getNotificationBy()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        CreatersModel creatersModel = snapshot.getValue(CreatersModel.class);
                        Picasso.get().load(creatersModel.getProfileImage()).placeholder(R.drawable.profileuser).into(holder.binding.profile);
                        holder.binding.NotificationText.setText(creatersModel.getName() + " " + model.getNotificationText());
                    } else {
                        FirebaseDatabase.getInstance().getReference().child("Users").child(model.getNotificationBy()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                UsersModel usersModel = snapshot.getValue(UsersModel.class);
                                holder.binding.NotificationText.setText(usersModel.getUserName() + " " + model.getNotificationText());
                                holder.binding.profile.setImageResource(R.drawable.profileuser);
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
        if (model.getNotificationType().equals("New Video")) {
            FirebaseDatabase.getInstance().getReference().child("Creaters").child(model.getNotificationBy()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    CreatersModel creatersModel = snapshot.getValue(CreatersModel.class);
                    Picasso.get().load(creatersModel.getProfileImage()).placeholder(R.drawable.profileuser).into(holder.binding.profile);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            holder.binding.NotificationText.setText(model.getNotificationText());
        }
        //On click event
        holder.binding.clickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (model.getNotificationType().equals("Subscribers")) {
                    Intent intent = new Intent(context, CommonActivity.class);
                    intent.putExtra("data", "Creater");
                    FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("Notifications").child(model.getNotificationId()).child("notificationOpened").setValue(true);
                    context.startActivity(intent);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("Notifications").child(model.getNotificationId()).child("notificationOpened").setValue(true);
                    Intent intent = new Intent(context, NotificationVideoViewActivity.class);
                    String id = model.getVideoId().toString();
                    intent.putExtra("OnNotificationOpened", id);
                    context.startActivity(intent);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        NotificationRecyclerViewBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = NotificationRecyclerViewBinding.bind(itemView);
        }
    }
}
