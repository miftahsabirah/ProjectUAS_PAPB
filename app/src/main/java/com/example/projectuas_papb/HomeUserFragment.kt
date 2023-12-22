package com.example.projectuas_papb

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectuas_papb.database.MovieDao
import com.example.projectuas_papb.database.MovieEntity
import com.example.projectuas_papb.database.MovieRoomDatabase
import com.example.projectuas_papb.databinding.FragmentHomeUserBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeUserFragment : Fragment(), MovieItemClickListener {

    private lateinit var binding: FragmentHomeUserBinding
    private lateinit var itemAdapterMovie: UserMovieAdapter
    private lateinit var itemListMovie: ArrayList<MovieAdminData>
    private val firestore = FirebaseFirestore.getInstance()
    private val moviesCollection = firestore.collection("Movie")
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var recyclerViewItem: RecyclerView

    private lateinit var movieDao: MovieDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi Room database
        movieDao = MovieRoomDatabase.getDatabase(requireContext())!!.movieDao()

        // Inisialisasi RecyclerView
        recyclerViewItem = binding.listMovie
        recyclerViewItem.setHasFixedSize(true)
        recyclerViewItem.layoutManager = GridLayoutManager(requireContext(), 2)

        // Inisialisasi MovieAdapter untuk RecyclerView
        itemListMovie = arrayListOf()

        itemAdapterMovie = UserMovieAdapter(itemListMovie, this)
        recyclerViewItem.adapter = itemAdapterMovie

        // Inisialisasi SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Mengambil film dari Firestore dan memperbarui RecyclerView
        moviesCollection.get().addOnSuccessListener { querySnapshots ->
            val movies = ArrayList<MovieAdminData>()

            for (doc in querySnapshots) {
                val movie = doc.toObject(MovieAdminData::class.java)
                movies.add(movie)
            }

            Log.d("MovieListSize", "Ukuran film Firestore: ${movies.size}")

            // Memperbarui film dalam RecyclerView
            itemAdapterMovie.updateMovies(movies)
            itemAdapterMovie.notifyDataSetChanged()
        }.addOnFailureListener { e ->
            Log.e("FirestoreError", "Error mengambil film dari Firestore", e)
        }

        with(binding){
            fetchDataFromFirestoreAndSaveToLocal()
        }
    }

    // Ambil data dari Firestore dan simpan ke database lokal Room
    private fun fetchDataFromFirestoreAndSaveToLocal() {
        Log.d("FirebaseToLocal", "Mulai menyalin data dari Firestore ke Lokal")

        val firestoreMovie = firestore.collection("Movie")
        firestoreMovie.get().addOnSuccessListener { documents ->
            val movieModels = mutableListOf<MovieAdminData>()
            for (document in documents) {
                val movie = document.toObject<MovieAdminData>()
                movieModels.add(movie)
                Log.d("FirebaseToLocalMipmipp", "$movie")
            }
            val movieEntities = convertToMovieEntity(movieModels)
            CoroutineScope(Dispatchers.IO).launch {
                // Hapus semua film yang ada di database lokal
                movieDao.deleteAllMovies()

                // Masukkan film baru ke dalam database lokal
                for(movie in movieEntities){
                    movieDao.insertMovie(movie)
                }

                Log.d("FirebaseToLocalMipmip", "${movieDao.getAllMovies()}")

                withContext(Dispatchers.Main) {
                    Log.d("FirebaseToLocal", "Penyalinan data selesai")
                }
            }
        }.addOnFailureListener { exception ->
            Log.e("Firebase", "Error mengambil dokumen: $exception")
        }
    }

    // mengubah MovieAdminData menjadi MovieEntity
    private fun convertToMovieEntity(movieModels: List<MovieAdminData>): List<MovieEntity> {
        val movieEntities = mutableListOf<MovieEntity>()
        for (movieModel in movieModels) {
            val movieEntity = MovieEntity(
                movieModel.id,
                movieModel.title,
                movieModel.desc,
                movieModel.image
            )
            movieEntities.add(movieEntity)
        }
        return movieEntities
    }

    override fun onEditButtonClick(movie: MovieAdminData) {
    }

    override fun onDeleteButtonClick(movie: MovieAdminData) {
    }
    override fun onItemClick(movie: MovieAdminData) {
        val intent = Intent(requireContext(), DetailUserActivity::class.java)
        intent.putExtra("title", movie.title)
        intent.putExtra("description", movie.desc)
        intent.putExtra("imgId", movie.image)
        startActivity(intent)
    }

}
