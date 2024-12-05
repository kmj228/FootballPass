package com.example.FootBall.footBall_damyeong.boardAndPost.boardSelectAndCreate

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.FootBall.FireStoreConnection
import com.example.FootBall.R

class BoardListAdapter
    (context: Context,
     private val resource: Int,
     private val boardList: List<BoardListItem>,
     private val rootPath:String)
    :ArrayAdapter<BoardListItem>(context, resource, boardList)
{
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        // 재사용 가능한 View를 가져오기 (ViewHolder 패턴 사용)
        val view: View = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)

        // 데이터 바인딩
        val boardName: TextView = view.findViewById(R.id.itemBoardPreview_boardName)
        val boardExplanation: TextView = view.findViewById(R.id.itemBoardPreview_explanation)
        val numberOfPost: TextView = view.findViewById(R.id.itemBoardPreview_numberOfPost)

        //postListItem 객체 받아오기
        val boardListItem = boardList[position]

        //itemBoardPreview속 boardName에 게시판이름넣기
        boardName.text = boardListItem.boardName
        //itemBoardPreview속 boardExplanation 게시판 설명넣기
        boardExplanation.text = "게시글 설명 : "+boardListItem.boardExplanation
        //itemBoardPreview속 numberOfPost 게시판의 총 게시글 갯수 넣기

        FireStoreConnection.onGetCount(rootPath+boardListItem.boardName+"/posts")
        {
                numberOfDocument->
            numberOfPost.text="게시글 갯수 : "+numberOfDocument.toString()
        }
        Log.d("getView",boardListItem.boardName)
        return view
    }

}