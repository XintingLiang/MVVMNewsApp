package com.androiddevs.mvvmnewsapp.ui.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.ui.db.ArticlesDatabase
import kotlinx.android.synthetic.main.activity_news.*

class NewsActivity : AppCompatActivity() {

    lateinit var viewModel: NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        //bottomNavigation
        bottomNavigationView.setupWithNavController(newsNavHostFragment.findNavController())

        // viewModel
        val newsRepository = NewsRepository(ArticlesDatabase(this))
        val viewModelFactory = NewsViewModelFactory(newsRepository)
        viewModel=ViewModelProvider(this, viewModelFactory).get(NewsViewModel::class.java)

    }

}
