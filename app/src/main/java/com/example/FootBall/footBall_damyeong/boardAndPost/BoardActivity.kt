package com.example.FootBall.footBall_damyeong.boardAndPost

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.FootBall.FireStoreConnection
import com.example.FootBall.R
import com.example.FootBall.databinding.ActivityBoardBinding

class BoardActivity : AppCompatActivity() {

    private var boardPath: String? = ""
    private var boardName: String? = ""
    private val postList = ArrayList<PostRef>()
    private val postItemList = ArrayList<PostRef>()
    private lateinit var adapter: PostListAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout // SwipeRefreshLayout 추가

    private fun refresh() {
        FireStoreConnection.onGetCollection(boardPath + "/posts") { documents ->
            postList.clear()
            postItemList.clear()
            for (document in documents) {
                val post = document.toObject(Post::class.java)
                if (post != null) {
                    postList.add(PostRef(post, document.reference.path))
                    postItemList.add(PostRef(post, document.reference.path))
                }
            }
            adapter.notifyDataSetChanged()
            swipeRefreshLayout.isRefreshing = false // 새로고침 완료 후 종료
        }
    }

    companion object {
        var postRef: PostRef = PostRef(Post(), "")
    }

    class PostRef(var post: Post, var postPath: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // activity_board xml 파일 바인딩
        val title = binding.boardTitle
        val searchButton = binding.boardSearchButton
        val searchEditText = binding.boardSearchEditText
        val writePostButton = binding.boardWritePostButton
        val listView = binding.boardListView
        swipeRefreshLayout = binding.boardSwipeRefreshLayout // SwipeRefreshLayout 초기화

        // 현재 보드 경로 저장
        val intent = intent
        boardPath = intent.getStringExtra("boardPath")
        boardName = intent.getStringExtra("boardName")

        // 보드 이름을 화면에 띄우기
        title.text = "$boardName 게시판"

        // 어댑터 만들기
        adapter = PostListAdapter(this, R.layout.item_post_preview, postItemList)
        listView.adapter = adapter

        // 리스트 아이템 클릭 시 게시글 화면으로 이동
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedPost = postItemList[position]
            val myIntent = Intent(applicationContext, PostActivity::class.java)
            BoardActivity.postRef = selectedPost
            startActivity(myIntent)
        }

        // 게시글 새로고침 설정
        refresh()

        // 게시글 쓰기 버튼
        writePostButton.setOnClickListener {
            val myIntent = Intent(applicationContext, CreatePostActivity::class.java)
            myIntent.putExtra("boardPath", boardPath)
            myIntent.putExtra("boardName", boardName)
            startActivity(myIntent)
        }

        // 게시글 검색 버튼
        searchButton.setOnClickListener {
            val searchTxt = searchEditText.text.toString()
            if (searchTxt.isBlank()) {
                refresh()
            } else {
                postItemList.clear()
                for (postRef in postList) {
                    if (postRef.post.content.contains(searchTxt, ignoreCase = true) ||
                        postRef.post.title.contains(searchTxt, ignoreCase = true)
                    ) {
                        postItemList.add(postRef)
                    }
                }
                adapter.notifyDataSetChanged()
                if (postItemList.isEmpty()) {
                    Toast.makeText(this, "검색된 내용이 없습니다", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 스와이프 리프레시 설정
        swipeRefreshLayout.setOnRefreshListener {
            refresh() // 새로고침 호출
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }
}
