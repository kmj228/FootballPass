package com.example.FootBall.footBall_damyeong

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter

class SlideAdapter(
    fragmentActivity: FragmentActivity,
    private val slideNews: List<List<String>>
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return slideNews.size
    }

    override fun createFragment(position: Int): Fragment {
        return SlideFragment(slideNews[position]) // 각 슬라이드에 대한 프레그먼트 생성
    }
}
