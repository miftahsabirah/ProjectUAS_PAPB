package com.example.projectuas_papb.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase


@Database(entities = [MovieEntity::class], version = 1, exportSchema = false)
abstract class MovieRoomDatabase : RoomDatabase() {
    // akses ke DAO untuk melakukan operasi database
    abstract fun movieDao(): MovieDao

    companion object {
        @Volatile
        private var INSTANCE: MovieRoomDatabase? = null

        // Fungsi getDatabase untuk mendapatkan instance database Room.
        fun getDatabase(context: Context): MovieRoomDatabase? {
            if (INSTANCE == null){
                // memastikan bahwa hanya satu thread yang dapat membuat instance database
                synchronized(MovieRoomDatabase::class.java){
                    INSTANCE = databaseBuilder(
                        context.applicationContext,
                        MovieRoomDatabase::class.java,"MovieLocal"
                    )
                        .build()
                }
            }
            return INSTANCE

        }
    }
}
