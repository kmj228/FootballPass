package com.example.FootBall.football_junsik

/*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Element



class SecondTabContent : Fragment() {

    var games: ArrayList<GameInfo> = arrayListOf() // 경기 정보를 저장할 리스트
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CustomerAdapter
    private lateinit var itemList: List<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Fragment의 레이아웃을 설정
        return inflater.inflate(R.layout.activity_first_tab_content, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val searchSite = "https://www.thesportsdb.com/season/4689-South-Korean-K-League-1/2024?csv=1&all=1#csv"
        val date = "2024-03-01" // 년월일을 받을 수 있음
        // 데이터 목록 생성
        getInfo(searchSite, date)

        // DB에서 불러와서 화면에 띄우기 위해 배열로 저장
        val items = arrayListOf<Customer>()

        for(gameInfo:GameInfo in games){
            items.add(Customer(gameInfo.date, "", "경기장", gameInfo.homeTeam, gameInfo.awayTeam, gameInfo.homeScore, gameInfo.awayScore))
        }

        adapter = CustomerAdapter(items)
        recyclerView.adapter = adapter
    }

    fun getInfo(url: String, findDate: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Jsoup을 사용하여 HTML 문서 가져오기
                val document = Jsoup.connect(url)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                    .get()

                Log.d("SearchStarts", "Starts!!!!")

                // 요일별 경기 일정 추출
                val scheduleElements = document.select("textarea#myInput") // CSS 선택자로 일정 요소 선택
                Log.d("ScheduleElements", "Found elements: ${scheduleElements.size}")

                if (scheduleElements.isEmpty()) {
                    Log.e("WebCrawl", "No schedule elements found.")
                    return@launch
                }

                // scheduleElements의 HTML 출력
                val scheduleHtml = StringBuilder() // HTML을 저장할 StringBuilder
                for (element: Element in scheduleElements) {
                    // 각 요소의 HTML 추가
                    scheduleHtml.append(element.outerHtml()).append("end \n")
                }
                Log.d("ScheduleElementsHTML", scheduleHtml.toString()) // 전체 HTML 출력

                val str =  scheduleElements.outerHtml().toString().split(',')
                val len = str.size

                for(i in 1..len-1 step 7){
                    val date = str[i]
                    Log.d("findDate", date)

                    if(findDate in date){
                        val homeTeam = str[i+2]
                        val awayTeam = str[i+4]
                        val homeScore = str[i+3]
                        val awayScore = str[i+5]

                        Log.d("findDate", date)

                        games.add(GameInfo(date, homeTeam, awayTeam, homeScore, awayScore))
                    }
                }

                // UI 업데이트는 Main Thread에서 수행

                withContext(Dispatchers.Main) {
                    if (games.isEmpty()) {
                        Log.e("Not Found Data", "없음")
                    } else {
                        Log.d("Found Data", "찾음")
                    }
                }

            } catch (e: Exception) {
                Log.e("WebCrawl", "Error: ${e.message}")
            }
        }


    }
}

 */