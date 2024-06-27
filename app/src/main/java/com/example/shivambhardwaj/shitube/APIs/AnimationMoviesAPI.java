package com.example.shivambhardwaj.shitube.APIs;

import com.example.shivambhardwaj.shitube.Models.MoviesModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface AnimationMoviesAPI {

    @GET("0b469fb2-cc2f-4875-8cce-2ad079671ef6")
    Call<List<MoviesModel>> getMovies();
}
