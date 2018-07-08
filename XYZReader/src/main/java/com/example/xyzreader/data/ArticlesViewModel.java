package com.example.xyzreader.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.example.xyzreader.remote.ArticlesRepositoryImpl;

import java.util.ArrayList;

public class ArticlesViewModel extends ViewModel {

    private MediatorLiveData<ArrayList<Article>> mNewsArticlesLiveData;
    private ArticlesRepositoryImpl mArticlesRepository;

    public ArticlesViewModel() {
        mNewsArticlesLiveData = new MediatorLiveData<>();
        mArticlesRepository = new ArticlesRepositoryImpl();
    }

    @NonNull
    public LiveData<ArrayList<Article>> getNewsArticles() {
        return mNewsArticlesLiveData;
    }

    public void loadNewsArticles() {
        mNewsArticlesLiveData.addSource(
                mArticlesRepository.getNewsArticles(),
                articles -> mNewsArticlesLiveData.setValue(articles)
        );
    }
}
