package com.example.FootBall.footBall_damyeong.boardAndPost.boardSelectAndCreate

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.FootBall.FireStoreConnection
import com.example.FootBall.R
import com.example.FootBall.databinding.ActivityBoardManagerBinding
import com.example.FootBall.footBall_damyeong.boardAndPost.BoardActivity

class BoardManagerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding=ActivityBoardManagerBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_board_manager)


        val boardTitle: EditText =binding.busManagerBoardTitle
        val boardExplanation: EditText =binding.busManagerBoardExplanation
        val boardEnterBtn: Button =binding.busManagerBoardEnterBtn

        boardEnterBtn.setOnClickListener{
            var temp:Boolean=false
            if(boardTitle.text.toString()=="")temp=true
            if(boardExplanation.text.toString()=="")temp=true
            if(temp)
            {
                Toast.makeText(this,"정보를 다 입력하세요", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(this,"성공", Toast.LENGTH_SHORT).show()
                    boardTitle.setText("")
                    boardExplanation.setText("")
                }
                else
                {
                    Toast.makeText(this,"실패", Toast.LENGTH_SHORT).show()
                }
            }

        }


    }
}