package com.androiddevs.mvvmnewsapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.ui.model.Article
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_article_preview.view.*

class NewsAdapter:RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)

    private val diffUtilCallBack = object : DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
              return oldItem.url== newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
              return oldItem == newItem
        }
    }

    // use AsyncListDiffer so we have the task to run in the background
    // does this get the most current article? when and where do we add the new article
    val diff = AsyncListDiffer(this, diffUtilCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
            return ArticleViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_article_preview, parent,false)
            )
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        // to get the current article from diff
       val article = diff.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(article.urlToImage).into(ivArticleImage)
            tvSource.text = article.source.name
            tvTitle.text = article.title
            tvDescription.text = article.description
            tvPublishedAt.text = article.publishedAt
            setOnClickListener {
                onItemClickListener?.let { it(article) } // don't get this at all
            }// what is the purpose of doing this, to pass the article as the parameter when click?
        }

    }

    override fun getItemCount(): Int {
       return  diff.currentList.size
    }

    // don't get this at all, but in the MarsProperty project, this was also implemented
    // this variable takes the lambda function as the value
    private var onItemClickListener : ((Article)->Unit)? = null

    // this never get called
    fun OnItemClickListener (listener: (Article)-> Unit){
        onItemClickListener = listener
    }


}