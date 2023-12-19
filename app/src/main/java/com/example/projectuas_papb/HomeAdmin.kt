package com.example.projectuas_papb

import Upload_Admin
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.lifecycle.MutableLiveData
import com.example.projectuas_papb.databinding.ActivityHomeAdminBinding
import com.google.firebase.firestore.FirebaseFirestore

class HomeAdmin : AppCompatActivity() {
    private lateinit var binding: ActivityHomeAdminBinding
//    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private val MovieList: ArrayList<Movie> = ArrayList()
    private val MovieListLiveData: MutableLiveData<List<Movie>> by lazy {
        MutableLiveData<List<Movie>>()
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the click listener for the "Tambah" button to open the Form activity
        binding.fabTambah.setOnClickListener {
            val intent = Intent(this, Upload_Admin::class.java)
            startActivity(intent)
        }

//        listView = findViewById(R.id.listMovie)
//        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ArrayList())
//        listView.adapter = adapter

        // Terima data dari intent
        val complaintData = intent.getSerializableExtra("complaintData") as? Movie

        // Tampilkan data di halaman lain dan tambahkan ke list
        complaintData?.let {

            // Add the new complaint to the list
            MovieList.add(it)

            // Update the adapter with the new list
//            updateAdapter()

            // Clear the intent data to avoid duplicates
            intent.removeExtra("complaintData")
        }
        // Observe changes in Firestore and update the list
        observeComplaints()

        // Set item click listener for the listView
//        with(binding) {
//            listView.setOnItemClickListener { _, _, position, _ ->
//                val selectedComplaint = MovieList[position]
//
//                // Intent untuk membuka ComplaintDetailActivity dengan data complaint yang dipilih
//                val intent = Intent(this@MainActivity, DetailComplaintActivity::class.java)
//                intent.putExtra("selectedComplaint", selectedComplaint)
//                startActivity(intent)
//            }
//        }
    }



//    private fun updateAdapter() {
//        // Clear the adapter and add all items from the list
//        adapter.clear()
//        for (complaint in MovieList) {
//            val displayText =
//                "Name    :    ${complaint.title}\nTitle       " +
//                        ":   ${complaint.desc}\nContent" +
//                        ":   ${complaint.content}"
//            adapter.add(displayText)
//        }
//    }


    private fun observeComplaints() {
        val firestore = FirebaseFirestore.getInstance()
        val complaintCollectionRef = firestore.collection("complaints")

        complaintCollectionRef.addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.d("MainActivity", "Error listening for complaint changes: ", error)
                return@addSnapshotListener
            }

            // Clear the existing list
            MovieList.clear()

            snapshots?.forEach { documentSnapshot ->
                val complaint = documentSnapshot.toObject(Movie::class.java)
                if (complaint != null) {
                    MovieList.add(complaint)
                }
            }

            // Notify LiveData observer
            MovieListLiveData.postValue(MovieList)

            // Update the adapter with the new list
//            updateAdapter()
        }
    }
}