package com.androiddevs.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.ui.adapter.NewsAdapter
import com.androiddevs.mvvmnewsapp.ui.ui.NewsActivity
import com.androiddevs.mvvmnewsapp.ui.ui.NewsViewModel
import com.androiddevs.mvvmnewsapp.ui.util.Constants.Companion.SEARCH_TIME_DELAY
import com.androiddevs.mvvmnewsapp.ui.util.Resource
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment: Fragment(R.layout.fragment_search_news) {

    lateinit var viewModel: NewsViewModel
    lateinit var newsadapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as NewsActivity).viewModel

        setUpRecyclerView()


        newsadapter.setOnItemClickListener {article->
            val bundle = Bundle().apply { putSerializable("article",article) }
            findNavController().navigate(R.id.action_searchNewsFragment_to_articleFragment,bundle)
        }

        var job : Job? = null
        etSearch.addTextChangedListener {editable->
            job?.cancel()
            job= MainScope().launch {
                delay(SEARCH_TIME_DELAY)
                editable?.let {
                    if(editable.toString().isNotEmpty()){
                        viewModel.searchForNews(editable.toString())
                    }
                }
            }
        }

        viewModel.searchNews.observe(viewLifecycleOwner, Observer { resourceResponse->
            when(resourceResponse){
                is Resource.Success-> {
                    hideProgressBar()
                    resourceResponse.data?.let {
                        newsadapter.diff.submitList(it.articles)
                    }
                }
                is Resource.Error->{
                    hideProgressBar()
                    resourceResponse.message?.let {
                        Log.e("Error", "an error occured : $it")
                    }
                }

                is Resource.Loading->{
                    showProgressBar()
                }
            }
        })

    }

    fun hideProgressBar(){
        paginationProgressBar.visibility = View.INVISIBLE
    }

    fun showProgressBar(){
        paginationProgressBar.visibility = View.VISIBLE
    }

    fun setUpRecyclerView(){
        newsadapter= NewsAdapter()
        rvSearchNews.apply {
            adapter = newsadapter
            layoutManager=LinearLayoutManager(activity)
        }
    }
}