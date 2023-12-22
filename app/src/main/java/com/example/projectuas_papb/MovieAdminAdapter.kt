package com.example.projectuas_papb

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

// Define the interface
interface MovieItemClickListener {
    fun onEditButtonClick(movie: MovieAdminData)
    fun onDeleteButtonClick(movie: MovieAdminData)
    fun onItemClick(currentItem: MovieAdminData) {

    }
}


class AdminMovieAdapter(
    private val MovieAdmin: ArrayList<MovieAdminData>,
    private val clickListener: MovieItemClickListener
) : RecyclerView.Adapter<AdminMovieAdapter.MovieAdminViewHolder>() {

    // ViewHolder untuk menyimpan referensi elemen UI dari item tampilan
    class MovieAdminViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val description: TextView = itemView.findViewById(R.id.desc)
        val image: ImageView = itemView.findViewById(R.id.content)
        val buttonEdit: Button = itemView.findViewById(R.id.buttonEdit)
        val buttonHapus: Button = itemView.findViewById(R.id.buttonHapus)
    }

    // Metode yang dipanggil ketika RecyclerView perlu menggambar tampilan item
    override fun onBindViewHolder(holder: MovieAdminViewHolder, position: Int) {
        val currentItem = MovieAdmin[position]

        // Menampilkan informasi dari item saat ini ke elemen UI di ViewHolder

        // Set judul (title) dari MovieAdminData ke TextView
        holder.title.text = currentItem.title

        // Set deskripsi (description) dari MovieAdminData ke TextView
        holder.description.text = currentItem.desc

        // Menggunakan Glide untuk memuat dan menampilkan gambar dari URL ke ImageView
        Glide.with(holder.itemView.context)
            .load(currentItem.image)
            .into(holder.image)

        holder.buttonEdit.setOnClickListener {
            clickListener.onEditButtonClick(currentItem)
        }

        holder.buttonHapus.setOnClickListener {
            clickListener.onDeleteButtonClick(currentItem)
        }
    }

    // saat RecyclerView perlu membuat ViewHolder baru
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieAdminViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_movie, parent, false)
        return MovieAdminViewHolder(itemView)
    }

    // mengembalikan jumlah item dalam daftar
    override fun getItemCount(): Int {
        return MovieAdmin.size
    }

    // mengganti data set pada adapter dengan data baru
    fun setData(dataMovie: List<MovieAdminData>) {
        // Membersihkan dataMovie yang ada dan menambahkan semua dataMovie baru
        MovieAdmin.clear()
        MovieAdmin.addAll(dataMovie)
    }
}
