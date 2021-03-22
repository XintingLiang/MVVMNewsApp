package com.androiddevs.mvvmnewsapp.ui.ui

import com.androiddevs.mvvmnewsapp.ui.api.RetrofitInstance
import com.androiddevs.mvvmnewsapp.ui.db.ArticlesDatabase

class NewsRepository (
    val db: ArticlesDatabase
        ){
    // why suspend? because the net call function is a suspend function, getting breaking news from api
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int)=
        RetrofitInstance.api.getBreakingNews(countryCode,pageNumber)

    suspend fun searchForNews(searchQuery: String, pageNumber: Int)=
        RetrofitInstance.api.searchForNews(searchQuery,pageNumber)
}