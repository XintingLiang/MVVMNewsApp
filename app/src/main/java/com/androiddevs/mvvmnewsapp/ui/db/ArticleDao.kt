package com.androiddevs.mvvmnewsapp.ui.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.androiddevs.mvvmnewsapp.ui.model.Article

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert (article: Article)

    @Query("SELECT * FROM articles")
    fun getSavedArticles():LiveData<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)
}