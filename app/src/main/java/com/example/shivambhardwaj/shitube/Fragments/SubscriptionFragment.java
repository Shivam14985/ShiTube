package com.example.shivambhardwaj.shitube.Fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.shivambhardwaj.shitube.APIs.AnimationMoviesAPI;
import com.example.shivambhardwaj.shitube.Adapters.SubscibedChannelAdapter;
import com.example.shivambhardwaj.shitube.Models.MoviesModel;
import com.example.shivambhardwaj.shitube.RetrofitInstance;
import com.example.shivambhardwaj.shitube.databinding.FragmentSubscriptionBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubscriptionFragment extends Fragment {
    FragmentSubscriptionBinding binding;
    AnimationMoviesAPI animationMoviesAPI;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSubscriptionBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();
        animationMoviesAPI = RetrofitInstance.getRetrofit().create(AnimationMoviesAPI.class);

        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.portraitshimmer.startShimmer();
            binding.landscapeShimmerViewContainer.setVisibility(View.GONE);
            binding.portraitShimmerViewContainer.setVisibility(View.VISIBLE);
            animationMoviesAPI.getMovies().enqueue(new Callback<List<MoviesModel>>() {
                @Override
                public void onResponse(Call<List<MoviesModel>> call, Response<List<MoviesModel>> response) {
                    if (response.body().size() > 0) {
                        ArrayList<MoviesModel> list = new ArrayList<>(response.body());
                        SubscibedChannelAdapter adapter = new SubscibedChannelAdapter(list, getContext());
                        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 2);
                        binding.RecyclerView.setLayoutManager(linearLayoutManager);
                        binding.RecyclerView.setAdapter(adapter);
                        binding.portraitShimmerViewContainer.setVisibility(View.GONE);

                    } else {
                    }
                }

                @Override
                public void onFailure(Call<List<MoviesModel>> call, Throwable throwable) {

                }
            });
        }
        else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.landscapesimmer.startShimmer();
            binding.portraitShimmerViewContainer.setVisibility(View.GONE);
            binding.landscapeShimmerViewContainer.setVisibility(View.VISIBLE);
            animationMoviesAPI.getMovies().enqueue(new Callback<List<MoviesModel>>() {
                @Override
                public void onResponse(Call<List<MoviesModel>> call, Response<List<MoviesModel>> response) {
                    if (response.body().size() > 0) {
                        ArrayList<MoviesModel> list = new ArrayList<>(response.body());
                        SubscibedChannelAdapter adapter = new SubscibedChannelAdapter(list, getContext());
                        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 4);
                        binding.RecyclerView.setLayoutManager(linearLayoutManager);
                        binding.RecyclerView.setAdapter(adapter);
                        binding.landscapeShimmerViewContainer.setVisibility(View.GONE);
                    } else {
                    }
                }

                @Override
                public void onFailure(Call<List<MoviesModel>> call, Throwable throwable) {
                    Toast.makeText(getContext(), throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        return view;
    }

    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        int orientation = newConfig.orientation;
        handleOrientationChange(orientation);
    }

    private void handleOrientationChange(int orientation) {
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.portraitshimmer.startShimmer();
            binding.landscapeShimmerViewContainer.setVisibility(View.GONE);
            binding.RecyclerView.setVisibility(View.GONE);
            binding.portraitShimmerViewContainer.setVisibility(View.VISIBLE);
            animationMoviesAPI.getMovies().enqueue(new Callback<List<MoviesModel>>() {
                @Override
                public void onResponse(Call<List<MoviesModel>> call, Response<List<MoviesModel>> response) {
                    if (response.body().size() > 0) {
                        ArrayList<MoviesModel> list = new ArrayList<>(response.body());
                        SubscibedChannelAdapter adapter = new SubscibedChannelAdapter(list, getContext());
                        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 2);
                        binding.RecyclerView.setLayoutManager(linearLayoutManager);
                        binding.RecyclerView.setAdapter(adapter);
                        binding.RecyclerView.setVisibility(View.VISIBLE);
                        binding.portraitShimmerViewContainer.setVisibility(View.GONE);
                    } else {
                    }
                }

                @Override
                public void onFailure(Call<List<MoviesModel>> call, Throwable throwable) {
                    Toast.makeText(getContext(), throwable.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.landscapesimmer.startShimmer();
            binding.portraitShimmerViewContainer.setVisibility(View.GONE);
            binding.RecyclerView.setVisibility(View.GONE);
            binding.landscapeShimmerViewContainer.setVisibility(View.VISIBLE);
            animationMoviesAPI.getMovies().enqueue(new Callback<List<MoviesModel>>() {
                @Override
                public void onResponse(Call<List<MoviesModel>> call, Response<List<MoviesModel>> response) {
                    if (response.body().size() > 0) {
                        ArrayList<MoviesModel> list = new ArrayList<>(response.body());
                        SubscibedChannelAdapter adapter = new SubscibedChannelAdapter(list, getContext());
                        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 4);
                        binding.RecyclerView.setLayoutManager(linearLayoutManager);
                        binding.RecyclerView.setAdapter(adapter);
                        binding.RecyclerView.setVisibility(View.VISIBLE);
                        binding.landscapeShimmerViewContainer.setVisibility(View.GONE);
                    } else {
                    }
                }

                @Override
                public void onFailure(Call<List<MoviesModel>> call, Throwable throwable) {
                    Toast.makeText(getContext(), throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
}