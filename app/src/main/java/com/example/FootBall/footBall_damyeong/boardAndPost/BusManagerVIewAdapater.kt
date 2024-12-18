package com.example.FootBall.footBall_damyeong.boardAndPost

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

import com.example.FootBall.FireStoreConnection
import com.example.FootBall.MyApplication
import com.example.FootBall.R
import com.example.FootBall.footBall_damyeong.boardAndPost.Bus.Ticket

class BusManagerVIewAdapater
    (context: Context,
     private val resource: Int,
     private val TicketList: List<Ticket>,)
    : ArrayAdapter<Ticket>(context, resource, TicketList)
{
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        // 재사용 가능한 View를 가져오기 (ViewHolder 패턴 사용)
        val view: View = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)

        // 데이터 바인딩
        val content:TextView=view.findViewById(R.id.itemBusReservationManagerview_content)
        val price:TextView=view.findViewById(R.id.itemBusReservationManagerview_price)
        val userEmail:TextView=view.findViewById(R.id.itemBusReservationManagerview_userEmail)
        val button:Button=view.findViewById(R.id.itemBusReservationManagerview_Button)
        //Item 객체 받아오기
        val item = TicketList[position]

        content.text=item.busContent
        price.text=item.price
        userEmail.text=item.userEmail

        button.setOnClickListener{

        }
        //점세개 버튼을 누를시에
        return view
    }

}