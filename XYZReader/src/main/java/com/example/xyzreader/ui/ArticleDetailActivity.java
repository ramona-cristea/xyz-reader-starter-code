package com.example.xyzreader.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;

import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.xyzreader.R;
import com.example.xyzreader.data.Article;
import com.example.xyzreader.data.ArticlesViewModel;

import java.util.ArrayList;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends AppCompatActivity {

    public static String EXTRA_ITEM_ID = "extra_item_id";
    private ProgressBar mLoadingIndicator;
    private int mCurrentId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_article_detail_redesigned);

        Toolbar toolbar = findViewById(R.id.toolbar);
        mLoadingIndicator = findViewById(R.id.progress_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ArticlesViewModel articlesViewModel = ViewModelProviders.of(this).get(ArticlesViewModel.class);
        articlesViewModel.getNewsArticles().observe(this, this::handleResponse);
        mLoadingIndicator.setVisibility(View.VISIBLE);
        articlesViewModel.loadNewsArticles();

        mCurrentId = getIntent().getIntExtra(EXTRA_ITEM_ID, 0);
    }

    private void handleResponse(ArrayList<Article> articles) {
        if(articles == null) {
            return;
        }
        Article article = null;
        for(int i = 0; i < articles.size(); i++) {
            if(mCurrentId == articles.get(i).getId()) {
                article = articles.get(i);
                break;
            }
        }

        if(article != null) {
            ArticleDetailFragmentRedesigned detailFragment = new ArticleDetailFragmentRedesigned();
            detailFragment.setArticleData(article);
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            mLoadingIndicator.setVisibility(View.GONE);

            fragmentManager.beginTransaction()
                    .replace(R.id.container_fragment_details, detailFragment, "article_details_tag")
                    .addToBackStack("article_details")
                    .commit();

        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
