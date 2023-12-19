package com.example.projectuas_papb

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.projectuas_papb.databinding.ActivityLoginRegisterBinding
import com.google.android.material.tabs.TabLayoutMediator

class LoginRegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding = ActivityLoginRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        with(binding){

            viewPager.adapter = TabAdapter(supportFragmentManager, this@LoginRegisterActivity.lifecycle)
            TabLayoutMediator(tabLayout,viewPager) { tab, position ->
                tab.text = when (position) {
                    0 -> "Register"
                    1 -> "Login"
                    else -> ""
                }
            }.attach()
        }

    }
}