package com.example.shivambhardwaj.shitube.Fragments;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        binding.historyshimmerFrameLayout.startShimmer();
        binding.profiledataShimmer.startShimmer();

        binding.search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                Toast.makeText(getContext(), "Signed Out", Toast.LENGTH_SHORT).show();
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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        linearLayoutManager.setStackFromEnd(false);
        binding.recyclerHistory.setLayoutManager(linearLayoutManager);
        binding.recyclerHistory.setAdapter(adapter);
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
                    } else {
                        binding.viewmoreHistory.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Snackbar snackbar = Snackbar.make(binding.getRoot(), "No History Found", Snackbar.LENGTH_SHORT);
                                snackbar.setBackgroundTint(getResources().getColor(R.color.white));
                                snackbar.setTextColor(getResources().getColor(R.color.black));
                                snackbar.show();
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
                    else if (dataSnapshot.child("watchLater").child(FirebaseAuth.getInstance().getUid()).exists()) {
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