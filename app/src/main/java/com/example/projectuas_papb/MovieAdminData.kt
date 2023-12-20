package com.example.projectuas_papb

import java.io.Serializable

data class MovieAdminData(
    var id: String = "",
    var title: String = "",
    var desc: String = "",
    var image: String = ""
) : Serializable