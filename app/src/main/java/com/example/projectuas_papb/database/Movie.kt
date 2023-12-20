package com.example.projectuas_papb.database

import androidx.room.Entity

@Entity(tableName = "movies")
data class Movie(
    val id: String,
    val title: String,
    val description: String,
    val imagePath: String
)


