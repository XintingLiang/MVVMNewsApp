package com.androiddevs.mvvmnewsapp.ui.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevs.mvvmnewsapp.ui.model.Article
import com.androiddevs.mvvmnewsapp.ui.model.NewsResponse
import com.androiddevs.mvvmnewsapp.ui.util.State
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel (val newsRepository: NewsRepository): ViewModel(){

    val breakingNews: MutableLiveData<State<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1

    val searchNews: MutableLiveData<State<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1

    init {
        getBreakingNews("us")
    }

    // connect to repository to api service
    fun getBreakingNews (countryCode: String)= viewModelScope.launch {
         breakingNews.postValue(State.Loading())
        val response = newsRepository.getBreakingNews(countryCode,breakingNewsPage)
        println("baobaoissmart.response: ${response.body()}")
        val handleResponse = handleBreakingNewsResponse(response)
        breakingNews.postValue(handleResponse)
        }

    fun handleBreakingNewsResponse(response: Response<NewsResponse>):State<NewsResponse> {
        // why do we need to use Resource class when we also check if it's successful here?
        if (response.isSuccessful){
            response.body()?.let { resultResponse->
                return State.Success(resultResponse)
            }
        }
        return State.Error(response.message())//?
    }// can we use enqueue(object: Callback) here to handle onResponse or onFailure??

    fun searchForNews(searchQuery: String) = viewModelScope.launch {
        searchNews.postValue(State.Loading())
        val response = newsRepository.searchForNews(searchQuery,searchNewsPage)
        searchNews.postValue(handleSearchNews(response))
    }

    fun handleSearchNews(response: Response<NewsResponse>): State<NewsResponse>{
        if (response.isSuccessful){
           response.body()?.let { resultResponse->
               return State.Success(resultResponse)
           }
        }
        return State.Error(response.message())
    }

    fun getSavedArticle()= newsRepository.getSavedArticle()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)  }

    fun upsertArticle(article: Article)= viewModelScope.launch {
        val whyLong = newsRepository.upsertArticle(article)
        println("baobao.upsertArticle.why not short.why long? long: $whyLong")
    }
}