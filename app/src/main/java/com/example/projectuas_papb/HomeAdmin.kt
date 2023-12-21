package com.example.projectuas_papb

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectuas_papb.databinding.ActivityHomeAdminBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class HomeAdmin : AppCompatActivity(), MovieItemClickListener {
    private lateinit var binding: ActivityHomeAdminBinding
    private lateinit var itemAdapterMovie: AdminMovieAdapter
    private lateinit var itemListMovie: ArrayList<MovieAdminData>
    private lateinit var recyclerViewItem: RecyclerView
    private lateinit var database: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private val firestore = FirebaseFirestore.getInstance()
    private val moviesCollection = firestore.collection("Movie")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerViewItem = binding.listMovie
        recyclerViewItem.setHasFixedSize(true)
        recyclerViewItem.layoutManager = LinearLayoutManager(this)

        itemListMovie = arrayListOf()
        itemAdapterMovie = AdminMovieAdapter(itemListMovie, this)
        recyclerViewItem.adapter = itemAdapterMovie

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        binding.fabTambah.setOnClickListener {
            intent = Intent(this, Upload_Admin::class.java)
            startActivity(intent)
        }

        // Logout Button
        val logoutAdminButton: ImageButton = findViewById(R.id.logoutAdmin)
        logoutAdminButton.setOnClickListener {
            logoutUser()
        }

        database = FirebaseDatabase.getInstance().getReference("Movie")

        moviesCollection.get().addOnSuccessListener { querySnapshots ->
            val movies = ArrayList<MovieAdminData>()

            for (doc in querySnapshots) {
                val movie = doc.toObject(MovieAdminData::class.java)
                movies.add(movie)
            }

            itemAdapterMovie.setData(movies)
            itemAdapterMovie.notifyDataSetChanged()
        }
    }

    private fun logoutUser() {
        // Ubah nilai userType menjadi "guest" di SharedPreferences
        val sharedPreferences =
            getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("userType", "needlogin")
        editor.apply()

        // Start activity login
        val intent = Intent(this, LoginRegisterActivity::class.java)
        startActivity(intent)
        finish() // Optional: Close the current activity if needed
    }


    override fun onEditButtonClick(movie: MovieAdminData) {
        val intent = Intent(this, Edit_Admin::class.java)
        intent.putExtra("selectedMovie", movie)
        startActivity(intent)
    }

    override fun onDeleteButtonClick(movie: MovieAdminData) {
        // Ambil ID dari item yang akan dihapus
        val movieId = movie.id

        // Hapus data dari Firestore
        moviesCollection.document(movieId)
            .delete()
            .addOnSuccessListener {
                // Hapus data dari Storage (hapus gambar jika ada)
                val imageUrl = movie.image
                if (imageUrl.isNotEmpty()) {
                    val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)
                    storageReference.delete().addOnSuccessListener {
                        Log.d("HomeAdmin", "Image deleted successfully")
                    }.addOnFailureListener {
                        Log.e("HomeAdmin", "Error deleting image: $it")
                    }
                }

                // Hapus item dari daftar dan perbarui RecyclerView
                itemListMovie.remove(movie)
                itemAdapterMovie.notifyDataSetChanged()

                Toast.makeText(this, "Movie deleted successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Log.e("HomeAdmin", "Error deleting movie: $it")
                Toast.makeText(this, "Failed to delete movie", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_movie_admin, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_logout -> {
                logoutUser()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun saveLoginStatus(isLoggedIn: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", isLoggedIn)
        editor.apply()
    }
}
