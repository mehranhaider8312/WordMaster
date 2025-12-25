package com.codewithmehran.wordmaster.view.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.codewithmehran.wordmaster.view.fragments.IntroFragment

class IntroAdapter(
    fragmentActivity: FragmentActivity,
    private val fragments: List<IntroFragment>
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}
