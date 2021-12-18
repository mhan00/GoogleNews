package com.laioffer.tinnews.ui.save;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.laioffer.tinnews.model.Article;
import com.laioffer.tinnews.repository.NewsRepository;

import java.util.List;

public class SaveViewModel extends ViewModel {
    private final NewsRepository newsRespository;

    public SaveViewModel(NewsRepository newsRepository){
        this.newsRespository=newsRepository;
    }

    public LiveData<List<Article>> getAllSavedArticles() {
        return newsRespository.getAllSavedArticles();
    }

    public void deleteSavedArticle(Article article) {
        newsRespository.deleteSavedArticle(article);
    }
}
