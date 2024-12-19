package com.example.FootBall.footBall_damyeong.boardAndPost.Bus

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.FootBall.FireStoreConnection
import com.example.FootBall.R
import com.example.FootBall.databinding.ActivityBusManagerBinding
import com.example.FootBall.footBall_damyeong.boardAndPost.BoardActivity
import com.example.FootBall.footBall_damyeong.boardAndPost.boardSelectAndCreate.BoardListItem
import com.google.firebase.firestore.DocumentSnapshot

class BusManagerActivity : AppCompatActivity() {
    val notAcceptedUserTicketList=ArrayList<DocumentSnapshot>()
    lateinit var adaptar:NotAcceptTicketListAdapater
    fun notAcceptTicketRefresh()
    {
        FireStoreConnection.onGetCollection("publicBoards/"+ BoardActivity.user.team+"/notAcceptedTicket")
        {
            documents->
            notAcceptedUserTicketList.clear()
            for(d in documents)
            {
                val ticket:Ticket?=d.toObject(Ticket::class.java)
                if(ticket == null)continue
                notAcceptedUserTicketList.add(d)
            }
            adaptar.notifyDataSetChanged()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding= ActivityBusManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val busContentEdit:EditText=binding.busManagerBusContentEdit
        val busTimeEdit:EditText=binding.busManagerBusTimeEdit
        val busStartEdit:EditText=binding.busManagerBusStartEdit
        val busEndEdit:EditText=binding.busManagerBusEndEdit
        val busPriceEdit:EditText=binding.busManagerBusPriceEdit
        val busInfoEnterBtn:Button=binding.busManagerBusInfoEnterBtn
        val account:EditText=binding.busManagerAccount

        val userTicketAcceptListView:ListView=binding.busManagerUserTicketAccept
        adaptar= NotAcceptTicketListAdapater(this, R.layout.item_bus_ticket_on_manager_page,notAcceptedUserTicketList)

        userTicketAcceptListView.adapter=adaptar

        notAcceptTicketRefresh()
        busInfoEnterBtn.setOnClickListener{
            var temp:Boolean=false
            if(busContentEdit.text.toString()=="")temp=true
            if(busTimeEdit.text.toString()=="")temp=true
            if(busStartEdit.text.toString()=="")temp=true
            if(busEndEdit.text.toString()=="")temp=true
            if(busPriceEdit.text.toString()=="")temp=true
            if(account.text.toString()=="")temp=true
            var price:Int=0;
            try {
                price=busPriceEdit.text.toString().toInt()
            }
            catch (e:NumberFormatException){
                Toast.makeText(this,"가격에 숫자만 입력",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(temp)
            {
                Toast.makeText(this,"정보를 다 입력하세요",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val busReservationItem=BusItem(
                content=busContentEdit.text.toString(),
                busTime=busTimeEdit.text.toString(),
                price=busPriceEdit.text.toString(),
                startAddress=busStartEdit.text.toString(),
                endAddress=busEndEdit.text.toString(),
                account=account.text.toString()
            )
            var path="publicBoards/"+ BoardActivity.user.team+"/bus"
            FireStoreConnection.addDocument(path,busReservationItem){
                success, docPath ->
                if(success)
                {
                    Toast.makeText(this,"성공",Toast.LENGTH_SHORT).show()
                    busContentEdit.setText("")
                    busTimeEdit.setText("")
                    busPriceEdit.setText("")
                    busStartEdit.setText("")
                    busEndEdit.setText("")
                    account.setText("")
                }
                else
                {
                    Toast.makeText(this,"실패",Toast.LENGTH_SHORT).show()
                }
            }


        }
    }
}