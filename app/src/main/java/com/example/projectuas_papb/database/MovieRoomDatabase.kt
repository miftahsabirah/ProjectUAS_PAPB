package com.example.projectuas_papb.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase


@Database(entities = [MovieEntity::class], version = 1, exportSchema = false)
abstract class MovieRoomDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao

    companion object {
        @Volatile
        private var INSTANCE: MovieRoomDatabase? = null 

        fun getDatabase(context: Context): MovieRoomDatabase? {
            if (INSTANCE == null){
                synchronized(MovieRoomDatabase::class.java){
                    INSTANCE = databaseBuilder(
                        context.applicationContext,
                        MovieRoomDatabase::class.java,"MovieLocal"
                    )
                        .build()
                }
            }
            return INSTANCE
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    MovieRoomDatabase::class.java,
//                    "movie_database"
//                ).build()
//                INSTANCE = instance
//                instance

        }
    }
}



//
//    companion object {
//        @Volatile
//        private var INSTANCE: MovieRoomDatabase? = null
//
//        fun getDatabase(context: Context): MovieRoomDatabase {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    MovieRoomDatabase::class.java,
//                    "movie_database"
//                ).build()
//                INSTANCE = instance
//                instance
//            }
//        }
//    }
