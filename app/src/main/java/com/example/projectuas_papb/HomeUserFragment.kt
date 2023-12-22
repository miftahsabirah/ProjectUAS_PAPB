package com.example.projectuas_papb

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectuas_papb.database.Movie
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

        movieDao = MovieRoomDatabase.getDatabase(requireContext())!!.movieDao()


        recyclerViewItem = binding.listMovie
        recyclerViewItem.setHasFixedSize(true)

        // Use GridLayoutManager with spanCount 2
        recyclerViewItem.layoutManager = GridLayoutManager(requireContext(), 2)

        itemListMovie = arrayListOf()
        itemAdapterMovie = UserMovieAdapter(itemListMovie)
        recyclerViewItem.adapter = itemAdapterMovie

        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        moviesCollection.get().addOnSuccessListener { querySnapshots ->
            val movies = ArrayList<MovieAdminData>()

            for (doc in querySnapshots) {
                val movie = doc.toObject(MovieAdminData::class.java)
                movies.add(movie)
            }

            Log.d("MovieListSize", "Firestore movies size: ${movies.size}")

            itemAdapterMovie.updateMovies(movies)
            itemAdapterMovie.notifyDataSetChanged()
        }.addOnFailureListener { e ->
            Log.e("FirestoreError", "Error getting movies from Firestore", e)
        }

        with(binding){
            fetchDataFromFirestoreAndSaveToLocal()
        }
    }


    private fun fetchDataFromFirestoreAndSaveToLocal() {
        Log.d("FirebaseToLocal", "Mulai penyalinan data dari Firestore ke Lokal")

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
                movieDao.deleteAllMovies()
                for(movie in movieEntities){
                    movieDao.insertMovie(movie)
                }
                Log.d("FirebaseToLocalMipmip", "${movieDao.getAllMovies()}")

                withContext(Dispatchers.Main) {
                    Log.d("FirebaseToLocal", "Penyalinan data selesai")

                }
            }
        }.addOnFailureListener { exception ->
            Log.e("Firebase", "Error getting documents: $exception")
        }
    }

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
        // Handle edit button click in user mode (if needed)
    }

    override fun onDeleteButtonClick(movie: MovieAdminData) {
        // Handle delete button click in user mode (if needed)
    }


}