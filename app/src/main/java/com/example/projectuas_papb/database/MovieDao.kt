package com.example.projectuas_papb.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MovieDao {
    @Insert
    suspend fun insertMovie(movie: MovieEntity)

    @Insert
     fun insertAllMovies(movies: List<MovieEntity>)

     @Query("DELETE FROM movies")
     suspend fun deleteAllMovies()


    @Query("SELECT * FROM movies")
    suspend fun getAllMovies(): List<MovieEntity>

    @Query("SELECT * FROM movies WHERE id = :movieId")
    fun getMovieById(movieId: String): MovieEntity?
}
