package com.example.projectuas_papb

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.projectuas_papb.databinding.ActivityEditAdminBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class Edit_Admin : AppCompatActivity() {
    private lateinit var binding: ActivityEditAdminBinding
    private lateinit var movie: MovieAdminData
    private val firestore = FirebaseFirestore.getInstance()
    private val storageReference = FirebaseStorage.getInstance().getReference("images")
    private var imgPath: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil data yang dikirim dari HomeAdmin
        movie = intent.getSerializableExtra("selectedMovie") as MovieAdminData

        //menampilkan di ui
        binding.edtTitle.setText(movie.title)
        binding.edtDesc.setText(movie.desc)

        if (movie.image.isNotEmpty()) {
            Glide.with(this).load(movie.image).into(binding.edtContent)
        }


        binding.uploadButton.setOnClickListener {
            val imageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(imageIntent, 1)
        }


        binding.btnEdtmovie.setOnClickListener {
            val newTitle = binding.edtTitle.text.toString()
            val newDesc = binding.edtDesc.text.toString()

            movie.title = newTitle
            movie.desc = newDesc

            if (imgPath != null) {
                storageReference.putFile(imgPath!!)
                    .addOnSuccessListener {
                        storageReference.downloadUrl.addOnSuccessListener {
                            val newImageFile = it.toString()
                            movie.image = newImageFile
                            updateMovieData(movie)
                        }
                    }
            } else {
                updateMovieData(movie)
            }
        }
    }

    // menangani hasil pemilihan gambar
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imgPath = data.data
            Glide.with(this).load(imgPath).into(binding.edtContent)
        }
    }

    // update data pada Firestore
    private fun updateMovieData(movie: MovieAdminData) {
        firestore.collection("Movie")
            .document(movie.id)
            .set(movie)
            .addOnSuccessListener {
                Toast.makeText(this, "Changes saved successfully", Toast.LENGTH_SHORT).show()

                val intent = Intent(this@Edit_Admin, HomeAdmin::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save changes", Toast.LENGTH_SHORT).show()
            }
    }
}
