package com.example.xyzreader.remote;


import com.example.xyzreader.data.Article;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

public interface NewsService {

    @GET("/xyz-reader-json")
    Call<ArrayList<Article>> getNews();
}
