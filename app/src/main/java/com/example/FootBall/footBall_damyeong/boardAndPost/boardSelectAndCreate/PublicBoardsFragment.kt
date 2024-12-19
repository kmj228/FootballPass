package com.example.FootBall.footBall_damyeong.boardAndPost.boardSelectAndCreate

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ListView
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.FootBall.FireStoreConnection
import com.example.FootBall.MyApplication
import com.example.FootBall.R
import com.example.FootBall.footBall_damyeong.boardAndPost.BoardActivity
import com.example.FootBall.databinding.FragmentPublicBoardsBinding
import com.example.FootBall.footBall_damyeong.SlideAdapter
import com.example.FootBall.footBall_damyeong.boardAndPost.Bus.BusReservationActivity
import com.example.FootBall.footBall_damyeong.boardAndPost.Bus.BusManagerActivity
import org.json.JSONArray
import org.json.JSONException
import java.util.Calendar

class PublicBoardsFragment : Fragment() {
    private val boardList = ArrayList<BoardListItem>()
    private lateinit var adapter: BoardListAdapter
    private var _binding: FragmentPublicBoardsBinding? = null
    private val binding get() = _binding!!
    lateinit var adapter2: SlideAdapter
    var result: MutableList<List<String>> = mutableListOf()
    private var autoSlideHandler: Handler? = null

    private fun refresh() {
        val swipeRefreshLayout = binding.swipeRefreshLayout
        swipeRefreshLayout.isRefreshing = true // 로딩 상태 시작

        val app = requireActivity().application as MyApplication
        val user = app.currentUser

        if (user == null) {
            swipeRefreshLayout.isRefreshing = false
            return
        }

        FireStoreConnection.onGetCollection("publicBoards/") { documents ->
            if (_binding == null) return@onGetCollection
            boardList.clear()
            for (document in documents) {
                val board = document.toObject(BoardListItem::class.java)
                if (board != null) {
                    if (user.team == "") {
                        boardList.add(board)
                    }
                    else if (board.boardName == user.team) {
                        boardList.add(board)
                    }
                    else if(board.official.equals(user.team)){
                        boardList.add(board)
                    }

                    if (board.boardName == "모두의 풋볼") {
                        boardList.add(0, board)
                    }

                }
            }
            adapter.notifyDataSetChanged()
            swipeRefreshLayout.isRefreshing = false // 로딩 상태 종료
        }
    }


    override fun onStart() {
        super.onStart()
        refresh()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPublicBoardsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listView: ListView = binding.publicBoardsListView
        val swipeRefreshLayout = binding.swipeRefreshLayout // SwipeRefreshLayout 초기화
        var busButton:Button=binding.publicBoardsBusBtn
        var managerPageButton:Button=binding.publicBoardsManagerBtn

        // 어댑터 만들기
        adapter = BoardListAdapter(requireContext(), R.layout.item_board_preview, boardList, "publicBoards/")
        listView.adapter = adapter

        // 초기 데이터 로드
        refresh()

        // SwipeRefreshLayout 새로고침 동작
        swipeRefreshLayout.setOnRefreshListener {
            refresh() // 데이터 새로고침 함수 호출
            swipeRefreshLayout.isRefreshing = false // 새로고침 완료 상태로 변경
        }


        //유저가 어드민이면 관리자페이지접속버튼을 표시
        managerPageButton.setOnClickListener{
            val myintent=Intent(requireContext(), BusManagerActivity::class.java)
            startActivity(myintent)
        }
        if(BoardActivity.user.admin==true)
        {
            binding.publicBoardsManagerBtn.visibility=View.VISIBLE

        }

        setupWebView()
        val cal = Calendar.getInstance()
        val date = String.format(
            "%d%02d%02d",
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )

        loadWebViewUrl("https://sports.news.naver.com/kfootball/news/index?date=${date}&isphoto=N&type=popular")

        // 리스트 아이템 클릭 시 게시글 화면으로 이동
        listView.setOnItemClickListener { _, _, position, _ ->
            val board = boardList[position]
            val intent = Intent(requireContext(), BoardActivity::class.java)
            intent.putExtra("boardPath", "publicBoards/" + board.boardName)
            intent.putExtra("boardName", board.boardName)
            startActivity(intent)
        }

        //버스 예먜버튼
        busButton.setOnClickListener{
            val myIntent=Intent(this.context, BusReservationActivity::class.java)
            startActivity(myIntent)
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        adapter.clear() // 어댑터 리소스 정리 (예: 데이터 초기화)
        _binding = null
        autoSlideHandler?.removeCallbacksAndMessages(null)
        autoSlideHandler = null
    }

    private fun setupWebView() {
        binding.webView.webViewClient = WebViewClient()
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.domStorageEnabled = true
        binding.webView.settings.mediaPlaybackRequiresUserGesture = false

        // User-Agent 설정해서 PC와 비슷하게 만든다.
        val userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36"
        binding.webView.settings.userAgentString = userAgent
    }

    private fun loadWebViewUrl(url: String) {
        binding.webView.loadUrl(url)
        Log.d("크롤링 시작", "시작")
        var cnt = 0
        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                binding.webView.evaluateJavascript("""
                (function() {
                    var kLeagueNewsList = [];
                    var popularNewsElements = document.querySelectorAll('div.news_list ul li'); // 모든 li 요소 선택

                    // 각 뉴스 항목을 순회
                    popularNewsElements.forEach(function(news) {
                        var newsSite = news.querySelector('a.thmb').href; // 뉴스 링크
                        var newsImage = news.querySelector('img').src; // 이미지 URL
                        var newsTitle = news.querySelector('div.text a.title span').textContent; // 뉴스 제목
                        var newsInfo = news.querySelector('div.text p.desc').textContent; // 뉴스 설명
                        var newsPublisher = news.querySelector('div.text span.press').textContent; // 뉴스 출처

                        kLeagueNewsList.push([newsSite, newsImage, newsTitle, newsInfo, newsPublisher]);
                    });

                    return kLeagueNewsList;
                })()
            """.trimIndent()) { html ->
                    if (!html.isNullOrEmpty() && html != "null" && cnt == 0) {
                        try {
                            Log.d("html", html.toString())
                            result = parseJson(html)
                            Log.d("ListResult", result.toString())

                            // 어댑터를 초기화하고 데이터로 설정
                            adapter2 = SlideAdapter(requireActivity(), result)
                            binding.viewPager.adapter = adapter2

                            // 슬라이드 자동 전환 설정
                            setupAutoSlide()

                        } catch (e: JSONException) {
                            Log.e("JSONError", "Error parsing JSON: ${e.message}")
                        }
                    } else {
                        Log.e("result2", "Failed to retrieve text content or empty.")
                    }
                }
            }
        }
    }

    private fun parseJson(json: String): MutableList<List<String>> {
        val jsonArray = JSONArray(json)
        val parsedResult = mutableListOf<List<String>>()
        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONArray(i)
            val newsList = listOf(
                item.getString(0), // newsSite
                item.getString(1),
                item.getString(2), // newsTitle
                item.getString(3), // newsInfo
                item.getString(4)  // newsPublisher
            )
            parsedResult.add(newsList)
        }
        return parsedResult
    }

    private fun setupAutoSlide() {
        autoSlideHandler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                // Fragment가 Activity에 연결되어 있는지 확인
                if (isAdded) {
                    val currentItem = binding.viewPager.currentItem
                    val nextItem = if (currentItem == result.size - 1) 0 else currentItem + 1
                    binding.viewPager.setCurrentItem(nextItem, true)
                    autoSlideHandler?.postDelayed(this, 5000) // 5초마다 전환
                }
            }
        }
        autoSlideHandler?.postDelayed(runnable, 5000) // 처음 실행
    }

}