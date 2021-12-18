package com.laioffer.tinnews.repository;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.laioffer.tinnews.TinNewsApplication;
import com.laioffer.tinnews.database.TinNewsDatabase;
import com.laioffer.tinnews.model.Article;
import com.laioffer.tinnews.model.NewsResponse;
import com.laioffer.tinnews.network.NewsApi;
import com.laioffer.tinnews.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsRepository {
    private final NewsApi newsApi;
    private final TinNewsDatabase database;


    public NewsRepository(){

        newsApi= RetrofitClient.newInstance().create(NewsApi.class);
        database = TinNewsApplication.getDatabase();
    }

    public LiveData<NewsResponse> getTopHeadLines(String country){
        MutableLiveData<NewsResponse> topHeadLinesLiveDate=new MutableLiveData<>();
        newsApi.getTopHeadlines(country).enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                if (response.isSuccessful()) {
                    topHeadLinesLiveDate.setValue(response.body());
                } else {
                    topHeadLinesLiveDate.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                topHeadLinesLiveDate.setValue(null);

            }
        });
        return topHeadLinesLiveDate;

    }
    public LiveData<NewsResponse> searchNews(String query) {
        MutableLiveData<NewsResponse> everyThingLiveData = new MutableLiveData<>();
        newsApi.getEverything(query, 40).enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                if (response.isSuccessful()) {
                    everyThingLiveData.setValue(response.body());
                } else {
                    everyThingLiveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                everyThingLiveData.setValue(null);
            }
        });
        return everyThingLiveData;
    }

    public LiveData<List<Article>> getAllSavedArticles() {
        return database.articleDao().getAllArticles();
    }

    public void deleteSavedArticle(Article article) {
        AsyncTask.execute(() -> database.articleDao().deleteArticle(article)); //doing background
    }

    private static class FavoriteAsyncTask extends AsyncTask<Article, Void, Boolean> {

        private final TinNewsDatabase database;
        private final MutableLiveData<Boolean> liveData;

        private FavoriteAsyncTask(TinNewsDatabase database, MutableLiveData<Boolean> liveData) {
            this.database = database;
            this.liveData = liveData;
        }

        @Override
        protected Boolean doInBackground(Article... articles) {
            Article article = articles[0];
            try {
                database.articleDao().saveArticle(article);
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            liveData.setValue(success);
        }
    }

    public LiveData<Boolean> favoriteArticle(Article article) {
        MutableLiveData<Boolean> resultLiveData = new MutableLiveData<>();
        new FavoriteAsyncTask(database, resultLiveData).execute(article);
        return resultLiveData;
    }



}
