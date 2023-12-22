package com.example.projectuas_papb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.projectuas_papb.databinding.ActivityDetailUserBinding

class DetailUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get data from Intent
        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        val imgId = intent.getStringExtra("imgId")


        // Display data in the layout
        binding.detailTitle.text = title
        binding.detailDesc.text = description

        Glide.with(this)
            .load(imgId)
            .placeholder(R.drawable.uploadimage) // Placeholder image while loading
            .error(R.drawable.uploadimage) // Image to display in case of error
            .into(binding.detailImage)

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}
