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

        // Mendapatkan data dari Intent
        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        val imgId = intent.getStringExtra("imgId")

        // Menampilkan data di tampilan
        binding.detailTitle.text = title
        binding.detailDesc.text = description

        // Menggunakan Glide untuk memuat dan menampilkan gambar
        Glide.with(this)
            .load(imgId)
            .skipMemoryCache(true) // Skip caching in memory
            .diskCacheStrategy(DiskCacheStrategy.NONE) // Skip caching on disk
            .into(binding.detailImage)

        // Menambahkan listener untuk tombol kembali
        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, HomeUserFragment::class.java))
        }
    }
}