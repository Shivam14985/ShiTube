package com.example.shivambhardwaj.shitube.APIs;

import com.example.shivambhardwaj.shitube.Models.MoviesModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface AnimationMoviesAPI {

    @GET("horror")
    Call<List<MoviesModel>> getMovies();
}
