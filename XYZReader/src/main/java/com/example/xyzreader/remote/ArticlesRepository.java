package com.example.xyzreader.remote;

import android.arch.lifecycle.LiveData;

import com.example.xyzreader.data.Article;

import java.util.ArrayList;

public interface ArticlesRepository {

    LiveData<ArrayList<Article>> getNewsArticles();

}
