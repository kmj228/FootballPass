package com.example.FootBall.footBall_damyeong.boardAndPost.Bus

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.FootBall.R

open class AcceptedBusTicketListAdapter
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
        val state:TextView= view.findViewById(R.id.itemAcceptedBusTicket_state)
        //Item 객체 받아오기
        val item = itemList[position]

        content.text=item.busItem.content
        busTime.text=item.busItem.busTime
        price.text=item.busItem.price.toString()+" 원"
        startAddress.text="출발:"+item.busItem.startAddress
        endAddress.setText("도착: "+item.busItem.endAddress)
        ticketNum.text="티켓 수량: "+item.ticket.num
        bankName.text="입금자명: "+item.ticket.bankName
        state.text=item.state
        Log.d("BusReservationActivity3",endAddress.text.toString())


        //점세개 버튼을 누를시에
        return view
    }

}