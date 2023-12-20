package com.example.projectuas_papb

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectuas_papb.MovieItemClickListener
import com.example.projectuas_papb.R
import java.util.ArrayList

class UserMovieAdapter(
    private val movieUserList: ArrayList<MovieAdminData>,
) : RecyclerView.Adapter<UserMovieAdapter.MovieUserViewHolder>() {

    class MovieUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView? = itemView.findViewById(R.id.titlemv)
        val image: ImageView? = itemView.findViewById(R.id.imagemv)
    }


    override fun onBindViewHolder(holder: MovieUserViewHolder, position: Int) {
        val currentItem = movieUserList[position]
        currentItem.image?.let { Log.d("ImageURL", it) }

        // Use safe calls to access TextView and ImageView
        holder.title?.text = currentItem.title

        // Use safe call to load the image from the URL into the ImageView
        holder.image?.let {
            Glide.with(holder.itemView.context)
                .load(currentItem.image)
                .into(it)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieUserViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.listmovie_user, parent, false)
        return MovieUserViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return movieUserList.size
    }

    // New method to set movies data
    fun updateMovies(movies: List<MovieAdminData>) {
        movieUserList.clear()
        movieUserList.addAll(movies)
        notifyDataSetChanged()
    }
}
