package com.example.FootBall

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.FootBall.footBall_damyeong.boardAndPost.boardSelectAndCreate.PublicBoardsFragment
import com.example.FootBall.footBall_damyeong.boardAndPost.boardSelectAndCreate.UserBoardsFragment
import com.example.FootBall.football_minjae.MyProfileFragment
import com.example.FootBall.football_minjae.TeamListFragment

class MyPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 5 // 탭 수
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> TeamListFragment()
            2 -> PublicBoardsFragment()
            3 -> UserBoardsFragment()
            4 -> MyProfileFragment()
            else -> TeamListFragment() // 기본 프래그먼트 반환
        }
    }
}
