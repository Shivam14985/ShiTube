package com.example.shivambhardwaj.shitube.Models;

public class MoviesModel {
    private float id;
    private String title;
    private String posterURL;
    private String imdbId;


    // Getter Methods

    public float getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterURL() {
        return posterURL;
    }

    public String getImdbId() {
        return imdbId;
    }

    // Setter Methods

    public void setId(float id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPosterURL(String posterURL) {
        this.posterURL = posterURL;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }
}
