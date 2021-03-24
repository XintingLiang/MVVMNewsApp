package com.androiddevs.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.ui.adapter.NewsAdapter
import com.androiddevs.mvvmnewsapp.ui.ui.NewsActivity
import com.androiddevs.mvvmnewsapp.ui.ui.NewsViewModel
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
                       newsAdapter.diff.submitList(newsReponse.articles)
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

    }

    fun showProgressBar(){
        paginationProgressBar.visibility= View.VISIBLE
    }


    fun setUpRecyclerView (){
        newsAdapter=NewsAdapter()
        rvBreakingNews.apply{
            adapter=newsAdapter
            layoutManager=LinearLayoutManager(activity)
        }
    }
}