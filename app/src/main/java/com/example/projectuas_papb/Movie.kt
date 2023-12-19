package com.example.projectuas_papb

import java.io.Serializable

data class Movie(
    var id: String = "",
    var title: String = "",
    var desc: String = "",
    var content: String = ""
) : Serializable