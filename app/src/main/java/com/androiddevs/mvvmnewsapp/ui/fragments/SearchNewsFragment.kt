package com.androiddevs.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.ui.adapter.NewsAdapter
import com.androiddevs.mvvmnewsapp.ui.ui.NewsActivity
import com.androiddevs.mvvmnewsapp.ui.ui.NewsViewModel
import com.androiddevs.mvvmnewsapp.ui.util.Constants.Companion.QUERY_PAGE_SIZE
import com.androiddevs.mvvmnewsapp.ui.util.Constants.Companion.SEARCH_TIME_DELAY
import com.androiddevs.mvvmnewsapp.ui.util.State
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
            job?.cancel()// whenever the user types in new letter, all the previous results will be cancel
            // S - 5th
            // Sw
            // Swe
            // Swed
            // Sweden
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
                is State.Success-> {
                    hideProgressBar()
                    resourceResponse.data?.let {
                        newsadapter.diff.submitList(it.articles.toList())
                        val totalPage = it.totalResults / QUERY_PAGE_SIZE +2
                        isLastPage = viewModel.breakingNewsPage == totalPage
                    }
                }
                is State.Error->{
                    hideProgressBar()
                    resourceResponse.message?.let {
                        Log.e("Error", "an error occured : $it")
                    }
                }

                is State.Loading->{
                    showProgressBar()
                }
            }
        })
    }

    fun hideProgressBar(){
        paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    fun showProgressBar(){
        paginationProgressBar.visibility = View.VISIBLE
        isLoading=true
    }

    fun setUpRecyclerView(){
        newsadapter= NewsAdapter()
        rvSearchNews.apply {
            adapter = newsadapter
            layoutManager=LinearLayoutManager(activity)
            addOnScrollListener(this@SearchNewsFragment.scrollListener)
        }
    }

    // these 3 decide if we are going to paginate
    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    // create a scrollListener to check if we should paginate
    val scrollListener = object : RecyclerView.OnScrollListener(){
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState==AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager

            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val totalItemCount = layoutManager.itemCount
            val visibleItemCount = layoutManager.childCount

            val isNotAtTheBeginning = firstVisibleItemPosition >= 0
            //? don't get this
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotLoadingAndNotAtLastPage = !isLoading && !isLastPage
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE

            val shouldPaginate = isNotAtTheBeginning && isNotLoadingAndNotAtLastPage && isTotalMoreThanVisible
                    &&isAtLastItem && isScrolling

            if(shouldPaginate){
                viewModel.searchForNews(etSearch.text.toString())
                isScrolling=false
            }

            // when we scroll, we want to check if the following is true, so we paginate

        }

    }




}