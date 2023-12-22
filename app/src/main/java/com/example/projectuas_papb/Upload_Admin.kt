package com.example.projectuas_papb

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.projectuas_papb.HomeAdmin
import com.example.projectuas_papb.MovieAdminData
import com.example.projectuas_papb.databinding.ActivityUploadAdminBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.util.*

class Upload_Admin : AppCompatActivity() {
    private lateinit var binding: ActivityUploadAdminBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val movieCollection = firestore.collection("Movie")
    private val storageReference = FirebaseStorage.getInstance().getReference("images")
    private var imgPath: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil data yang ada dari intent
        val selectedMovie = intent.getSerializableExtra("selectedMovie") as? MovieAdminData

        with(binding) {
            content.setOnClickListener {
                val Img = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(Img, 0)
            }

            btnAddmovie.setOnClickListener {
                // Ambil data dari form
                val title = title.text.toString()
                val deskripsi = desc.text.toString()

                if (imgPath != null) {
                    // Buat nama file unik menggunakan UUID
                    val filename = UUID.randomUUID().toString()

                    // Upload gambar dengan nama file unik
                    val imageRef = storageReference.child(filename)
                    imageRef.putFile(imgPath!!)
                        .addOnSuccessListener {
                            // mendapatkan URL gambar
                            imageRef.downloadUrl.addOnSuccessListener { uri ->
                                val imageFile = uri.toString()
                                val movie = MovieAdminData(
                                    title = title,
                                    desc = deskripsi,
                                    image = imageFile
                                )
                                addMovie(movie)
                                navigateToAdminHome(movie)
                            }
                        }
                        .addOnFailureListener {
                            Log.d("Upload_Admin", "Error uploading image: ", it)
                        }
                } else {
                    val movie = MovieAdminData(
                        title = title,
                        desc = deskripsi,
                        image = ""
                    )
                    addMovie(movie)
                    navigateToAdminHome(movie)
                }
            }
        }
    }

    // Metode untuk menambahkan data film baru ke Firestore
    private fun addMovie(movie: MovieAdminData) {
        // Tambahkan data film ke Firestore
        movieCollection.add(movie)
            .addOnSuccessListener { documentReference ->
                // Jika penambahan film berhasil, dapatkan ID film yang baru dibuat
                val createdMovieId = documentReference.id
                movie.id = createdMovieId
                documentReference.set(movie)
                    .addOnFailureListener {
                        Log.d("Upload_Admin", "Error adding movie: ", it)
                    }
            }
            .addOnFailureListener {
                Log.d("Upload_Admin", "Error adding movie: ", it)
            }
    }

    private fun navigateToAdminHome(movie: MovieAdminData) {
        val intent = Intent(this@Upload_Admin, HomeAdmin::class.java)
        intent.putExtra("movieData", movie)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // menangani hasil pemilihan gambar dari galeri
        if (resultCode == Activity.RESULT_OK) {
            imgPath = data?.data
            // Tampilkan gambar yang dipilih menggunakan Glide
            Glide.with(this).load(imgPath).into(binding.content)
        }
    }
}
