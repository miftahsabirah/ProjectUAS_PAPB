package com.example.projectuas_papb

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.projectuas_papb.databinding.FragmentProfileUserBinding

class ProfileUserFragment : Fragment() {

    private lateinit var binding: FragmentProfileUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            // TODO: Handle arguments if needed
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            logoutButton.setOnClickListener {
                // mengubah userType menjadi "needlogin"
                val sharedPreferences =
                    requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("userType", "needlogin")
                editor.apply()

                val intent = Intent(requireContext(), LoginRegisterActivity::class.java)
                startActivity(intent)

                requireActivity().finish()
            }
        }
    }
}

