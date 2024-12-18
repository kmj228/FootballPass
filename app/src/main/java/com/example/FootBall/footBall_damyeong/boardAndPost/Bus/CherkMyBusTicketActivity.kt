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
    fun refresh()
    {
        //내가 예매한 버스티켓 불러오기
        FireStoreConnection.onGetCollection("publicBoards/"+BoardActivity.user.team+"/acceptedBusTicket")
        {
                documents ->
            adapter2.notifyDataSetChanged()
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

        swipeRefreshLayout.setOnRefreshListener {
            refresh() // 새로고침 호출
        }
    }
}
data class AcceptedBusTicketListItem(
    var busItem: BusItem,
    var ticket: Ticket
)
class AcceptedBusTicketListAdapter
    (context: Context,
     private val resource: Int,
     private val itemList: ArrayList<AcceptedBusTicketListItem>)
    : ArrayAdapter<AcceptedBusTicketListItem>(context, resource, itemList)
{
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        // 재사용 가능한 View를 가져오기 (ViewHolder 패턴 사용)
        val view: View = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)

        // 데이터 바인딩
        val content: TextView = view.findViewById(R.id.itemAcceptedBusTicket_content)
        val busTime: TextView = view.findViewById(R.id.itemAcceptedBusTicket_busTime)
        val price: TextView = view.findViewById(R.id.itemAcceptedBusTicket_price)
        val startAddress: TextView = view.findViewById(R.id.itemAcceptedBusTicket_startAddress)
        val endAddress: TextView = view.findViewById(R.id.itemAcceptedBusTicket_endAddress)
        val ticketNum: TextView = view.findViewById(R.id.itemAcceptedBusTicket_num)
        val bankName: TextView =view.findViewById(R.id.itemAcceptedBusTicket_bankName)

        //Item 객체 받아오기
        val item = itemList[position]

        content.text=item.busItem.content
        busTime.text=item.busItem.busTime
        price.text=item.busItem.price.toString()+" 원"
        startAddress.text="rotlqkf"//"출발:"+item.busItem.startAddress
        endAddress.text="도착: "+item.busItem.endAddress
        ticketNum.text="티켓 수량: "+item.ticket.num
        bankName.text="입금자명: "+item.ticket.bankName
        Log.d("BusReservationActivity3",endAddress.text.toString())
        //점세개 버튼을 누를시에
        return view
    }

}