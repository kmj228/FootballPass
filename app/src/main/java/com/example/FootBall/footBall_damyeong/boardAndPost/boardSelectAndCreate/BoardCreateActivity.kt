package com.example.FootBall.footBall_damyeong.boardAndPost.boardSelectAndCreate

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.FootBall.FireStoreConnection
import com.example.FootBall.databinding.ActivityBoardCreateBinding

class BoardCreateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding=ActivityBoardCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val boardName:EditText=binding.boardCreateBoardName
        val boardExplanation:EditText=binding.boardCreateExplanation
        val createButton=binding.boardCreateCreateButton
        Log.d("BoardCreateActivity","0")

        createButton.setOnClickListener(){
            Log.d("BoardCreateActivity","1")
            val name=boardName.text.toString()
            val explanation=boardExplanation.text.toString()
            //먼저 이름이 겹치지 않는가 확인한가
            FireStoreConnection.onGetCollection("userBoards/")
            {
                documents ->

                Log.d("BoardCreateActivity","2")

                var isExists:Boolean=false
                for(d in documents){
                    if(d.getString("boardName")!!.equals(name)){
                        isExists=true
                    }
                }
                //단 하나라도 같은 이름의 게시판이 있으면
                if(isExists){
                    Toast.makeText(this,"이미 같은 이름의 게시판이 존재합니다.",Toast.LENGTH_SHORT).show()
                    return@onGetCollection
                }
                else//이름이 겹치지 않으면 본격적으로 게시판 만들기
                {
                    Log.d("BoardCreateActivity","3")
                    //게시판 정보를 담은 boardListItem을 만든뒤 파이어스토어에 올림.
                    var boardListItem= BoardListItem(name,explanation)
                    FireStoreConnection.setDocument("userBoards/"+name,boardListItem)
                    {
                        success, docPath ->
                        if(success)//게시판이 성공적으로 만들어짐.
                        {
                            Toast.makeText(this,"게시판이 성공적으로 만들어짐.",Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        else{//게시판 만들기 실패
                            Toast.makeText(this,"게시판만들기 실패.",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

        }
    }
}