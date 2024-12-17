package com.example.FootBall.footBall_damyeong.boardAndPost.Bus

import android.os.Bundle
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.FootBall.FireStoreConnection
import com.example.FootBall.R
import com.example.FootBall.databinding.ActivityBusReservationBinding
import com.example.FootBall.footBall_damyeong.boardAndPost.BoardActivity

class BusReservationActivity : AppCompatActivity() {
    lateinit var adapater: BusReservationListAdapater
    val itemList=ArrayList<BusReservationItem>()
    var teamName:String=""
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout // SwipeRefreshLayout 추가
    fun refresh()
    {
        FireStoreConnection.onGetCollection("publicBoards/"+teamName+"/bus"){
            documents->
            itemList.clear()
            for(d in documents)
            {
                val item=d.toObject(BusReservationItem::class.java)
                if(item==null)
                    continue
                itemList.add(item)
            }
        }
        adapater.notifyDataSetChanged()
        swipeRefreshLayout.isRefreshing = false // 새로고침 완료 후 종료
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding=ActivityBusReservationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val listView:ListView=binding.busPostListView
        val title: TextView = binding.busPostTitle
        swipeRefreshLayout = binding.busPostSwipeRefreshLayout // SwipeRefreshLayout 초기화
        // 스와이프 리프레시 설정

        teamName=BoardActivity.user.team
        if(teamName=="")
        {
            Toast.makeText(this,"BusReservationActivity 오류1",Toast.LENGTH_SHORT).show()
            finish()
            return;
        }

        adapater= BusReservationListAdapater(this,R.layout.item_bus_reservation,itemList)
        listView.adapter=adapater

        title.text=teamName+" 팀 버스예매"
        refresh()



    }
}