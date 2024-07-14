package com.example.shivambhardwaj.shitube.Fragments;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.shivambhardwaj.shitube.Activities.CommonActivity;
import com.example.shivambhardwaj.shitube.Activities.NotificationActivity;
import com.example.shivambhardwaj.shitube.Activities.SearchActivity;
import com.example.shivambhardwaj.shitube.Adapters.HomeAdapter;
import com.example.shivambhardwaj.shitube.Adapters.LandscapeHomeAdapter;
import com.example.shivambhardwaj.shitube.Models.NotificationModel;
import com.example.shivambhardwaj.shitube.Models.VideoModel;
import com.example.shivambhardwaj.shitube.R;
import com.example.shivambhardwaj.shitube.databinding.FragmentHomeBinding;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    FragmentHomeBinding binding;
    FirebaseDatabase firebaseDatabase;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();


        firebaseDatabase = FirebaseDatabase.getInstance();

        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            fetchingData();
            binding.ShimmerHome.startShimmer();
            binding.wanthide.setVisibility(View.VISIBLE);
            binding.landscapesimmer.setVisibility(View.GONE);
            binding.HorizontalScrollView.setVisibility(View.VISIBLE);
            binding.adView.setVisibility(View.VISIBLE);
            binding.SwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    binding.SwipeRefreshLayout.setRefreshing(false);
                    binding.wanthide.setVisibility(View.VISIBLE);
                    binding.ShimmerHome.startShimmer();
                    binding.Autos.setTextColor(getContext().getResources().getColor(R.color.black));
                    binding.Autos.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                    binding.All.setTextColor(getContext().getResources().getColor(R.color.white));
                    binding.All.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF000000")));
                    binding.blogs.setTextColor(getContext().getResources().getColor(R.color.black));
                    binding.blogs.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                    binding.education.setTextColor(getContext().getResources().getColor(R.color.black));
                    binding.education.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                    binding.Comedy.setTextColor(getContext().getResources().getColor(R.color.black));
                    binding.Comedy.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                    binding.Films.setTextColor(getContext().getResources().getColor(R.color.black));
                    binding.Films.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                    binding.Gaming.setTextColor(getContext().getResources().getColor(R.color.black));
                    binding.Gaming.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                    binding.Style.setTextColor(getContext().getResources().getColor(R.color.black));
                    binding.Style.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                    binding.news.setTextColor(getContext().getResources().getColor(R.color.black));
                    binding.news.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                    binding.nonprofits.setTextColor(getContext().getResources().getColor(R.color.black));
                    binding.nonprofits.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                    binding.music.setTextColor(getContext().getResources().getColor(R.color.black));
                    binding.music.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                    binding.science.setTextColor(getContext().getResources().getColor(R.color.black));
                    binding.science.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                    binding.animals.setTextColor(getContext().getResources().getColor(R.color.black));
                    binding.animals.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                    binding.sport.setTextColor(getContext().getResources().getColor(R.color.black));
                    binding.sport.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                    binding.Travel.setTextColor(getContext().getResources().getColor(R.color.black));
                    binding.Travel.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                    binding.entertainment.setTextColor(getContext().getResources().getColor(R.color.black));
                    binding.entertainment.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));

                    fetchingData();
                }
            });
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            fetchingDataInLandscape();
            binding.HorizontalScrollView.setVisibility(View.GONE);
            binding.landscapesimmer.setVisibility(View.VISIBLE);
            binding.adView.setVisibility(View.GONE);
            binding.SwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    binding.SwipeRefreshLayout.setRefreshing(false);
                    fetchingDataInLandscape();
                }
            });
        }

        //loadBannerAds();
        loadBannerAds();
        firebaseDatabase.getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("Notifications").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    NotificationModel notificationModel = dataSnapshot.getValue(NotificationModel.class);
                    String opened = String.valueOf(notificationModel.isNotificationOpened());
                    if (opened.equals("false")) {
                        binding.notificationCount.setVisibility(View.VISIBLE);

                    } else {
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        firebaseDatabase.getReference().child("Creaters").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String admin = snapshot.child("admin").getValue().toString();
                    if (admin.equals("true")) {
                        firebaseDatabase.getReference().child("Videos").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    String approved = dataSnapshot.child("approved").getValue().toString();
                                    if (approved.equals("false")) {
                                        binding.approvVideo.setVisibility(View.VISIBLE);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } else {

                    }
                } else {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.approvVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CommonActivity.class);
                intent.putExtra("data", "Review");
                startActivity(intent);
            }
        });

        binding.search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                startActivity(intent);
            }
        });
        binding.notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NotificationActivity.class);
                startActivity(intent);
            }
        });
        binding.Autos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.wanthide.setVisibility(View.VISIBLE);
                binding.ShimmerHome.startShimmer();
                binding.All.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.All.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Autos.setTextColor(getContext().getResources().getColor(R.color.white));
                binding.Autos.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF000000")));
                binding.blogs.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.blogs.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.education.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.education.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Comedy.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Comedy.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Films.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Films.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Gaming.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Gaming.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Style.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Style.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.news.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.news.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.nonprofits.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.nonprofits.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.music.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.music.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.science.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.science.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.animals.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.animals.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.sport.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.sport.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Travel.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Travel.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.entertainment.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.entertainment.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));

                ArrayList<VideoModel> list = new ArrayList();
                HomeAdapter adapter = new HomeAdapter(list, getContext());
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
                linearLayoutManager.setStackFromEnd(true);
                binding.RecyclerView.setAdapter(adapter);
                binding.RecyclerView.setLayoutManager(linearLayoutManager);
                firebaseDatabase.getReference().child("Videos").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            VideoModel model = dataSnapshot.getValue(VideoModel.class);
                            String category = dataSnapshot.child("category").getValue(String.class);
                            if (category.equals("Autos and Vehicles")) {
                                list.add(model);
                                binding.wanthide.setVisibility(View.GONE);
                                binding.ShimmerHome.stopShimmer();
                            } else {
                                binding.wanthide.setVisibility(View.GONE);
                                binding.ShimmerHome.stopShimmer();
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
        binding.All.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.wanthide.setVisibility(View.VISIBLE);
                binding.ShimmerHome.startShimmer();
                binding.Autos.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Autos.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.All.setTextColor(getContext().getResources().getColor(R.color.white));
                binding.All.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF000000")));
                binding.blogs.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.blogs.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.education.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.education.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Comedy.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Comedy.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Films.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Films.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Gaming.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Gaming.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Style.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Style.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.news.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.news.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.nonprofits.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.nonprofits.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.music.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.music.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.science.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.science.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.animals.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.animals.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.sport.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.sport.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Travel.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Travel.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.entertainment.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.entertainment.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));

                fetchingData();
            }
        });
        binding.entertainment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.wanthide.setVisibility(View.VISIBLE);
                binding.ShimmerHome.startShimmer();
                binding.All.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.All.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Autos.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Autos.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.blogs.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.blogs.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.education.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.education.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Comedy.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Comedy.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Films.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Films.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Gaming.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Gaming.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Style.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Style.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.news.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.news.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.nonprofits.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.nonprofits.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.music.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.music.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.science.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.science.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.animals.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.animals.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.sport.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.sport.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Travel.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Travel.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.entertainment.setTextColor(getContext().getResources().getColor(R.color.white));
                binding.entertainment.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF000000")));

                ArrayList<VideoModel> list = new ArrayList();
                HomeAdapter adapter = new HomeAdapter(list, getContext());
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
                linearLayoutManager.setStackFromEnd(true);
                binding.RecyclerView.setAdapter(adapter);
                binding.RecyclerView.setLayoutManager(linearLayoutManager);
                firebaseDatabase.getReference().child("Videos").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            VideoModel model = dataSnapshot.getValue(VideoModel.class);
                            String category = dataSnapshot.child("category").getValue(String.class);
                            if (category.equals("Entertainment")) {
                                list.add(model);
                                binding.wanthide.setVisibility(View.GONE);
                                binding.ShimmerHome.stopShimmer();
                            } else {
                                binding.wanthide.setVisibility(View.GONE);
                                binding.ShimmerHome.stopShimmer();
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
        binding.Comedy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.wanthide.setVisibility(View.VISIBLE);
                binding.ShimmerHome.startShimmer();
                binding.All.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.All.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Autos.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Autos.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.blogs.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.blogs.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.education.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.education.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Comedy.setTextColor(getContext().getResources().getColor(R.color.white));
                binding.Comedy.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF000000")));
                binding.Films.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Films.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Gaming.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Gaming.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Style.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Style.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.news.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.news.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.nonprofits.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.nonprofits.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.music.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.music.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.science.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.science.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.animals.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.animals.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.sport.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.sport.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Travel.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Travel.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.entertainment.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.entertainment.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));

                ArrayList<VideoModel> list = new ArrayList();
                HomeAdapter adapter = new HomeAdapter(list, getContext());
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
                linearLayoutManager.setStackFromEnd(true);
                binding.RecyclerView.setAdapter(adapter);
                binding.RecyclerView.setLayoutManager(linearLayoutManager);
                firebaseDatabase.getReference().child("Videos").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            VideoModel model = dataSnapshot.getValue(VideoModel.class);
                            String category = dataSnapshot.child("category").getValue(String.class);
                            if (category.equals("Comedy")) {
                                list.add(model);
                                binding.wanthide.setVisibility(View.GONE);
                                binding.ShimmerHome.stopShimmer();
                            } else {
                                binding.wanthide.setVisibility(View.GONE);
                                binding.ShimmerHome.stopShimmer();
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
        binding.education.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.wanthide.setVisibility(View.VISIBLE);
                binding.ShimmerHome.startShimmer();
                binding.All.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.All.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Autos.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Autos.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.blogs.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.blogs.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.education.setTextColor(getContext().getResources().getColor(R.color.white));
                binding.education.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF000000")));
                binding.Comedy.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Comedy.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Films.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Films.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Gaming.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Gaming.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Style.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Style.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.news.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.news.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.nonprofits.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.nonprofits.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.music.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.music.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.science.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.science.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.animals.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.animals.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.sport.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.sport.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Travel.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Travel.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.entertainment.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.entertainment.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));

                ArrayList<VideoModel> list = new ArrayList();
                HomeAdapter adapter = new HomeAdapter(list, getContext());
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
                linearLayoutManager.setStackFromEnd(true);
                binding.RecyclerView.setAdapter(adapter);
                binding.RecyclerView.setLayoutManager(linearLayoutManager);
                firebaseDatabase.getReference().child("Videos").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            VideoModel model = dataSnapshot.getValue(VideoModel.class);
                            String category = dataSnapshot.child("category").getValue(String.class);
                            if (category.equals("Education")) {
                                list.add(model);
                                binding.wanthide.setVisibility(View.GONE);
                                binding.ShimmerHome.stopShimmer();
                            } else {
                                binding.wanthide.setVisibility(View.GONE);
                                binding.ShimmerHome.stopShimmer();
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
        binding.Films.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.wanthide.setVisibility(View.VISIBLE);
                binding.ShimmerHome.startShimmer();
                binding.All.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.All.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Autos.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Autos.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.blogs.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.blogs.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.education.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.education.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Comedy.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Comedy.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Films.setTextColor(getContext().getResources().getColor(R.color.white));
                binding.Films.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF000000")));
                binding.Gaming.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Gaming.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Style.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Style.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.news.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.news.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.nonprofits.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.nonprofits.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.music.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.music.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.science.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.science.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.animals.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.animals.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.sport.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.sport.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Travel.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Travel.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.entertainment.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.entertainment.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));

                ArrayList<VideoModel> list = new ArrayList();
                HomeAdapter adapter = new HomeAdapter(list, getContext());
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
                linearLayoutManager.setStackFromEnd(true);
                binding.RecyclerView.setAdapter(adapter);
                binding.RecyclerView.setLayoutManager(linearLayoutManager);
                firebaseDatabase.getReference().child("Videos").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            VideoModel model = dataSnapshot.getValue(VideoModel.class);
                            String category = dataSnapshot.child("category").getValue(String.class);
                            if (category.equals("Films and Animation")) {
                                list.add(model);
                                binding.wanthide.setVisibility(View.GONE);
                                binding.ShimmerHome.stopShimmer();
                            } else {
                                binding.wanthide.setVisibility(View.GONE);
                                binding.ShimmerHome.stopShimmer();
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
        binding.Gaming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.wanthide.setVisibility(View.VISIBLE);
                binding.ShimmerHome.startShimmer();
                binding.All.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.All.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Autos.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Autos.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.blogs.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.blogs.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.education.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.education.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Comedy.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Comedy.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Films.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Films.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Gaming.setTextColor(getContext().getResources().getColor(R.color.white));
                binding.Gaming.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF000000")));
                binding.Style.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Style.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.news.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.news.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.nonprofits.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.nonprofits.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.music.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.music.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.science.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.science.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.animals.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.animals.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.sport.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.sport.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Travel.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Travel.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.entertainment.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.entertainment.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));

                ArrayList<VideoModel> list = new ArrayList();
                HomeAdapter adapter = new HomeAdapter(list, getContext());
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
                linearLayoutManager.setStackFromEnd(true);
                binding.RecyclerView.setAdapter(adapter);
                binding.RecyclerView.setLayoutManager(linearLayoutManager);
                firebaseDatabase.getReference().child("Videos").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            VideoModel model = dataSnapshot.getValue(VideoModel.class);
                            String category = dataSnapshot.child("category").getValue(String.class);
                            if (category.equals("Gaming")) {
                                list.add(model);
                                binding.wanthide.setVisibility(View.GONE);
                                binding.ShimmerHome.stopShimmer();
                            } else {
                                binding.wanthide.setVisibility(View.GONE);
                                binding.ShimmerHome.stopShimmer();
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
        binding.Style.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.wanthide.setVisibility(View.VISIBLE);
                binding.ShimmerHome.startShimmer();
                binding.All.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.All.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Autos.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Autos.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.blogs.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.blogs.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.education.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.education.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Comedy.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Comedy.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Films.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Films.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Gaming.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Gaming.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Style.setTextColor(getContext().getResources().getColor(R.color.white));
                binding.Style.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF000000")));
                binding.news.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.news.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.nonprofits.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.nonprofits.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.music.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.music.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.science.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.science.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.animals.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.animals.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.sport.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.sport.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Travel.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Travel.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.entertainment.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.entertainment.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));

                ArrayList<VideoModel> list = new ArrayList();
                HomeAdapter adapter = new HomeAdapter(list, getContext());
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
                linearLayoutManager.setStackFromEnd(true);
                binding.RecyclerView.setAdapter(adapter);
                binding.RecyclerView.setLayoutManager(linearLayoutManager);
                firebaseDatabase.getReference().child("Videos").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            VideoModel model = dataSnapshot.getValue(VideoModel.class);
                            String category = dataSnapshot.child("category").getValue(String.class);
                            if (category.equals("Howto & Style")) {
                                list.add(model);
                                binding.wanthide.setVisibility(View.GONE);
                                binding.ShimmerHome.stopShimmer();
                            } else {
                                binding.wanthide.setVisibility(View.GONE);
                                binding.ShimmerHome.stopShimmer();
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
        binding.news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.wanthide.setVisibility(View.VISIBLE);
                binding.ShimmerHome.startShimmer();
                binding.All.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.All.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Autos.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Autos.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.blogs.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.blogs.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.education.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.education.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Comedy.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Comedy.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Films.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Films.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Gaming.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Gaming.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Style.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Style.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.news.setTextColor(getContext().getResources().getColor(R.color.white));
                binding.news.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF000000")));
                binding.nonprofits.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.nonprofits.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.music.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.music.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.science.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.science.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.animals.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.animals.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.sport.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.sport.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Travel.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Travel.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.entertainment.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.entertainment.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));

                ArrayList<VideoModel> list = new ArrayList();
                HomeAdapter adapter = new HomeAdapter(list, getContext());
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
                linearLayoutManager.setStackFromEnd(true);
                binding.RecyclerView.setAdapter(adapter);
                binding.RecyclerView.setLayoutManager(linearLayoutManager);
                firebaseDatabase.getReference().child("Videos").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            VideoModel model = dataSnapshot.getValue(VideoModel.class);
                            String category = dataSnapshot.child("category").getValue(String.class);
                            if (category.equals("News & Politics")) {
                                list.add(model);
                                binding.wanthide.setVisibility(View.GONE);
                                binding.ShimmerHome.stopShimmer();
                            } else {
                                binding.wanthide.setVisibility(View.GONE);
                                binding.ShimmerHome.stopShimmer();
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
        binding.nonprofits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.wanthide.setVisibility(View.VISIBLE);
                binding.ShimmerHome.startShimmer();
                binding.All.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.All.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Autos.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Autos.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.blogs.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.blogs.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.education.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.education.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Comedy.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Comedy.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Films.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Films.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Gaming.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Gaming.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Style.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Style.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.news.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.news.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.nonprofits.setTextColor(getContext().getResources().getColor(R.color.white));
                binding.nonprofits.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF000000")));
                binding.music.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.music.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.science.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.science.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.animals.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.animals.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.sport.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.sport.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Travel.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Travel.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.entertainment.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.entertainment.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));

                ArrayList<VideoModel> list = new ArrayList();
                HomeAdapter adapter = new HomeAdapter(list, getContext());
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
                linearLayoutManager.setStackFromEnd(true);
                binding.RecyclerView.setAdapter(adapter);
                binding.RecyclerView.setLayoutManager(linearLayoutManager);
                firebaseDatabase.getReference().child("Videos").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            VideoModel model = dataSnapshot.getValue(VideoModel.class);
                            String category = dataSnapshot.child("category").getValue(String.class);
                            if (category.equals("Nonprofits & Activism")) {
                                list.add(model);
                                binding.wanthide.setVisibility(View.GONE);
                                binding.ShimmerHome.stopShimmer();
                            } else {
                                binding.wanthide.setVisibility(View.GONE);
                                binding.ShimmerHome.stopShimmer();
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
        binding.music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.wanthide.setVisibility(View.VISIBLE);
                binding.ShimmerHome.startShimmer();
                binding.All.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.All.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Autos.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Autos.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.blogs.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.blogs.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.education.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.education.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Comedy.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Comedy.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Films.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Films.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Gaming.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Gaming.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Style.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Style.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.news.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.news.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.nonprofits.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.nonprofits.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.music.setTextColor(getContext().getResources().getColor(R.color.white));
                binding.music.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF000000")));
                binding.science.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.science.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.animals.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.animals.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.sport.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.sport.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Travel.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Travel.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.entertainment.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.entertainment.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));

                ArrayList<VideoModel> list = new ArrayList();
                HomeAdapter adapter = new HomeAdapter(list, getContext());
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
                linearLayoutManager.setStackFromEnd(true);
                binding.RecyclerView.setAdapter(adapter);
                binding.RecyclerView.setLayoutManager(linearLayoutManager);
                firebaseDatabase.getReference().child("Videos").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            VideoModel model = dataSnapshot.getValue(VideoModel.class);
                            String category = dataSnapshot.child("category").getValue(String.class);
                            if (category.equals("Music")) {
                                list.add(model);
                                binding.wanthide.setVisibility(View.GONE);
                                binding.ShimmerHome.stopShimmer();
                            } else {
                                binding.wanthide.setVisibility(View.GONE);
                                binding.ShimmerHome.stopShimmer();
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
        binding.blogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.wanthide.setVisibility(View.VISIBLE);
                binding.ShimmerHome.startShimmer();
                binding.All.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.All.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Autos.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Autos.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.education.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.education.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.blogs.setTextColor(getContext().getResources().getColor(R.color.white));
                binding.blogs.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF000000")));
                binding.Comedy.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Comedy.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Films.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Films.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Gaming.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Gaming.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Style.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Style.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.news.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.news.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.nonprofits.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.nonprofits.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.music.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.music.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.science.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.science.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.animals.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.animals.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.sport.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.sport.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Travel.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Travel.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.entertainment.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.entertainment.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));

                ArrayList<VideoModel> list = new ArrayList();
                HomeAdapter adapter = new HomeAdapter(list, getContext());
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
                linearLayoutManager.setStackFromEnd(true);
                binding.RecyclerView.setAdapter(adapter);
                binding.RecyclerView.setLayoutManager(linearLayoutManager);
                firebaseDatabase.getReference().child("Videos").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            VideoModel model = dataSnapshot.getValue(VideoModel.class);
                            String category = dataSnapshot.child("category").getValue(String.class);
                            if (category.equals("People & Blogs")) {
                                list.add(model);
                                binding.wanthide.setVisibility(View.GONE);
                                binding.ShimmerHome.stopShimmer();
                            } else {
                                binding.wanthide.setVisibility(View.GONE);
                                binding.ShimmerHome.stopShimmer();
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
        binding.animals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.wanthide.setVisibility(View.VISIBLE);
                binding.ShimmerHome.startShimmer();
                binding.All.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.All.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Autos.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Autos.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.blogs.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.blogs.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.animals.setTextColor(getContext().getResources().getColor(R.color.white));
                binding.animals.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF000000")));
                binding.Comedy.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Comedy.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Films.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Films.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Gaming.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Gaming.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Style.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Style.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.news.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.news.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.nonprofits.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.nonprofits.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.music.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.music.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.science.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.science.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.education.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.education.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.sport.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.sport.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Travel.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Travel.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.entertainment.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.entertainment.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));

                ArrayList<VideoModel> list = new ArrayList();
                HomeAdapter adapter = new HomeAdapter(list, getContext());
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
                linearLayoutManager.setStackFromEnd(true);
                binding.RecyclerView.setAdapter(adapter);
                binding.RecyclerView.setLayoutManager(linearLayoutManager);
                firebaseDatabase.getReference().child("Videos").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            VideoModel model = dataSnapshot.getValue(VideoModel.class);
                            String category = dataSnapshot.child("category").getValue(String.class);
                            if (category.equals("Pets & Animals")) {
                                list.add(model);
                                binding.wanthide.setVisibility(View.GONE);
                                binding.ShimmerHome.stopShimmer();
                            } else {
                                binding.wanthide.setVisibility(View.GONE);
                                binding.ShimmerHome.stopShimmer();
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
        binding.science.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.wanthide.setVisibility(View.VISIBLE);
                binding.ShimmerHome.startShimmer();
                binding.All.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.All.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Autos.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Autos.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.blogs.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.blogs.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.science.setTextColor(getContext().getResources().getColor(R.color.white));
                binding.science.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF000000")));
                binding.Comedy.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Comedy.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Films.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Films.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Gaming.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Gaming.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Style.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Style.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.news.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.news.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.nonprofits.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.nonprofits.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.music.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.music.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.education.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.education.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.animals.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.animals.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.sport.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.sport.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Travel.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Travel.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.entertainment.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.entertainment.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));

                ArrayList<VideoModel> list = new ArrayList();
                HomeAdapter adapter = new HomeAdapter(list, getContext());
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
                linearLayoutManager.setStackFromEnd(true);
                binding.RecyclerView.setAdapter(adapter);
                binding.RecyclerView.setLayoutManager(linearLayoutManager);
                firebaseDatabase.getReference().child("Videos").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            VideoModel model = dataSnapshot.getValue(VideoModel.class);
                            String category = dataSnapshot.child("category").getValue(String.class);
                            if (category.equals("Science & Technology")) {
                                list.add(model);
                                binding.wanthide.setVisibility(View.GONE);
                                binding.ShimmerHome.stopShimmer();
                            } else {
                                binding.wanthide.setVisibility(View.GONE);
                                binding.ShimmerHome.stopShimmer();
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
        binding.sport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.wanthide.setVisibility(View.VISIBLE);
                binding.ShimmerHome.startShimmer();
                binding.All.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.All.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Autos.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Autos.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.blogs.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.blogs.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.sport.setTextColor(getContext().getResources().getColor(R.color.white));
                binding.sport.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF000000")));
                binding.Comedy.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Comedy.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Films.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Films.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Gaming.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Gaming.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Style.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Style.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.news.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.news.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.nonprofits.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.nonprofits.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.music.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.music.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.science.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.science.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.animals.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.animals.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.education.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.education.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Travel.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Travel.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.entertainment.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.entertainment.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));

                ArrayList<VideoModel> list = new ArrayList();
                HomeAdapter adapter = new HomeAdapter(list, getContext());
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
                linearLayoutManager.setStackFromEnd(true);
                binding.RecyclerView.setAdapter(adapter);
                binding.RecyclerView.setLayoutManager(linearLayoutManager);
                firebaseDatabase.getReference().child("Videos").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            VideoModel model = dataSnapshot.getValue(VideoModel.class);
                            String category = dataSnapshot.child("category").getValue(String.class);
                            if (category.equals("Sports")) {
                                list.add(model);
                                binding.wanthide.setVisibility(View.GONE);
                                binding.ShimmerHome.stopShimmer();
                            } else {
                                binding.wanthide.setVisibility(View.GONE);
                                binding.ShimmerHome.stopShimmer();
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
        binding.Travel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.wanthide.setVisibility(View.VISIBLE);
                binding.ShimmerHome.startShimmer();
                binding.All.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.All.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Autos.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Autos.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.blogs.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.blogs.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Travel.setTextColor(getContext().getResources().getColor(R.color.white));
                binding.Travel.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF000000")));
                binding.Comedy.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Comedy.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Films.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Films.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Gaming.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Gaming.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.Style.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.Style.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.news.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.news.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.nonprofits.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.nonprofits.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.music.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.music.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.science.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.science.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.animals.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.animals.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.sport.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.sport.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.education.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.education.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                binding.entertainment.setTextColor(getContext().getResources().getColor(R.color.black));
                binding.entertainment.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));

                ArrayList<VideoModel> list = new ArrayList();
                HomeAdapter adapter = new HomeAdapter(list, getContext());
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
                linearLayoutManager.setStackFromEnd(true);
                binding.RecyclerView.setAdapter(adapter);
                binding.RecyclerView.setLayoutManager(linearLayoutManager);
                firebaseDatabase.getReference().child("Videos").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            VideoModel model = dataSnapshot.getValue(VideoModel.class);
                            String category = dataSnapshot.child("category").getValue(String.class);
                            if (category.equals("Travel & Events")) {
                                list.add(model);
                                binding.wanthide.setVisibility(View.GONE);
                                binding.ShimmerHome.stopShimmer();
                            } else {
                                binding.wanthide.setVisibility(View.GONE);
                                binding.ShimmerHome.stopShimmer();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        return view;
    }

    public void loadBannerAds() {
        //Ads Code
        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        binding.adView.loadAd(adRequest);
        binding.adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                loadBannerAds();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }
        });
    }

    public void fetchingData() {
        ArrayList<VideoModel> list = new ArrayList();
        HomeAdapter adapter = new HomeAdapter(list, getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
        linearLayoutManager.setStackFromEnd(true);
        binding.RecyclerView.setAdapter(adapter);
        binding.RecyclerView.setLayoutManager(linearLayoutManager);
        firebaseDatabase.getReference().child("Videos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String approved = dataSnapshot.child("approved").getValue().toString();
                    if (approved.equals("true")) {
                        VideoModel model = dataSnapshot.getValue(VideoModel.class);
                        list.add(model);
                        String id = dataSnapshot.getKey();
                        firebaseDatabase.getReference().child("Videos").child(id).child("postId").setValue(id);
                        binding.wanthide.setVisibility(View.GONE);
                        binding.ShimmerHome.stopShimmer();
                    } else {
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void fetchingDataInLandscape() {
        ArrayList<VideoModel> list = new ArrayList();
        LandscapeHomeAdapter adapter = new LandscapeHomeAdapter(list, getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
        linearLayoutManager.setStackFromEnd(true);
        binding.RecyclerView.setAdapter(adapter);
        binding.RecyclerView.setLayoutManager(linearLayoutManager);
        firebaseDatabase.getReference().child("Videos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    VideoModel model = dataSnapshot.getValue(VideoModel.class);
                    String approved = dataSnapshot.child("approved").getValue().toString();
                    if (approved.equals("true")) {
                        list.add(model);
                        String id = dataSnapshot.getKey();
                        firebaseDatabase.getReference().child("Videos").child(id).child("postId").setValue(id);
                        binding.landscapesimmer.setVisibility(View.GONE);
                        binding.landscapeLAyoutShimmer.stopShimmer();
                    } else {
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        int orientation = newConfig.orientation;
        handleOrientationChange(orientation);
    }

    private void handleOrientationChange(int orientation) {
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.HorizontalScrollView.setVisibility(View.VISIBLE);
            binding.adView.setVisibility(View.VISIBLE);
            binding.landscapesimmer.setVisibility(View.GONE);
            binding.wanthide.setVisibility(View.VISIBLE);
            // Handle portrait orientation
            fetchingData();
            binding.SwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    binding.SwipeRefreshLayout.setRefreshing(false);
                    binding.wanthide.setVisibility(View.VISIBLE);
                    binding.ShimmerHome.startShimmer();
                    binding.Autos.setTextColor(getContext().getResources().getColor(R.color.black));
                    binding.Autos.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                    binding.All.setTextColor(getContext().getResources().getColor(R.color.white));
                    binding.All.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF000000")));
                    binding.blogs.setTextColor(getContext().getResources().getColor(R.color.black));
                    binding.blogs.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                    binding.education.setTextColor(getContext().getResources().getColor(R.color.black));
                    binding.education.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                    binding.Comedy.setTextColor(getContext().getResources().getColor(R.color.black));
                    binding.Comedy.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                    binding.Films.setTextColor(getContext().getResources().getColor(R.color.black));
                    binding.Films.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                    binding.Gaming.setTextColor(getContext().getResources().getColor(R.color.black));
                    binding.Gaming.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                    binding.Style.setTextColor(getContext().getResources().getColor(R.color.black));
                    binding.Style.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                    binding.news.setTextColor(getContext().getResources().getColor(R.color.black));
                    binding.news.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                    binding.nonprofits.setTextColor(getContext().getResources().getColor(R.color.black));
                    binding.nonprofits.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                    binding.music.setTextColor(getContext().getResources().getColor(R.color.black));
                    binding.music.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                    binding.science.setTextColor(getContext().getResources().getColor(R.color.black));
                    binding.science.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                    binding.animals.setTextColor(getContext().getResources().getColor(R.color.black));
                    binding.animals.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                    binding.sport.setTextColor(getContext().getResources().getColor(R.color.black));
                    binding.sport.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                    binding.Travel.setTextColor(getContext().getResources().getColor(R.color.black));
                    binding.Travel.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                    binding.entertainment.setTextColor(getContext().getResources().getColor(R.color.black));
                    binding.entertainment.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));

                    fetchingData();
                }
            });
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {

            binding.HorizontalScrollView.setVisibility(View.GONE);
            binding.landscapesimmer.setVisibility(View.VISIBLE);
            binding.wanthide.setVisibility(View.GONE);
            binding.landscapeLAyoutShimmer.startShimmer();
            binding.adView.setVisibility(View.GONE);
            binding.SwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    binding.SwipeRefreshLayout.setRefreshing(false);
                    fetchingDataInLandscape();
                }
            });
            // Handle landscape orientation
            fetchingDataInLandscape();
        }
    }
}