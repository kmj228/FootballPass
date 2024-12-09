package com.example.FootBall.footBall_damyeong.boardAndPost

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.FootBall.R
import com.example.FootBall.footBall_damyeong.boardAndPost.boardSelectAndCreate.CommentItem

class CommentListAdapter
    (context: Context,
     private val resource: Int,
     private val commentList: List<CommentItem>,)
    : ArrayAdapter<CommentItem>(context, resource, commentList)
{
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        // 재사용 가능한 View를 가져오기 (ViewHolder 패턴 사용)
        val view: View = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)

        // 데이터 바인딩
        val name: TextView = view.findViewById(R.id.itemPostComment_name)
        val content: TextView = view.findViewById(R.id.itemPostComment_content)

        //commentItem 객체 받아오기
        val commentItem = commentList[position]

        name.text=commentItem.name
        content.text=commentItem.content

        return view
    }

}