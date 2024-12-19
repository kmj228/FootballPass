package com.example.FootBall.footBall_damyeong.boardAndPost.Bus

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.FootBall.FireStoreConnection
import com.example.FootBall.R
import com.example.FootBall.databinding.ActivityCherkMyBusTicketBinding
import com.example.FootBall.footBall_damyeong.boardAndPost.BoardActivity
import com.fasterxml.jackson.databind.ser.VirtualBeanPropertyWriter

class CherkMyBusTicketActivity : AppCompatActivity() {
    lateinit var adapter2:AcceptedBusTicketListAdapter
    val itemList2=ArrayList<AcceptedBusTicketListItem>()
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout // SwipeRefreshLayout 추가
    private fun refresh2()
    {
        adapter2.notifyDataSetChanged()
        //내가 예매한 버스티켓 불러오기
        FireStoreConnection.onGetCollection("publicBoards/"+BoardActivity.user.team+"/acceptedBusTicket")
        {
                documents ->
            itemList2.clear()

            val tempList=ArrayList<String>()
            for(d in documents)
            {
                val ticket=d.toObject(Ticket::class.java)
                if(ticket==null)continue
                if(ticket.busReservationpath=="")continue
                Log.d("BusReservationActivity",ticket.userEmail)
                FireStoreConnection.onGetDocument(ticket.busReservationpath){
                        document ->
                    val busItem=document.toObject(BusItem::class.java)
                    if(busItem==null)return@onGetDocument
                    Log.d("BusReservationActivity2",busItem.busTime)
                    val acceptedBusTicketListItem=AcceptedBusTicketListItem(busItem,ticket)
                    acceptedBusTicketListItem.state="입금 확인됨"
                    itemList2.add(acceptedBusTicketListItem)
                }
            }
            swipeRefreshLayout.isRefreshing = false // 새로고침 완료 후 종료
        }
        FireStoreConnection.onGetCollection("publicBoards/"+BoardActivity.user.team+"/notAcceptedTicket")
        {
                documents ->

            val tempList=ArrayList<String>()
            for(d in documents)
            {
                val ticket=d.toObject(Ticket::class.java)
                if(ticket==null)continue
                if(ticket.busReservationpath=="")continue
                Log.d("BusReservationActivity",ticket.userEmail)
                FireStoreConnection.onGetDocument(ticket.busReservationpath){
                        document ->
                    val busItem=document.toObject(BusItem::class.java)
                    if(busItem==null)return@onGetDocument
                    Log.d("BusReservationActivity2",busItem.busTime)
                    val acceptedBusTicketListItem=AcceptedBusTicketListItem(busItem,ticket)
                    itemList2.add(acceptedBusTicketListItem)
                }
            }
            swipeRefreshLayout.isRefreshing = false // 새로고침 완료 후 종료
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding=ActivityCherkMyBusTicketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val acceptedTicketList=binding.cherkMyBusMyAcceptedTicketList
        swipeRefreshLayout = binding.cherkMyBusSwipeRefreshLayout // SwipeRefreshLayout 초기화

        adapter2= AcceptedBusTicketListAdapter(this,R.layout.item_accepted_bus_ticket,itemList2)
        acceptedTicketList.adapter=adapter2
        refresh2()
        swipeRefreshLayout.setOnRefreshListener {
            refresh2() // 새로고침 호출
        }
    }
}
data class AcceptedBusTicketListItem(
    var busItem: BusItem,
    var ticket: Ticket,
    var state:String="입금확인안됨"
)
