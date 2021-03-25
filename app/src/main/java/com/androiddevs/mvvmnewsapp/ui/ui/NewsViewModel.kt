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
    var breakingNewsResponse : NewsResponse? = null

    val searchNews: MutableLiveData<State<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse : NewsResponse? = null

    init {
        getBreakingNews("us")
    }

    // connect to repository to api service
    fun getBreakingNews (countryCode: String)= viewModelScope.launch {
         breakingNews.postValue(State.Loading())
        val response = newsRepository.getBreakingNews(countryCode,breakingNewsPage)// first page result
        val handleResponse = handleBreakingNewsResponse(response)
        breakingNews.postValue(handleResponse)
        }

    fun handleBreakingNewsResponse(response: Response<NewsResponse>):State<NewsResponse> {
        // after we got the first page NewsResponse, we load the second page, but when do we call second page request?
        // we will call the request in the UI when we are scrolling and isNotTheLastPage and total>visible
        if (response.isSuccessful){
            response.body()?.let { resultResponse->
                breakingNewsPage++

                val currentBreakingNewsResponse = breakingNewsResponse
                if (currentBreakingNewsResponse==null){
                    breakingNewsResponse= resultResponse
                    return State.Success(resultResponse)
                } else{
                    val oldArticles = breakingNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)

                    // the above is the same as following: if the type we are editing is not primitive, when we edit, we are editing the original even
                    // we make a copy of that
                   // breakingNewsResponse?.articles?.addAll(resultResponse.articles)
                    return State.Success(currentBreakingNewsResponse)
                }
            }
        }
        return State.Error(response.message())//?
    }// can we use enqueue(object: Callback) here to handle onResponse or onFailure?? we can, but we also want to have a loading state, so we use State

    fun searchForNews(searchQuery: String) = viewModelScope.launch {
        searchNews.postValue(State.Loading())
        val response = newsRepository.searchForNews(searchQuery,searchNewsPage)// first page result
        searchNews.postValue(handleSearchNews(response))
    }

    fun handleSearchNews(response: Response<NewsResponse>): State<NewsResponse>{
        if (response.isSuccessful){
           response.body()?.let { resultResponse->
               searchNewsPage++
               if(searchNewsResponse==null){
                   searchNewsResponse=resultResponse
               }else{
                   val oldArticle = searchNewsResponse?.articles
                   val newArticle = resultResponse.articles
                   oldArticle?.addAll(newArticle)
               }
               return State.Success(searchNewsResponse?:resultResponse)// what do we return here? did we add the old articles and new article? did we pass it ?
           }
        }
        return State.Error(response.message())
    }

    fun getSavedArticle()= newsRepository.getSavedArticle()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)  }

    fun upsertArticle(article: Article)= viewModelScope.launch {
         newsRepository.upsertArticle(article)
    }
}