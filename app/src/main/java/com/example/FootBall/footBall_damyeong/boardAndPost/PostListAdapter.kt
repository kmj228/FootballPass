package com.example.FootBall.footBall_damyeong.boardAndPost

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.FootBall.FireStorageConnection
import com.example.FootBall.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PostListAdapter
    (context: Context,
     private val resource: Int,
     private val postList: List<BoardActivity.PostRef>)
    :ArrayAdapter<BoardActivity.PostRef>(context, resource, postList)
{
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        // 재사용 가능한 View를 가져오기 (ViewHolder 패턴 사용)
        val view: View = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)

        // 데이터 바인딩
        val titleTextView: TextView = view.findViewById(R.id.itemPostPreview_title)
        val authorTextView: TextView = view.findViewById(R.id.itemPostPreview_auther)
        val dateTextView: TextView = view.findViewById(R.id.itemPostPreview_date)
        val imageView: ImageView = view.findViewById(R.id.itemPostPreview_postImageView)

        //postListItem 객체 받아오기
        val postref = postList[position]

        // post화면에 제목띄우기
        titleTextView.text = postref.post.title
        // 화면에 작성자이름 띄우기
        authorTextView.text = postref.post.author


        // 화면에 날짜띄우기
        // postListItem안에 있는 Timestamp를 Date 객체로 변환
        val date = Date(postref.post.timestamp)
        //val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val dateFormat = SimpleDateFormat("MM-dd HH:mm:ss", Locale.getDefault())
        val formattedDate = dateFormat.format(date)
        dateTextView.text=formattedDate

        // 프리뷰이미지 있으면 이미지 띄우기
        if(postref.post.imagePath != null)
            FireStorageConnection.bindImageByPath(context,postref.post.imagePath!!,imageView)
        else //프리뷰이미지가 없으면 이미지뷰를 안보이게하기
            imageView.visibility=ImageView.INVISIBLE
        return view
    }

}