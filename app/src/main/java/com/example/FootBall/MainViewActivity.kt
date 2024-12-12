package com.example.FootBall

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
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

    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_view) // 레이아웃 설정

        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)

        val adapter = MyPagerAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.setIcon(iconList[position]) // 탭의 아이콘 설정
        }.attach()

        // Intent로 초기 페이지 전달받기
        val initialPage = intent.getIntExtra("INITIAL_PAGE", 0)
        goToPage(initialPage, false) // 초기 페이지 설정
    }

    // 특정 페이지로 이동하는 함수
    fun goToPage(position: Int, smoothScroll: Boolean = true) {
        val itemCount = viewPager.adapter?.itemCount ?: 0
        if (position >= 0 && position < itemCount) { // 유효 범위 확인
            viewPager.setCurrentItem(position, smoothScroll)
        } else {
            throw IllegalArgumentException("Invalid page position: $position. Valid range is 0 to ${itemCount - 1}.")
        }
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this).apply {
            setTitle("앱 종료")
            setMessage("정말로 종료하시겠습니까?")
            setPositiveButton("네") { _, _ ->
                super.onBackPressed() // 기본 뒤로 가기 동작 실행
            }
            setNegativeButton("아니요") { dialog, _ ->
                dialog.dismiss() // 다이얼로그 닫기
            }
            show()
        }
    }


}
