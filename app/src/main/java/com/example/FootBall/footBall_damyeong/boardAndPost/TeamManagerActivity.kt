package com.example.FootBall.footBall_damyeong.boardAndPost

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.FootBall.FireStoreConnection
import com.example.FootBall.R
import com.example.FootBall.databinding.ActivityTeamManagerBinding
import com.example.FootBall.footBall_damyeong.boardAndPost.Bus.BusReservationItem
import com.example.FootBall.footBall_damyeong.boardAndPost.boardSelectAndCreate.BoardListItem

class TeamManagerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding=ActivityTeamManagerBinding.inflate(layoutInflater)
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

        val teamManager_busListView:ListView=binding.teamManagerBusListView
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
            if(temp)
            {
                Toast.makeText(this,"정보를 다 입력하세요",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val busReservationItem=BusReservationItem(
                content=busContentEdit.text.toString(),
                busTime=busTimeEdit.text.toString(),
                price=busPriceEdit.text.toString(),
                startAddress=busStartEdit.text.toString(),
                endAddress=busEndEdit.text.toString()
            )
            var path="publicBoards/"+BoardActivity.user.team+"/bus"
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
                }
                else
                {
                    Toast.makeText(this,"실패",Toast.LENGTH_SHORT).show()
                }
            }


        }
    }
}