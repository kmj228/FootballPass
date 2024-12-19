package com.example.FootBall.footBall_damyeong.boardAndPost.Bus

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
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
    lateinit var adapater1: BusListAdapater
    val itemList1=ArrayList<BusItem>()
    var teamName:String=""
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout // SwipeRefreshLayout 추가
    fun refresh()
    {
        //버스운행정보 불러오기
        Log.d("BusReservationActivity","publicBoards/"+teamName+"/bus");
        FireStoreConnection.onGetCollection("publicBoards/"+teamName+"/bus"){
            documents->
            itemList1.clear()
            for(d in documents)
            {
                val item=d.toObject(BusItem::class.java)
                if(item==null) continue
                item.path=d.reference.path
                itemList1.add(item)
            }
            adapater1.notifyDataSetChanged()
        }

        swipeRefreshLayout.isRefreshing = false // 새로고침 완료 후 종료
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding=ActivityBusReservationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val listView:ListView=binding.busPostListView
        val title: TextView = binding.busPostTitle
        val goMyTicketBtn:Button=binding.busPostGoMyTicketBtn
        swipeRefreshLayout = binding.busPostSwipeRefreshLayout // SwipeRefreshLayout 초기화
        // 스와이프 리프레시 설정

        teamName=BoardActivity.user.team
        if(teamName=="")
        {
            Toast.makeText(this,"BusReservationActivity 오류1",Toast.LENGTH_SHORT).show()
            finish()
            return;
        }

        adapater1= BusListAdapater(this,R.layout.item_bus_reservation,itemList1)
        listView.adapter=adapater1

        title.text=teamName+" 팀 버스예매"

        goMyTicketBtn.setOnClickListener{
            val myintent=Intent(applicationContext,CherkMyBusTicketActivity::class.java)
            startActivity(myintent)
        }
        refresh()

        swipeRefreshLayout.setOnRefreshListener {
            refresh() // 새로고침 호출
        }

    }
}