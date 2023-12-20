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
import com.example.projectuas_papb.databinding.FragmentHomeUserBinding
import com.google.firebase.firestore.FirebaseFirestore

class HomeUserFragment : Fragment(), MovieItemClickListener {

    private lateinit var binding: FragmentHomeUserBinding
    private lateinit var itemAdapterMovie: UserMovieAdapter
    private lateinit var itemListMovie: ArrayList<MovieAdminData>
    private val firestore = FirebaseFirestore.getInstance()
    private val moviesCollection = firestore.collection("Movie")
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var recyclerViewItem: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Error getting movies from Firestore", e)
            }
    }

    override fun onEditButtonClick(movie: MovieAdminData) {
        // Handle edit button click in user mode (if needed)
    }

    override fun onDeleteButtonClick(movie: MovieAdminData) {
        // Handle delete button click in user mode (if needed)
    }
}
