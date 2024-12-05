package com.example.FootBall.football_junsik


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter


class MatchInfoPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun createFragment(position: Int): Fragment {
        // 각 탭에 대한 Fragment 반환
        return when (position) {
            0 -> MatchLineUpFragment()
            1 -> DetailMatchInfoFragment()

            else -> MatchLineUpFragment()
        }
    }

    override fun getItemCount(): Int {
        return 2 // 탭의 수
    }
}