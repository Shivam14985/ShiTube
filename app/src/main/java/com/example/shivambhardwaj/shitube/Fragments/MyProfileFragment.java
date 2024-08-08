package com.example.shivambhardwaj.shitube.Fragments;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.shivambhardwaj.shitube.Activities.CommonActivity;
import com.example.shivambhardwaj.shitube.Activities.LoginActivity;
import com.example.shivambhardwaj.shitube.Adapters.HistoryAdapter;
import com.example.shivambhardwaj.shitube.Models.CreatersModel;
import com.example.shivambhardwaj.shitube.Models.UsersModel;
import com.example.shivambhardwaj.shitube.Models.VideoModel;
import com.example.shivambhardwaj.shitube.R;
import com.example.shivambhardwaj.shitube.databinding.FragmentMyProfileBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyProfileFragment extends Fragment {
    FragmentMyProfileBinding binding;
    FirebaseDatabase database;
    Uri uri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMyProfileBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();

        database = FirebaseDatabase.getInstance();
        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {

        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {

        }

        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/shitube-173fe.appspot.com/o/Icons%20For%20App%2Finstagram.png?alt=media&token=6848c167-4e12-4840-8ad8-51e4b237118e").into(binding.Instagram);
        binding.Instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.instagram.com/shivambhardwaj26_07/"));
                startActivity(intent);
            }
        });
        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/shitube-173fe.appspot.com/o/Icons%20For%20App%2Ftwitter.png?alt=media&token=c42789e9-f329-4ce7-8e12-6f47c9734ab0").into(binding.TwitterImage);
        binding.TwitterImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://x.com/ShivamB93657571"));
                startActivity(intent);
            }
        });
        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/shitube-173fe.appspot.com/o/Icons%20For%20App%2Fgithub.png?alt=media&token=44bb65a2-34ab-4816-90a8-40a323eaec4a").into(binding.Github);
        binding.Github.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://github.com/Shivam14985"));
                startActivity(intent);
            }
        });
        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/shitube-173fe.appspot.com/o/Icons%20For%20App%2Fgmail.png?alt=media&token=3e0203ba-f21e-4354-b459-a391173c805f").into(binding.Mail);
        binding.Mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "bhardwajshivam667@gmail.com" });
                startActivity(Intent.createChooser(intent, null));
            }
        });
        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/shitube-173fe.appspot.com/o/Icons%20For%20App%2Flinkedin_logo.png?alt=media&token=d1cb517f-19d2-4259-a990-a57d93a3f2cf").into(binding.LinkedIn);
        binding.LinkedIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.linkedin.com/in/shivam-bhardwaj-348491270/"));
                startActivity(Intent.createChooser(intent,""));
            }
        });
        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/shitube-173fe.appspot.com/o/Icons%20For%20App%2Finternet.png?alt=media&token=fb524c41-b569-46ea-ba8a-b163e8ca8b6a").into(binding.Website);
        binding.Website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://shivam14985.github.io/PortPholio/"));
                startActivity(intent);
            }
        });

        binding.historyshimmerFrameLayout.startShimmer();
        binding.profiledataShimmer.startShimmer();

        binding.moreOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getContext(), binding.moreOption);
                popupMenu.getMenuInflater().inflate(R.menu.more_profile_options_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.logout) {
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(getContext(), LoginActivity.class);
                            startActivity(intent);
                            Toast.makeText(getContext(), "Signed Out", Toast.LENGTH_SHORT).show();
                        }
                        if (item.getItemId() == R.id.shareApp) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_TEXT, "Download ShiTube " + "https://drive.google.com/drive/folders/1oaddC4CTr8IcrOjOLR24TXi5dMGkGE_j");
                            intent.setType("text/plain");
                            startActivity(Intent.createChooser(intent, "Share Via"));
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
        binding.becomeCreater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent();
            }
        });
        binding.becomecreterimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent();
            }
        });

        //Show History
        ArrayList list = new ArrayList();
        HistoryAdapter adapter = new HistoryAdapter(list, getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, true);
        linearLayoutManager.setStackFromEnd(true);
        binding.recyclerHistory.setLayoutManager(linearLayoutManager);
        binding.recyclerHistory.setAdapter(adapter);

        //History
        database.getReference().child("Videos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.child("viewedBy").child(FirebaseAuth.getInstance().getUid()).exists()) {
                        VideoModel model = dataSnapshot.getValue(VideoModel.class);
                        list.add(model);
                        binding.historyshimmerFrameLayout.stopShimmer();
                        binding.ShimmerScrollView.setVisibility(View.GONE);
                        binding.viewmoreHistory.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(), CommonActivity.class);
                                intent.putExtra("data", "moreHistory");
                                startActivity(intent);
                            }
                        });
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Liked Video Poster
        database.getReference().child("Videos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.child("likedBy").child(FirebaseAuth.getInstance().getUid()).exists()) {
                        VideoModel model = dataSnapshot.getValue(VideoModel.class);
                        Picasso.get().load(model.getThumbnail()).into(binding.posterliked);
                        binding.LikedVideos.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(), CommonActivity.class);
                                intent.putExtra("data", "likedVideos");
                                startActivity(intent);
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Wtach Later Video Poster
        database.getReference().child("Videos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.child("watchLater").child(FirebaseAuth.getInstance().getUid()).exists()) {
                        VideoModel model = dataSnapshot.getValue(VideoModel.class);
                        Picasso.get().load(model.getThumbnail()).into(binding.posterwatchlater);
                        binding.watcjLAterVideos.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(), CommonActivity.class);
                                intent.putExtra("data", "watchLaterVideos");
                                startActivity(intent);
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        database.getReference().child("Creaters").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    CreatersModel model = snapshot.getValue(CreatersModel.class);
                    binding.channelName.setText(model.getName());
                    binding.UserName.setText(model.getUserName());
                    Picasso.get().load(model.getProfileImage()).placeholder(R.drawable.profileuser).into(binding.ProfileImage);
                    binding.becomeCreater.setText("view channel");
                    binding.UserName.setVisibility(View.VISIBLE);
                    binding.profiledataShimmer.setVisibility(View.GONE);
                    binding.profiledataLAyout.setVisibility(View.VISIBLE);
                } else {
                    binding.becomeCreater.setText("Become Creater");
                    database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            UsersModel model = snapshot.getValue(UsersModel.class);
                            binding.channelName.setText(model.getUserName());
                            binding.profiledataShimmer.setVisibility(View.GONE);
                            binding.profiledataLAyout.setVisibility(View.VISIBLE);
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

    private void intent() {
        Intent intent = new Intent(getContext(), CommonActivity.class);
        intent.putExtra("data", "Creater");
        startActivity(intent);
    }

    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        int orientation = newConfig.orientation;
        handleOrientationChange(orientation);
    }

    private void handleOrientationChange(int orientation) {
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {

        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {

        }
    }
}