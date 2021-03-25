package com.androiddevs.mvvmnewsapp.ui.db

import android.content.Context
import androidx.room.*
import com.androiddevs.mvvmnewsapp.ui.model.Article


@Database(entities = [Article::class], version = 4)
@TypeConverters(Converters::class)

abstract class ArticlesDatabase: RoomDatabase() {

    abstract fun getArticleDao (): ArticleDao


    companion object{

        // WE create this instance object which is a singleton so we are sure there will be only 1 instance in this database
        // Volatile will make sure whenever a thread update the database, other threads will see the update instantly
        @Volatile
        private var instance : ArticlesDatabase? = null
        private val LOCK = Any()//? will be used in the synchronized block to make sure we will only have one instance

        // whenever we create the instance in this database, this invoke function will be called,
        // we will check if the instance is a null, if it's a null, we execute the things inside of the synchronized block
        // we check the instance in the synchronized block again to see if the instance is a null
        // if it's a null, we call the createDatabase function to create one instance and return the value to instance
        operator fun invoke(context: Context) = instance?: synchronized(LOCK){
            instance?:createDatabase(context).also{ instance=it}
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                ArticlesDatabase::class.java,
                "article_db.db"
            )
                .fallbackToDestructiveMigration()
                .build()
    }
}// this instance of the database will be used to access the articleDao, which has the actual database functions