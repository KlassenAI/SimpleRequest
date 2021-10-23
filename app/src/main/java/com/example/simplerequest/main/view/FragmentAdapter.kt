package com.example.simplerequest.main.view

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.simplerequest.mvp.view.MvpFragment
import com.example.simplerequest.view.MviFragment
import com.example.simplerequest.mvvm.view.MvvmFragment

class FragmentAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            1 -> MvvmFragment()
            2 -> MviFragment()
            else -> MvpFragment()
        }
    }

    override fun getItemCount(): Int = 3
}