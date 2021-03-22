package com.androiddevs.mvvmnewsapp.ui.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevs.mvvmnewsapp.ui.model.NewsResponse
import com.androiddevs.mvvmnewsapp.ui.model.Source
import com.androiddevs.mvvmnewsapp.ui.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel (val newsRepository: NewsRepository): ViewModel(){

    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1

    init {
        getBreakingNews("us")
    }

    // connect to repository to api service
    fun getBreakingNews (countryCode: String)= viewModelScope.launch {
         breakingNews.postValue(Resource.Loading())
        val response = newsRepository.getBreakingNews(countryCode,breakingNewsPage)
        val handleResponse = handleBreakingNewsResponse(response)
        breakingNews.postValue(handleResponse)
        }

    fun handleBreakingNewsResponse(response: Response<NewsResponse>):Resource<NewsResponse> {
        // why do we need to use Resource class when we also check if it's successful here?
        if (response.isSuccessful){
            response.body()?.let { resultResponse->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())//?
    }// can we use enqueue(object: Callback) here to handle onResponse or onFailure??
}