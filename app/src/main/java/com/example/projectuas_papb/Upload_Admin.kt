import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projectuas_papb.HomeAdmin
import com.example.projectuas_papb.Movie
import com.example.projectuas_papb.databinding.ActivityUploadAdminBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.util.*

class Upload_Admin : AppCompatActivity() {
    private val firestore = FirebaseFirestore.getInstance()
    private val complaintCollectionRef = firestore.collection("complaints")
    private lateinit var binding: ActivityUploadAdminBinding
    private var updateId = ""
    private var imgPath: Uri? = null
    private var storageReference: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi referensi Firebase Storage
        storageReference = FirebaseStorage.getInstance().reference

        // Ambil data yang ada dari intent
        val selectedMovie = intent.getSerializableExtra("selectedComplaint") as? Movie

        with(binding) {
            if (selectedMovie != null) {
                // Jika ada data movie, set nilai awal untuk form editing
                title.setText(selectedMovie.title)
                desc.setText(selectedMovie.desc)
//                content.setText(selectedMovie.content)

                // Simpan ID untuk keperluan update
                updateId = selectedMovie.id
            }

            uploadButton.setOnClickListener {
                // Membuka galeri untuk memilih gambar
                val iImg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(iImg, 0)
            }

            btnAdd.setOnClickListener {
                // Ambil data dari form
                val titlemovie = title.text.toString()
                val description = desc.text.toString()

                // Cek apakah gambar telah dipilih
                if (imgPath != null) {
                    // Upload gambar ke Firebase Storage
                    uploadImageToFirebaseStorage(titlemovie, description)
                } else {
                    // Jika gambar tidak dipilih, langsung simpan data ke Firestore
                    saveDataToFirestore(titlemovie, description, "")
                }
            }
        }
    }

    // Fungsi untuk menangani hasil dari pemilihan gambar
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            imgPath = data?.data
        }
    }

    // Fungsi untuk meng-upload gambar ke Firebase Storage
    private fun uploadImageToFirebaseStorage(title: String, description: String) {
        if (imgPath != null) {
            val imageName = UUID.randomUUID().toString() // Nama unik untuk gambar
            val imageRef = storageReference?.child("images/$imageName")

            // Mulai upload
            imageRef?.putFile(imgPath!!)
                ?.addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot? ->
                    // Jika upload berhasil, dapatkan URL gambar dari Firebase Storage
                    imageRef.downloadUrl
                        .addOnSuccessListener { uri: Uri? ->
                            // Gunakan URL gambar untuk keperluan penyimpanan ke Firestore
                            val imageUrl = uri.toString()

                            // Simpan data ke Firestore
                            saveDataToFirestore(title, description, imageUrl)
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to get image URL", Toast.LENGTH_SHORT)
                                .show()
                        }
                }
                ?.addOnFailureListener { e: Exception ->
                    Toast.makeText(this, "Upload failed: $e", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Fungsi untuk menyimpan data ke Firestore
    private fun saveDataToFirestore(title: String, description: String, imageUrl: String) {
        // Buat objek Movie
        val movie = Movie(title = title, desc = description, content = imageUrl)

        if (updateId.isNotEmpty()) {
            // Jika ada updateId, berarti mode editing
            movie.id = updateId
            updateComplaint(movie)
        } else {
            // Jika tidak ada updateId, berarti mode penambahan baru
            addComplaint(movie)
        }

        // Kirim data ke halaman sebelumnya
        val intent = Intent(this@Upload_Admin, HomeAdmin::class.java)
        intent.putExtra("complaintData", movie)
        startActivity(intent)
    }

    private fun addComplaint(complaint: Movie) {
        // Add the complaint to Firestore
        complaintCollectionRef.add(complaint)
            .addOnSuccessListener { documentReference ->
                val createdComplaintId = documentReference.id
                complaint.id = createdComplaintId
                documentReference.set(complaint)
                    .addOnFailureListener {
                        Toast.makeText(this, "Error updating complaint ID", Toast.LENGTH_SHORT)
                            .show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error adding complaint", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateComplaint(complaint: Movie) {
        // Update complaint di Firestore berdasarkan ID
        complaintCollectionRef.document(complaint.id).set(complaint)
            .addOnFailureListener {
                Toast.makeText(this, "Error updating complaint", Toast.LENGTH_SHORT).show()
            }
    }
}
