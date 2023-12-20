package com.example.projectuas_papb.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.projectuas_papb.MovieAdminData

@Dao
interface MovieDao {
    @Insert
    suspend fun insertMovie(movie: Movie)

    @Insert
    suspend fun insertAll(vararg movies: List<MovieAdminData>)

    @Query("SELECT * FROM movies")
    suspend fun getAllMovies(): List<MovieAdminData>
}
