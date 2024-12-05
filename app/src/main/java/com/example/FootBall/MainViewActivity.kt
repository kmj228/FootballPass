package com.example.FootBall

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainViewActivity : FragmentActivity() {

    private val iconList = arrayListOf(
        R.drawable.calendar,
        R.drawable.sports,
        R.drawable.home,
        R.drawable.forum,
        R.drawable.person
    ) // 각 탭의 아이콘 리소스

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_view) // 레이아웃 설정

        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)

        val adapter = MyPagerAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.setIcon(iconList[position]) // 탭의 아이콘 설정
        }.attach()

        // Intent로 초기 페이지 전달받기
        val initialPage = intent.getIntExtra("INITIAL_PAGE", 0)
        viewPager.setCurrentItem(initialPage, false) // 초기 페이지 설정 (애니메이션 없음)
    }
}
