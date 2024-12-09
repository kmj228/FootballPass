package com.example.FootBall.footBall_damyeong.boardAndPost

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.FootBall.FireStorageConnection
import com.example.FootBall.FireStoreConnection
import com.example.FootBall.databinding.ActivityPostBinding
import com.example.FootBall.MyApplication
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PostActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding=ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val deleteButton=binding.postDeleteButton
        val postTitle = binding.postTitle
        val postDate = binding.postDate
        val postAuther = binding.postAuther
        val postContent = binding.textView
        val postImage = binding.imageView

        //인텐트대신 전역변수를 통해서 전달받음.
        var postRef: BoardActivity.PostRef =BoardActivity.postRef

        /*
        // 인텐트를 통해 받은 데이터 표시
        val postPath = intent.getStringExtra("postPath")
        // 받은 게시글 경로가 null이면 종료.
        if(postPath == null){
            Log.d("dfdf","불러오기에 실패함5.")
            finish()
        }
        //게시물경로로 게시물(post객체)받아오기
        FireStoreConnection.onGetDocument(postPath!!) { document ->
            post = document.toObject(Post::class.java)

            if (post == null)//게시글을 받아와도 안에든게 없으면 종료.
            {
                Log.d("dfdf", "불러오기에 실패함t=8.")
                finish()
            }
            //post 객체안의 데이터를 뺴내어 화면에 표시하기.
            postTitle.text = post?.title ?: ""
            postDate.text = post?.title ?: ""
            postAuther.text = "작성자 : "+post?.author ?: ""
            postContent.text = post?.content ?: ""
            // postListItem안에 있는 Timestamp를 date로 변환해 표기하기
            val date = Date(post?.timestamp ?: 0)
            // 날짜 포맷 설정
            //val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일 HH:mm:ss", Locale.getDefault())
            val formattedDate = dateFormat.format(date)
            postDate.text = formattedDate


            //화면에 이미지 띄우기
            if(post?.imagePath != null)
            {
                FireStorageConnection.bindImageByPath(this,post!!.imagePath!!,postImage)
            }
            else
            //이미지가 없으면 이미지뷰를 안보이게한다.
                postImage.visibility = ImageView.INVISIBLE
        }

         */

        val post:Post=postRef.post
        postTitle.text =post.title ?: ""
        postDate.text = post.title ?: ""
        postAuther.text = "작성자 : "+post.author ?: ""
        postContent.text = post.content ?: ""
        // postListItem안에 있는 Timestamp를 date로 변환해 표기하기
        val date = Date(post.timestamp ?: 0)
        // 날짜 포맷 설정
        //val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일 HH:mm", Locale.getDefault())
        val formattedDate = dateFormat.format(date)
        postDate.text = formattedDate

        // 이미지가 있는 경우
        if (post.imagePath != null) {
            postImage.visibility = View.VISIBLE
            postImage.layoutParams = (postImage.layoutParams as LinearLayout.LayoutParams).apply {
                weight = 1f // 이미지가 차지할 비율
            }
            postContent.layoutParams = (postContent.layoutParams as LinearLayout.LayoutParams).apply {
                weight = 2f // 텍스트가 차지할 비율
            }

            FireStorageConnection.bindImageByPath(this,post.imagePath!!,postImage)
        } else {
            // 이미지가 없는 경우
            postImage.visibility = View.GONE
            postContent.layoutParams = (postContent.layoutParams as LinearLayout.LayoutParams).apply {
                weight = 1f // 텍스트가 전체를 차지
            }
        }

        //게시글 삭제버튼
        deleteButton.setOnClickListener{
            val app = application as MyApplication
            val user = app.currentUser
            //현재사용자 이메일과 작성자 이메일이 다르면 빠꾸
            if(!(user!!.email.equals(post!!.email)))
            {
                Toast.makeText(this,"이 글을 작성한 작성자만 삭제할 수 있습니다.",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            //그냥 바로 게시글 삭제하기  (나중에 이곳에 사용자 인증코드 추가)
            FireStoreConnection.documentDelete(postRef.postPath){
                    success ->
                if(success){//현재문서 삭제에 성공했다면
                    //이미지 문서가 없는 경우엔 바로 게시글 삭제완료처리
                    if(post!!.imagePath==null || post!!.imagePath==""){
                        finish()
                    }
                    else
                    {
                        //안에 들어있는 이미지도 파이어 스토리지에 삭제하기
                        FireStorageConnection.deleteFile(post!!.imagePath!!){
                                success->
                            //이미지 삭제에 성공했다면
                            if(success){
                                finish()
                            }
                            //이미지 삭제에 실패했다면
                            else{
                                Toast.makeText(this,"이미지 삭제 실패",Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        }
                    }
                }
                else{
                    Toast.makeText(this,"게시글 삭제 실패",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}