package com.example.shivambhardwaj.shitube.Adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shivambhardwaj.shitube.Activities.CommonActivity;
import com.example.shivambhardwaj.shitube.Activities.OthersProfileViewActivity;
import com.example.shivambhardwaj.shitube.Fragments.BottomSheetFragment;
import com.example.shivambhardwaj.shitube.Models.CreatersModel;
import com.example.shivambhardwaj.shitube.Models.VideoModel;
import com.example.shivambhardwaj.shitube.R;
import com.example.shivambhardwaj.shitube.databinding.ShortsVideoRecyclerDesignBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ShortsAdapter extends RecyclerView.Adapter<ShortsAdapter.vieHolder> {
    ArrayList<VideoModel> list;
    Context context;
    FirebaseDatabase database;


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
        database = FirebaseDatabase.getInstance();
        Uri uri = Uri.parse(model.getVideo());
        holder.binding.videoview.setVideoURI(uri);
        holder.binding.likecount.setText(model.getLikesCount() + "");
        holder.binding.commentcount.setText(model.getCommentsCount() + "");
        holder.binding.Title.setText(model.getTitle());
        FirebaseDatabase.getInstance().getReference().child("Creaters").child(model.getAddedBy()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                CreatersModel creatersModel = snapshot.getValue(CreatersModel.class);
                holder.binding.Name.setText(creatersModel.getName());
                Picasso.get().load(creatersModel.getProfileImage()).into(holder.binding.Profile);
                if (snapshot.child("admin").getValue().toString().equals("true")){
                    holder.binding.verifiedMark.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.binding.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(model.getVideo());
//                Intent i = new Intent(Intent.ACTION_VIEW);
//                i.setData(Uri.parse(String.valueOf(uri)));
//                context.startActivity(i);
                DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setTitle(model.getTitle());
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "filename.jpg");
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                request.setAllowedOverRoaming(false);
                downloadManager.enqueue(request);
                Toast.makeText(context, "Downloading", Toast.LENGTH_SHORT).show();
            }
        });
        //click on like
        database.getReference().child("Shorts").child(model.getPostId()).child("likedBy").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    holder.binding.like.setImageResource(R.drawable.liked);
                    holder.binding.like.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int dislekes = model.getLikesCount() - 1;
                            model.setLikesCount(dislekes);
                            database.getReference().child("Shorts").child(model.getPostId()).child("likesCount").setValue(dislekes);
                            database.getReference().child("Shorts").child(model.getPostId()).child("likedBy").child(FirebaseAuth.getInstance().getUid()).removeValue();
                            holder.binding.like.setImageResource(R.drawable.like);

                            database.getReference().child("Shorts").child(model.getPostId()).child("likesCount").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String likes = snapshot.getValue().toString();
                                    holder.binding.likecount.setText(likes);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    });
                } else {
                    holder.binding.like.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int likes = model.getLikesCount() + 1;
                            model.setLikesCount(likes);
                            holder.binding.likedBlastAnimation.setVisibility(View.VISIBLE);
                            holder.binding.likedBlastAnimation.playAnimation();
                            holder.binding.likedBlastAnimation.addAnimatorListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(@NonNull Animator animation, boolean isReverse) {
                                    super.onAnimationEnd(animation, isReverse);
                                    holder.binding.likedBlastAnimation.setVisibility(View.INVISIBLE);
                                }
                            });
                            database.getReference().child("Shorts").child(model.getPostId()).child("likesCount").setValue(likes);
                            database.getReference().child("Shorts").child(model.getPostId()).child("likedBy").child(FirebaseAuth.getInstance().getUid()).setValue(true);
                            holder.binding.like.setImageResource(R.drawable.liked);
                            database.getReference().child("Shorts").child(model.getPostId()).child("likesCount").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String likes = snapshot.getValue().toString();
                                    holder.binding.likecount.setText(likes);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.binding.Share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = model.getVideo();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, model.getTitle() + "\n" + uri);
                intent.setType("text/plain");
                context.startActivity(Intent.createChooser(intent, "Share Via"));

            }
        });
        holder.binding.videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                holder.binding.progressBar3.setVisibility(View.GONE);
               }
        });
        holder.binding.main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.binding.videoview.isPlaying()) {
                    holder.binding.videoview.pause();
                    holder.binding.pause.setVisibility(View.GONE);
                    holder.binding.play.setVisibility(View.VISIBLE);
                    holder.binding.play.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            holder.binding.play.setVisibility(View.GONE);
                        }
                    }, 2000);
                } else {
                    holder.binding.videoview.start();
                    holder.binding.pause.setVisibility(View.VISIBLE);
                    holder.binding.play.setVisibility(View.GONE);
                    holder.binding.pause.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            holder.binding.pause.setVisibility(View.GONE);
                        }
                    }, 2000);
                }
            }
        });
        holder.binding.videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.start();
                }
        });

        holder.binding.KnowMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (model.getAddedBy().equals(FirebaseAuth.getInstance().getUid())) {
                    Intent intent = new Intent(context, CommonActivity.class);
                    intent.putExtra("data", "Creater");
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, OthersProfileViewActivity.class);
                    String addedBy = model.getAddedBy().toString();
                    intent.putExtra("channelId", addedBy);
                    context.startActivity(intent);
                }
            }
        });
        holder.binding.Comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle mBundle = new Bundle();
                mBundle.putString("shortComments", model.getPostId());
                BottomSheetFragment bottomSheetDialogFragment = new BottomSheetFragment();
                bottomSheetDialogFragment.setArguments(mBundle);
                bottomSheetDialogFragment.show(((FragmentActivity) v.getContext()).getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
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
