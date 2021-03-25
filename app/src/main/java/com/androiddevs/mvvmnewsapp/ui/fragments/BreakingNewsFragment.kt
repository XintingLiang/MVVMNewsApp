package com.androiddevs.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
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
import com.androiddevs.mvvmnewsapp.ui.util.State
import kotlinx.android.synthetic.main.fragment_breaking_news.*

class BreakingNewsFragment: Fragment(R.layout.fragment_breaking_news) {

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter : NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel
        setUpRecyclerView()

        newsAdapter.setOnItemClickListener {article->
            val bundle = Bundle().apply { putSerializable("article",article) }
            findNavController().navigate(R.id.action_breakingNewsFragment_to_articleFragment,bundle)
        }

       viewModel.breakingNews.observe(viewLifecycleOwner, Observer {resourceResponse->
           when(resourceResponse){
               is State.Success-> {
                   hideProgressBar()
                   resourceResponse.data?.let {newsReponse->
                       newsAdapter.diff.submitList(newsReponse.articles.toList())

                       // if it's the last page already, we don't paginate any more
                       // total result is the result we get from the api NewsResponse
                       // when we divide, we round off, that's 1, and the last page should always be empty, so we add 2
                       val totalPage = newsReponse.totalResults / QUERY_PAGE_SIZE +2
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

    fun hideProgressBar (){
        paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    fun showProgressBar(){
        paginationProgressBar.visibility= View.VISIBLE
        isLoading = true
    }

    fun setUpRecyclerView (){
        newsAdapter=NewsAdapter()
        rvBreakingNews.apply{
            adapter=newsAdapter
            layoutManager=LinearLayoutManager(activity)
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)// apply scrollListener??
        }
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object: RecyclerView.OnScrollListener(){
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            // if we are scrolling
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                 isScrolling=true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager

            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible
                    &&isScrolling

            if (shouldPaginate){
                viewModel.getBreakingNews("us")
                isScrolling = false
            }
        }

    }

}