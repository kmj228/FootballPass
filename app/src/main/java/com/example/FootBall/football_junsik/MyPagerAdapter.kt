package com.example.FootBall.football_junsik


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MyPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        // 각 탭에 대한 Fragment 반환
        return when (position) {
            0 -> FirstTabContent()
            //1 -> SecondTabContent()

            else -> FirstTabContent()
        }
    }

    override fun getItemCount(): Int {
        return 5 // 탭의 수
    }
}