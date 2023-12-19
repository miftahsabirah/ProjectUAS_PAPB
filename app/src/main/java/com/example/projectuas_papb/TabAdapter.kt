package com.example.projectuas_papb

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.projectuas_papb.LoginFragment
import com.example.projectuas_papb.RegisterFragment

class TabAdapter(fm: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fm, lifecycle) {
    override fun getItemCount(): Int {
        return 2 //membuat kolom pada bagian atas seperti register dan login
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> RegisterFragment()
            1 -> LoginFragment()
            else -> throw IllegalArgumentException("Position out of array")
        }
    }


}