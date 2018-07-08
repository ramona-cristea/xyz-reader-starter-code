package com.example.xyzreader.remote;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.example.xyzreader.data.Article;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ArticlesRepositoryImpl implements ArticlesRepository{

    private static final String BASE_URL = "https://go.udacity.com";

    private NewsService mNewsService;

    public ArticlesRepositoryImpl() {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build();
        mNewsService = retrofit.create(NewsService.class);
    }

    @Override
    public LiveData<ArrayList<Article>> getNewsArticles() {
        final MutableLiveData<ArrayList<Article>> mNewsArticlesLiveData = new MutableLiveData<>();
        Call<ArrayList<Article>> request = mNewsService.getNews();
        request.enqueue(new Callback<ArrayList<Article>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Article>> call, @NonNull Response<ArrayList<Article>> response) {
                mNewsArticlesLiveData.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Article>> call, @NonNull Throwable error) {
                mNewsArticlesLiveData.setValue(null);
            }
        });

        return mNewsArticlesLiveData;
    }
}
