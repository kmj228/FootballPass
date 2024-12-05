package com.example.FootBall.football_junsik


import android.os.Bundle
import android.widget.TabHost
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator




class MainActivity : AppCompatActivity() {


    val iconView = arrayOf(
        R.drawable.home,
        R.drawable.dontknow,
        R.drawable.list,
        R.drawable.search,
        R.drawable.profile
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /*
        // 리스트로 저장해서 ID만 가지고 아이콘과 xml 뜨워주기
        // iconList = arrayListOf(R.drawable.ic_launcher_background, R.drawable.ic_launcher_background)

        // TabHost 초기화
        tabHost = findViewById<TabHost>(android.R.id.tabhost)
        tabHost.setup()

        val tabTestOne = tabHost.newTabSpec("Test1").setIndicator("그냥2")
        tabTestOne.setContent(R.id.firstTabContent)
        tabHost.addTab(tabTestOne)

        // 두 번째 탭
        val tabTestTwo = tabHost.newTabSpec("Test2").setIndicator("그냥2")
        tabTestTwo.setContent(R.id.recyclerView)
        tabHost.addTab(tabTestTwo)

        val tabTestThree = tabHost.newTabSpec("Test2").setIndicator("그냥2")
        tabTestThree.setContent(R.id.teamInfoTabContent)
        tabHost.addTab(tabTestThree)

        val tabTestFour = tabHost.newTabSpec("Test2").setIndicator("그냥2")
        tabTestFour.setContent(R.id.communiteTabContent)
        tabHost.addTab(tabTestFour)

        val tabTestFive = tabHost.newTabSpec("Test2").setIndicator("그냥2")
        tabTestFive.setContent(R.id.myPageTabContent)
        tabHost.addTab(tabTestFive)

        // RecyclerView 초기화
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 샘플 데이터 추가
        val items = arrayListOf(
            Customer("2024-11-20", "15:00", "수원월드컵 경기장", "Team B", "수원월드컵 경기장", 8, 1),
            Customer("2024-11-21", "17:00", "울산문학경기장", "Team D", "울산문학경기장", 1, 0)
            // 추가 데이터...
        )
        customerAdapter = CustomerAdapter(items)
        recyclerView.adapter = customerAdapter

        // Tab 변경 리스너 설정
        tabHost.setOnTabChangedListener { tabId ->
            val currentTab = tabHost.currentTab
            for (i in 0 until tabHost.tabWidget.childCount) {
                val view = tabHost.getTabContentView().getChildAt(i)
                view.visibility = if (i == currentTab) View.VISIBLE else View.GONE
            }
        }

         */

        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)

        // Adapter 설정
        // 하단의 탭으로 이동도 가능, 페이지가 이동할 수 있도록
        val adapter = MyPagerAdapter(this)
        viewPager.adapter = adapter

        // TabLayout과 ViewPager 연결
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.setIcon(iconView[position])
        }.attach()
    }
}

