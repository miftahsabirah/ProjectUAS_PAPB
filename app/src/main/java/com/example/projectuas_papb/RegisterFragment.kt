package com.example.projectuas_papb

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import com.example.projectuas_papb.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding

    // autentikasi pengguna
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentRegisterBinding.bind(view)

        // Menginisialisasi Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance()

        binding.tombolRegistrasi.setOnClickListener {
            val email = binding.addEmail.text.toString()
            val pass = binding.addPassword.text.toString()
            val confirmPass = binding.confirmPassword.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (pass == confirmPass) {
                    // Membuat pengguna baru menggunakan Firebase Authentication
                    firebaseAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val user: FirebaseUser? = firebaseAuth.currentUser

                                sendRegistrationToServer(user?.uid ?: "")

                                sendNotification()

                                navigateToUserActivity()
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    task.exception?.message ?: "Registration failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    Toast.makeText(requireContext(), "Password is not matching", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendRegistrationToServer(userId: String) {
    }

    private fun sendNotification() {
        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "Your_Channel_Id"
        val notificationId = 1

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Your_Channel_Name",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(requireContext(), channelId)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Selamat Bergabung")
            .setContentText("Selamat! Anda telah berhasil bergabung.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify(notificationId, builder.build())
    }

    private fun navigateToUserActivity() {
        val intent = Intent(requireContext(), UserActivity::class.java)
        startActivity(intent)
    }
}
