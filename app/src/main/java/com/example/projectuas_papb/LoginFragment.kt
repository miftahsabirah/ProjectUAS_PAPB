package com.example.projectuas_papb

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.projectuas_papb.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi instance FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.clickableRegister.setOnClickListener {
            val intent = Intent(requireContext(), RegisterFragment::class.java)
            startActivity(intent)
        }

        binding.tombolLogin.setOnClickListener {
            val email = binding.addEmailLogin.text.toString()
            val pass = binding.addPasswordLogin.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                // Melakukan proses login menggunakan FirebaseAuth
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val userEmail = firebaseAuth.currentUser?.email
                        if (userEmail == "admin@gmail.com") {
                            Toast.makeText(activity, "Autentikasi Berhasil.", Toast.LENGTH_SHORT).show()

                            // Set nilai Shared Preference untuk admin
                            setSharedPreference("admin")

                            val adminIntent = Intent(requireContext(), HomeAdmin::class.java)
                            startActivity(adminIntent)
                        } else {
                            Toast.makeText(activity, "Autentikasi Berhasil.", Toast.LENGTH_SHORT).show()

                            // Set nilai Shared Preference untuk pengguna/public
                            setSharedPreference("user")

                            val intent = Intent(requireContext(), UserActivity::class.java)
                            startActivity(intent)
                        }
                    } else {
                        Toast.makeText(activity, "Autentikasi gagal.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(activity, "Kolom yang kosong tidak diperbolehkan!!", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    // untuk mengatur nilai Shared Preference
    private fun setSharedPreference(userType: String) {
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Menyimpan nilai userType ke Shared Preferences
        editor.putString("userType", userType)
        editor.apply()
    }
}
