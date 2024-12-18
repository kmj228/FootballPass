package com.example.FootBall.footBall_damyeong.boardAndPost.Bus

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import com.example.FootBall.FireStoreConnection
import com.example.FootBall.R
import com.example.FootBall.footBall_damyeong.boardAndPost.BoardActivity

class BusListAdapater
    (context: Context,
     private val resource: Int,
     private val ItemList: List<BusItem>)
    : ArrayAdapter<BusItem>(context, resource, ItemList)
{
    private var btnCherk:Boolean=false
    companion object{
        var busItem:BusItem?=null
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        // 재사용 가능한 View를 가져오기 (ViewHolder 패턴 사용)
        val view: View = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)

        // 데이터 바인딩
        val content: TextView = view.findViewById(R.id.itemBusReservation_content)
        val busTime: TextView = view.findViewById(R.id.itemBusReservation_busTime)
        val price: TextView = view.findViewById(R.id.itemBusReservation_price)
        val startAddress: TextView = view.findViewById(R.id.itemBusReservation_startAddress)
        val endAddress: TextView = view.findViewById(R.id.itemBusReservation_endAddress)
        val account: TextView = view.findViewById(R.id.itemBusReservation_account)
        val ticketNum: EditText = view.findViewById(R.id.itemBusReservation_numEdit)
        val bankName:EditText=view.findViewById(R.id.itemBusReservation_bankName)

        val reservationButton:Button=view.findViewById(R.id.itemBusReservation_Button)
        //Item 객체 받아오기
        val busItem = ItemList[position]

        content.text=busItem.content
        busTime.text=busItem.busTime
        price.text=busItem.price.toString()+" 원"
        startAddress.text=busItem.startAddress
        endAddress.text=busItem.endAddress
        account.text=busItem.account

        reservationButton.setOnClickListener{

            var ticketN: Int=0
            try {
                ticketN = ticketNum.text.toString().toInt()
            }
            catch (e:NumberFormatException){
                Toast.makeText(context,"숫자만입력하세요",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val allPrice=busItem.price.toString().toInt()*ticketN
            if(allPrice==0){
                Toast.makeText(context,"제대로 입력하세요",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(bankName.text.toString()==""){
                Toast.makeText(context,"입금자명을 제대로 입력하세요",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            var ticket=Ticket(
                busReservationpath = busItem.path,
                userEmail = BoardActivity.user.email,
                price=allPrice.toString(),
                busContent=busItem.content,
                bankName =bankName.text.toString(),
                num = ticketN.toString()
            )
            val path="publicBoards/"+BoardActivity.user.team+"/notAcceptedTicket"
            FireStoreConnection.addDocument(path,ticket){
                success, docPath ->
                if(success){
                    Toast.makeText(context,"성공. 계좌로 입금하세요",Toast.LENGTH_SHORT).show()
                }
                else
                {
                    Toast.makeText(context,"예매실패",Toast.LENGTH_SHORT).show()
                }
            }
        }
        //점세개 버튼을 누를시에
        return view
    }

}