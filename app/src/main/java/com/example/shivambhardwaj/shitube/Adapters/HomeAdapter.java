package com.example.shivambhardwaj.shitube.Adapters;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shivambhardwaj.shitube.Activities.CommonActivity;
import com.example.shivambhardwaj.shitube.Activities.OthersProfileViewActivity;
import com.example.shivambhardwaj.shitube.Activities.ViewVideoActivity;
import com.example.shivambhardwaj.shitube.Models.CreatersModel;
import com.example.shivambhardwaj.shitube.Models.VideoModel;
import com.example.shivambhardwaj.shitube.R;
import com.example.shivambhardwaj.shitube.databinding.HomeRecyclerViewBinding;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.viewHolder> {
    ArrayList<VideoModel> list;
    Context context;
    private DownloadManager downloadManager;

    public HomeAdapter(ArrayList<VideoModel> list, Context context) {
        this.list = list;
        this.context = context;

    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_recycler_view, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        VideoModel model = list.get(position);
        String time = TimeAgo.using(model.getAddedAt());
        holder.binding.VideoTitle.setText(model.getTitle());
        Picasso.get().load(model.getThumbnail()).into(holder.binding.ThumnailImage);
        // Calculate hours, minutes, and seconds
        long milliseconds = model.getDuration(); // Example milliseconds
        // Calculate hours, minutes, and seconds
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) ((milliseconds % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) ((milliseconds % (1000 * 60)) / 1000);
        if (hours == 00) {
            String formattedTime = String.format("%02d:%02d", minutes, seconds);
            holder.binding.duration.setText(formattedTime);
        } else {
            String formattedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
            holder.binding.duration.setText(formattedTime);
        }
        //Click To View Video
        holder.binding.Recyclerclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewVideoActivity.class);
                intent.putExtra("VideoContent", list.get(position));  // Use model directly
                context.startActivity(intent);
                FirebaseDatabase.getInstance().getReference().child("Videos").child(model.getPostId()).child("viewedBy").child(FirebaseAuth.getInstance().getUid()).setValue("true");
                FirebaseDatabase.getInstance().getReference().child("Videos").child(model.getPostId()).child("viewsCount").setValue(model.getViewsCount() + 1);

            }
        });
        holder.binding.moreotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, holder.binding.moreotion);
                popupMenu.getMenuInflater().inflate(R.menu.home_video_more_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.Download) {
                            Uri uri = Uri.parse(model.getVideo());
                            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                            DownloadManager.Request request = new DownloadManager.Request(uri);
                            request.setTitle(model.getTitle());
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "filename.jpg");
                            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                            request.setAllowedOverRoaming(false);
                            downloadManager.enqueue(request);
                            Toast.makeText(context, "Downloading", Toast.LENGTH_SHORT).show();
//                            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
//                            DownloadManager.Request request = new DownloadManager.Request(uri);
//                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "filename.jpg");
//                            downloadManager.enqueue(request);
//                            Toast.makeText(context, "Downloading", Toast.LENGTH_SHORT).show();
                        }
                        if (item.getItemId() == R.id.Share) {
                            String uri = model.getVideo();
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_TEXT, model.getTitle() + "\n" + uri);
                            intent.setType("text/plain");
                            context.startActivity(Intent.createChooser(intent, "Share Via"));
                        }
                        if (item.getItemId() == R.id.save) {
                            FirebaseDatabase.getInstance().getReference().child("Videos").child(model.getPostId()).child("watchLater").child(FirebaseAuth.getInstance().getUid()).setValue("true");
                            Toast.makeText(context, "Video Saved", Toast.LENGTH_SHORT).show();

                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
        holder.binding.KnowMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (model.getAddedBy().equals(FirebaseAuth.getInstance().getUid())){
                    Intent intent = new Intent(context, CommonActivity.class);
                    intent.putExtra("data", "Creater");
                    context.startActivity(intent);
                }
                else {
                    Intent intent = new Intent(context, OthersProfileViewActivity.class);
                    String addedBy=model.getAddedBy().toString();
                    intent.putExtra("channelId",addedBy );
                    context.startActivity(intent);
                }
            }
        });
        FirebaseDatabase.getInstance().getReference().child("Creaters").child(model.getAddedBy()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                CreatersModel model1 = snapshot.getValue(CreatersModel.class);
                Picasso.get().load(model1.getProfileImage()).placeholder(R.drawable.profileuser).into(holder.binding.channelImage);
                int views = model.getViewsCount();
                if (views > 999 & 100000 > views) {
                    double converted = (double) views / 1000;
                    DecimalFormat form = new DecimalFormat("0.0");
                    holder.binding.userName.setText(model1.getName() + " • " + form.format(converted) + "k views • " + time);
                }
                if (views > 99999 & views < 1000000) {
                    double converted = (double) views / 1000;
                    DecimalFormat form = new DecimalFormat("0");
                    holder.binding.userName.setText(model1.getName() + " • " + form.format(converted) + "k views • " + time);
                }
                if (views < 1000) {
                    holder.binding.userName.setText(model1.getName() + " • " + model.getViewsCount() + " views • " + time);
                }
                if (views > 999999) {
                    double converted = (double) views / 1000000;
                    DecimalFormat form = new DecimalFormat("0.0");
                    holder.binding.userName.setText(model1.getName() + " • " + form.format(converted) + "M views • " + time);
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
        HomeRecyclerViewBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = HomeRecyclerViewBinding.bind(itemView);
        }
    }
}
