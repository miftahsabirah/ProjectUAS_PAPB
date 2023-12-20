package com.example.projectuas_papb

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.projectuas_papb.databinding.ActivityLoginRegisterBinding
import com.google.android.material.tabs.TabLayoutMediator

class LoginRegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginRegisterBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi Shared Preferences
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Cek apakah pengguna sudah login sebagai "user" atau "admin"
        val userType = sharedPreferences.getString("userType", "")

        if (userType == "user") {
            // Jika sudah login sebagai "user", langsung intent ke UserActivity
            val intentToUserActivity = Intent(this, UserActivity::class.java)
            startActivity(intentToUserActivity)
            finish()  // Menutup activity ini agar tidak bisa kembali lagi
        } else if (userType == "admin") {
            // Jika sudah login sebagai "admin", langsung intent ke HomeAdminActivity
            val intentToHomeAdmin = Intent(this, HomeAdmin::class.java)
            startActivity(intentToHomeAdmin)
            finish()  // Menutup activity ini agar tidak bisa kembali lagi
        }

        with(binding) {
            viewPager.adapter = TabAdapter(supportFragmentManager, this@LoginRegisterActivity.lifecycle)
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = when (position) {
                    0 -> "Register"
                    1 -> "Login"
                    else -> ""
                }
            }.attach()
        }
    }
}
