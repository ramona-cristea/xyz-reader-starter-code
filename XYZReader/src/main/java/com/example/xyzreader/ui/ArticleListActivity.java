package com.example.xyzreader.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.data.Article;
import com.example.xyzreader.data.ArticlesViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * An activity representing a list of Articles. This activity has different presentations for
 * handset and tablet-size devices. On handsets, the activity presents a list of items, which when
 * touched, lead to a {@link ArticleDetailActivity} representing item details. On tablets, the
 * activity presents a grid of items as cards.
 */
public class ArticleListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ArticlesViewModel mArticlesViewModel;
    private static final String TAG = ArticleListActivity.class.toString();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArticlesAdapter mArticlesAdapter;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
    // Use default locale format
    private SimpleDateFormat outputFormat = new SimpleDateFormat();
    // Most time functions can only handle 1902 - 2037
    private GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2,1,1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list_redesigned);

        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        mArticlesAdapter = new ArticlesAdapter(null);
        recyclerView.setAdapter(mArticlesAdapter);

        int columnCount = getResources().getInteger(R.integer.list_column_count);
        StaggeredGridLayoutManager gridLayout =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayout);

        mArticlesViewModel = ViewModelProviders.of(this).get(ArticlesViewModel.class);
        mArticlesViewModel.getNewsArticles().observe(this, this::handleResponse);
    }

    private void handleResponse(ArrayList<Article> articles) {
        //mLoadingIndicator.setVisibility(View.GONE);
        if(articles == null) {
            //Toast.makeText(this, getString(R.string.load_recipes_error), Toast.LENGTH_SHORT).show();
            return;
        }

        if(mIsRefreshing) {
            mIsRefreshing = false;
            updateRefreshingUI();
        }

        mArticlesAdapter.setData(articles);
        mArticlesAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mArticlesViewModel.loadNewsArticles();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private boolean mIsRefreshing = false;


    private void updateRefreshingUI() {
        mSwipeRefreshLayout.setRefreshing(mIsRefreshing);
    }

    @Override
    public void onRefresh() {
        mIsRefreshing = true;
        updateRefreshingUI();
        mArticlesViewModel.loadNewsArticles();
    }

    private class ArticlesAdapter extends RecyclerView.Adapter<ViewHolder> {
        private ArrayList<Article> mItems;

        ArticlesAdapter(ArrayList<Article> items) {
            mItems = items;
        }

        public void setData(ArrayList<Article> items) {
            mItems = items;
        }

        @Override
        public long getItemId(int position) {
            return mItems.get(position).getId();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.list_item_article, parent, false);
            final ViewHolder vh = new ViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent detailIntent = new Intent(ArticleListActivity.this, ArticleDetailActivity.class);
                    detailIntent.putExtra(ArticleDetailActivity.EXTRA_ITEM_ID, mItems.get(vh.getAdapterPosition()).getId());
                    startActivity(detailIntent);
                }
            });
            return vh;
        }

        private Date parsePublishedDate(Article article) {
            try {
                String date = article.getPublishedDate();
                return dateFormat.parse(date);
            } catch (ParseException ex) {
                Log.e(TAG, ex.getMessage());
                Log.i(TAG, "passing today's date");
                return new Date();
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Article article = mItems.get(position);
            holder.titleView.setText(article.getTitle());
            Date publishedDate = parsePublishedDate(article);
            if (!publishedDate.before(START_OF_EPOCH.getTime())) {

                holder.subtitleView.setText(Html.fromHtml(
                        DateUtils.getRelativeTimeSpanString(
                                publishedDate.getTime(),
                                System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                                DateUtils.FORMAT_ABBREV_ALL).toString()
                                + "<br/>" + " by "
                                + article.getAuthor()));
            } else {
                holder.subtitleView.setText(Html.fromHtml(
                        outputFormat.format(publishedDate)
                        + "<br/>" + " by "
                        + article.getAuthor()));
            }
            holder.thumbnailView.setImageUrl(
                    article.getThumb(),
                    ImageLoaderHelper.getInstance(ArticleListActivity.this).getImageLoader());
            holder.thumbnailView.setAspectRatio(article.getAspectRatio());
        }

        @Override
        public int getItemCount() {
            return mItems == null ? 0 : mItems.size();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        DynamicHeightNetworkImageView thumbnailView;
        TextView titleView;
        TextView subtitleView;

        ViewHolder(View view) {
            super(view);
            thumbnailView = view.findViewById(R.id.thumbnail);
            titleView = view.findViewById(R.id.article_title);
            subtitleView = view.findViewById(R.id.article_subtitle);
            titleView.setTypeface(Typeface.createFromAsset(titleView.getResources().getAssets(), "Rosario-Regular.ttf"));
            subtitleView.setTypeface(Typeface.createFromAsset(subtitleView.getResources().getAssets(), "Rosario-Regular.ttf"));
        }
    }
}
