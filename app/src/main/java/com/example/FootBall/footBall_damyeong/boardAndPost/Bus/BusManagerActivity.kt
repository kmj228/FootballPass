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

        val boardTitle:EditText=binding.teamManagerBoardTitle
        val boardExplanation:EditText=binding.teamManagerBoardExplanation
        val boardEnterBtn:Button=binding.teamManagerBoardEnterBtn

        val busContentEdit:EditText=binding.teamManagerBusContentEdit
        val busTimeEdit:EditText=binding.teamManagerBusTimeEdit
        val busStartEdit:EditText=binding.teamManagerBusStartEdit
        val busEndEdit:EditText=binding.teamManagerBusEndEdit
        val busPriceEdit:EditText=binding.teamManagerBusPriceEdit
        val busInfoEnterBtn:Button=binding.teamManagerBusInfoEnterBtn
        val account:EditText=binding.teamManagerAccount

        val userTicketAcceptListView:ListView=binding.teamManagerUserTicketAccept
        adaptar= NotAcceptTicketListAdapater(this, R.layout.item_bus_ticket_on_manager_page,notAcceptedUserTicketList)

        userTicketAcceptListView.adapter=adaptar

        notAcceptTicketRefresh()
        boardEnterBtn.setOnClickListener{
            var temp:Boolean=false
            if(boardTitle.text.toString()=="")temp=true
            if(boardExplanation.text.toString()=="")temp=true
            if(temp)
            {
                Toast.makeText(this,"정보를 다 입력하세요",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val boardItem=BoardListItem(
                boardName = boardTitle.text.toString(),
                boardExplanation = boardExplanation.text.toString(),
                official = BoardActivity.user.team
            )
            var path="publicBoards/"+boardTitle.text.toString()
            FireStoreConnection.setDocument(path,boardItem)
            {
                success, docPath ->
                if(success)
                {
                    Toast.makeText(this,"성공",Toast.LENGTH_SHORT).show()
                    boardTitle.setText("")
                    boardExplanation.setText("")
                }
                else
                {
                    Toast.makeText(this,"실패",Toast.LENGTH_SHORT).show()
                }
            }

        }
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