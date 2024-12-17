package com.example.FootBall.footBall_damyeong.boardAndPost.boardSelectAndCreate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ListView
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.FootBall.FireStoreConnection
import com.example.FootBall.MyApplication
import com.example.FootBall.R
import com.example.FootBall.footBall_damyeong.boardAndPost.BoardActivity
import com.example.FootBall.ToMinjaeActivity
import com.example.FootBall.databinding.FragmentPublicBoardsBinding
import com.example.FootBall.footBall_damyeong.SlideAdapter
import com.example.FootBall.footBall_damyeong.boardAndPost.Bus.BusReservationActivity
import org.json.JSONArray
import org.json.JSONException
import java.util.Calendar
import java.util.Timer
import java.util.TimerTask

class PublicBoardsFragment : Fragment() {
    private val boardList = ArrayList<BoardListItem>()
    private lateinit var adapter: BoardListAdapter
    private var _binding: FragmentPublicBoardsBinding? = null
    private val binding get() = _binding!!
    lateinit var adapter2: SlideAdapter
    var result: MutableList<List<String>> = mutableListOf()

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
                    } else if (board.boardName == user.team) {
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
            val myIntent=Intent(requireContext(), BusReservationActivity::class.java)
            startActivity(myIntent)
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        adapter.clear() // 어댑터 리소스 정리 (예: 데이터 초기화)
        _binding = null
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
        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                requireActivity().runOnUiThread {
                    val currentItem = binding.viewPager.currentItem
                    // 마지막 슬라이드일 경우, 첫 번째 슬라이드로 즉시 이동
                    if (currentItem == result.size - 1) {
                        binding.viewPager.setCurrentItem(0, false) // false를 사용하여 애니메이션 없이 즉시 이동
                    } else {
                        val nextItem = currentItem + 1
                        binding.viewPager.setCurrentItem(nextItem, true) // 다음 슬라이드로 전환
                    }
                }
            }
        }, 5000, 5000) // 5초마다 전환
    }

}