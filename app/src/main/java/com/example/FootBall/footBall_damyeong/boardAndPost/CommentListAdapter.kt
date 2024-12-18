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
import com.example.FootBall.footBall_damyeong.boardAndPost.boardSelectAndCreate.CommentItem

class CommentListAdapter
    (context: Context,
     private val resource: Int,
     private val commentList: List<CommentItem>,)
    : ArrayAdapter<CommentItem>(context, resource, commentList)
{
    private var btnCherk:Boolean=false
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        // 재사용 가능한 View를 가져오기 (ViewHolder 패턴 사용)
        val view: View = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)

        // 데이터 바인딩
        val name: TextView = view.findViewById(R.id.itemPostComment_name)
        val content: TextView = view.findViewById(R.id.itemPostComment_content)
        val likeBtn:Button=view.findViewById(R.id.itemPostComment_likeBtn)
        val dot3btn:ImageButton=view.findViewById(R.id.itemPostComment_dot3)
        //commentItem 객체 받아오기
        val commentItem = commentList[position]

        name.text=commentItem.name
        content.text=commentItem.content
        likeBtn.text="좋아요"+commentItem.like.toString()

        //좋아요 버튼을 누를시에
        likeBtn.setOnClickListener{
            //파이어베이스 작업이 종료되기 전까진 버튼 콜백을 무효화
            if(btnCherk == true)
                return@setOnClickListener
            btnCherk=true
            //이미 좋아요를 눌렀다면 (좋아요를 누른 게시그이나 댓글의 문서경로가 PostActivity.likeCache에 저장되있음)
            if(PostActivity.likeCache.contains(commentItem.path)){
                /*
                Toast.makeText(this,"이미 좋아요를 눌렀습니다.",Toast.LENGTH_SHORT).show()
                return@setOnClickListener

                 */
                FireStoreConnection.fieldIncrement(commentItem.path,"like",-1)
                {
                        success ->
                    btnCherk=false
                    if(success)
                    {
                        commentItem.like-=1
                        likeBtn.text="좋아요"+commentItem.like.toString()
                        PostActivity.likeCache.remove(commentItem.path)
                    }
                    else
                        Toast.makeText(context,"문서 업데이트 오류", Toast.LENGTH_SHORT).show()
                }
            }
            else
            {
                FireStoreConnection.fieldIncrement(commentItem.path,"like",1)
                {
                        success ->
                    btnCherk=false
                    if(success)
                    {
                        commentItem.like+=1
                        likeBtn.text="좋아요"+commentItem.like.toString()
                        PostActivity.likeCache.add(commentItem.path)
                    }
                    else
                        Toast.makeText(context,"문서 업데이트 오류", Toast.LENGTH_SHORT).show()
                }
            }
        }

        dot3btn.setOnClickListener{

            if(commentItem.email.equals(BoardActivity.user!!.email)==false)
            {
                Toast.makeText(context,"작성자만 댓글을 지울 수 있습니다.",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val dialog = AlertDialog.Builder(context)
                .setTitle("확인")
                .setMessage("댓글을 삭제하시겠습니까?")
                .setPositiveButton("Yes") { _, _ ->
                    FireStoreConnection.documentDelete(commentItem.path)
                    {
                            success ->
                        if(success){
                            Toast.makeText(context,"댓글 지워짐",Toast.LENGTH_SHORT).show()
                            //commentItem을 화면에서 안보이게 한다.
                            view.visibility=View.GONE
                        }
                        else{
                            Toast.makeText(context,"댓글 삭제 실패",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                /*
            .setNegativeButton("No") { _, _ ->
                // "No" 버튼 클릭 시 처리
                // 예를 들어, 다른 작업을 수행할 수 있음
                // Toast.makeText(this, "No clicked", Toast.LENGTH_SHORT).show()
            }

                 */
                .create()

            dialog.show()  // 다이얼로그를 화면에 띄움
        }
        //점세개 버튼을 누를시에
        return view
    }

}