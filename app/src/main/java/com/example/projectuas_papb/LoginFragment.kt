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
        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
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
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val userEmail = firebaseAuth.currentUser?.email
                        if (userEmail == "admin@gmail.com") {
                            // If the user is admin
                            Toast.makeText(activity, "Authentication Success.", Toast.LENGTH_SHORT).show()

                            // Set Shared Preference value for admin
                            setSharedPreference("admin")

                            val adminIntent = Intent(requireContext(), HomeAdmin::class.java)
                            startActivity(adminIntent)
                        } else {
                            // If the user is not admin, navigate to HomeUser
                            Toast.makeText(activity, "Authentication Success.", Toast.LENGTH_SHORT).show()

                            // Set Shared Preference value for user
                            setSharedPreference("user")

                            val intent = Intent(requireContext(), UserActivity::class.java)
                            startActivity(intent)
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(activity, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(activity, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun setSharedPreference(userType: String) {
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("userType", userType)
        editor.apply()
    }
}
