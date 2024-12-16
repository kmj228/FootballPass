package com.example.FootBall.football_minjae

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.FootBall.MainTeamList
import com.example.FootBall.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class TeamListFragment : Fragment() {

    private val teamList = MainTeamList().getMainTeamList()
    private lateinit var recyclerView: RecyclerView
    //private lateinit var adapter: TeamAdapter
    private var currentFilter: String = "K리그 1" // 초기 필터 설정
    lateinit var webView: WebView
    lateinit var adapter: TeamRankAdapterFragment
    var result: MutableList<List<String>> = mutableListOf()
    val rankOfKLeague =  mutableListOf<MutableList<String>>()
    var league = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.activity_team_list, container, false)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // 초기 어댑터 설정
        //adapter = TeamAdapter(teamList.filter { it.league == currentFilter })
        //recyclerView.adapter = adapter
        adapter = TeamRankAdapterFragment(rankOfKLeague)
        recyclerView.adapter = adapter

        fetchHtml()

        // 필터 버튼 처리
        val btnKLeague1: Button = view.findViewById(R.id.btnKLeague1)
        val btnKLeague2: Button = view.findViewById(R.id.btnKLeague2)

        btnKLeague1.setOnClickListener {
            currentFilter = "K리그 1"
            league = 1
            fetchHtml()
        }

        btnKLeague2.setOnClickListener {
            currentFilter = "K리그 2"
            league = 2
            fetchHtml()
        }
    }


    private fun fetchHtml() {
        lifecycleScope.launch {
            try {
                // 필요한 파라미터를 설정하여 HTML을 가져옵니다.
                var kleague = "kleague" + if(league == 1) "" else "2"
                val htmlContent = RetrofitClient.apiService.getPlayers(category = kleague, year = 2024)
                parseHtml(htmlContent)
            } catch (e: HttpException) {
                // 오류 처리
                //textView.text = "Error: ${e.message()}"
            } catch (e: Exception) {
                //textView.text = "Unexpected error: ${e.message}"
            }
        }
    }

    private suspend fun parseHtml(html: String) {
        withContext(Dispatchers.IO) {
            // Jsoup을 사용하여 HTML 파싱
            val document: Document = Jsoup.parse(html)
            var teamElements: Elements
            rankOfKLeague.clear()
            // k2
            if(league != 1){
                teamElements = document.select("tbody#regularGroup_table tr") // 예시로 tr 태그 선택

                for(i in 0 until teamElements.size){
                    //:not([class]) 클래스가 없다는 뜻, .attr("src")
                    val teamImageURL = teamElements[i].select("td.tm div span.emblem img").attr("src")
                    val teamInfo = teamElements[i].text().split(' ').toMutableList()
                    teamInfo.add(teamImageURL)

                    rankOfKLeague.add(teamInfo)

                }
            }
            // k1
            else{
                teamElements = document.select("div.tbl_box.type3 table tbody tr") // 예시로 tr 태그 선택
                for(i in 0 until teamElements.size){
                    //:not([class]) 클래스가 없다는 뜻, .attr("src")
                    val teamImageURL = teamElements[i].select("td.tm div span.emblem img").attr("src")
                    //val teamName = teamElements[i].select("td.tm div span").text()
                    val teamInfo = teamElements[i].text().split(' ').toMutableList()
                    teamInfo.add(teamImageURL)

                    rankOfKLeague.add(teamInfo)

                }

            }




            // UI 업데이트는 메인 스레드에서 수행해야 합니다.
            withContext(Dispatchers.Main) {
                // 선수 이름을 TextView에 설정
                //textView.text = teamElements.joinToString("\n") { it.text() } // 각 행의 텍스트를 가져와서 표시
                //Log.d("result", teamElements.joinToString("\n") { it.text() })
                adapter.notifyDataSetChanged()
            }
        }
    }

    interface ApiService {
        @GET("index") // 엔드포인트 설정
        suspend fun getPlayers(
            @Query("category") category: String,
            @Query("year") year: Int
        ): String
    }

    object RetrofitClient {
        private const val BASE_URL = "https://sports.news.naver.com/kfootball/record/"

        private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()

        val apiService: ApiService = retrofit.create(ApiService::class.java)
    }
}
