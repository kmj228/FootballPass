package com.example.FootBall.footBall_damyeong.boardAndPost.Bus

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

import com.example.FootBall.FireStoreConnection
import com.example.FootBall.R
import com.example.FootBall.footBall_damyeong.boardAndPost.BoardActivity
import com.example.FootBall.footBall_damyeong.boardAndPost.PostActivity
import com.example.FootBall.footBall_damyeong.boardAndPost.boardSelectAndCreate.CommentItem

class BusReservationListAdapater
    (context: Context,
     private val resource: Int,
     private val ItemList: List<BusReservationItem>,)
    : ArrayAdapter<BusReservationItem>(context, resource, ItemList)
{
    private var btnCherk:Boolean=false
    companion object{
        var busReservationItem:BusReservationItem?=null
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

        val reservationButton:Button=view.findViewById(R.id.itemBusReservation_Button)
        //Item 객체 받아오기
        val busReservationItem = ItemList[position]

        content.text=busReservationItem.content
        busTime.text=busReservationItem.busTime
        price.text=busReservationItem.price.toString()+" 원"
        startAddress.text=busReservationItem.startAddress
        endAddress.text=busReservationItem.endAddress
        account.text=busReservationItem.account

        reservationButton.setOnClickListener{

            var ticketN: Int=0
            try {
                ticketN = ticketNum.text.toString().toInt()
            }
            catch (e:NumberFormatException){
                Toast.makeText(context,"숫자만입력하세요",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val allPrice=busReservationItem.price.toString().toInt()*ticketN
            if(allPrice==0){
                Toast.makeText(context,"제대로 입력하세요",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            var ticket=Ticket(
                busReservationpath = busReservationItem.path,
                userEmail = BoardActivity.user.email,
                price=allPrice.toString(),
                busContent=busReservationItem.content
            )
            val path="publicBoards/"+BoardActivity.user.team+"/chart"
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