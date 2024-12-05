package com.example.FootBall.football_junsik


import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import android.widget.Button
import android.widget.ImageView
import com.example.FootBall.MainTeamList
import com.example.FootBall.R
import kotlinx.coroutines.async
import org.jsoup.select.Elements
import java.util.Calendar
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element


class FirstTabContent : Fragment() {

    private var games: ArrayList<GameInfo> = arrayListOf() // 경기 정보를 저장할 리스트
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CustomerAdapter
    private lateinit var dateBtn: Button
    // DB에서 불러와서 화면에 띄우기 위해 배열로 저장
    private var items = arrayListOf<Customer>()

    val BASE_URL = "https://www.kleague.com/schedule.do"
    val SELECT_LEAGUE = "?leagueId="
    val SELECT_YEAR = "&year="
    val SELECT_MONTH = "&month="
    val TEST_DATE = "2024.10.18"

    var playOff = mutableListOf<String>()

    var teamWithId = mapOf("Gangwon FC" to 1,
        "Sangju Sangmu" to 2,
        "Ulsan Hyundai FC" to 3,
        "Pohang Steelers" to 4,
        "Gwangju FC" to 5,
        "Jeonbuk Hyundai Motors" to 6,
        "Incheon United FC" to 7,
        "Daegu FC" to 8,
        "FC Seoul" to 9,
        "Daejeon Hana Citizen" to 10,
        "Jeju United FC" to 11,
        "Suwon FC" to 12,
        "Busan IPark" to 13,
        "Gimpo Citizen" to 14,
        "Gyeongnam FC" to 15,
        "Bucheon FC 1995" to 16,
        "Anyang" to 17,
        "Jeonnam Dragons" to 18,
        "Chungbuk Cheongju" to 19,
        "Seongnam FC" to 20,
        "Chungnam Asan" to 21,
        "Seoul E-Land" to 22,
        "Suwon Samsung Bluewings" to 23,
        "Ansan Greeners" to 24,
        "Cheonan City" to 25
    )
    val mainTeamList = MainTeamList()

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
        dateBtn = view.findViewById(R.id.dateBtn)

        adapter = CustomerAdapter(items)
        recyclerView.adapter = adapter

        //getUrl(1, 2024, 10)

        dateBtn.setOnClickListener {
            showDatePicker() // 날짜 선택 다이얼로그 표시
        }

        // 데이터 목록 생성
        loadInitialData()
    }

    private fun loadInitialData() {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val date = String.format("%d-%02d-%02d", year, cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DATE))
        getInfo(date, year.toString()) // 초기 데이터 로딩
    }

    private fun showDatePicker() {
        val cal = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val selectedDate = String.format("%d-%02d-%02d", year, month + 1, day)
                getInfo(selectedDate, year.toString()) // 선택된 날짜로 데이터 새로고침
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )

        // Fragment가 활성 상태인지 확인
        if (isAdded) {
            datePickerDialog.show() // DatePickerDialog 보여주기
        } else {
            Log.w("DatePicker", "Fragment is not added, cannot show DatePicker.")
        }
    }

    private fun getInfo(findDate: String, year: String) {
        val k1Url = "https://www.thesportsdb.com/season/4689-South-Korean-K-League-1/${year}?csv=1&all=1#csv"
        val k2Url = "https://www.thesportsdb.com/season/4822-South-Korean-K-League-2/${year}?csv=1&all=1#csv"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val k1Deferred = async { Jsoup.connect(k1Url).get() }
                val k2Deferred = async { Jsoup.connect(k2Url).get() }

                val k1Document = k1Deferred.await()
                val k2Document = k2Deferred.await()

                val k1ScheduleElements = k1Document.select("textarea#myInput")
                val k2ScheduleElements = k2Document.select("textarea#myInput")

                Log.d("ScheduleElements", "K1 Found elements: ${k1ScheduleElements.size}, K2 Found elements: ${k2ScheduleElements.size}")
                games.clear() // 기존 게임 정보 초기화
                // K1 데이터 처리
                if (k1ScheduleElements.isNotEmpty()) {
                    processSchedule(k1ScheduleElements, findDate, 1)
                } else {
                    Log.e("WebCrawl", "No K1 schedule elements found.")
                }

                // K2 데이터 처리
                if (k2ScheduleElements.isNotEmpty()) {
                    processSchedule(k2ScheduleElements, findDate, 2)
                } else {
                    Log.e("WebCrawl", "No K2 schedule elements found.")
                }

                withContext(Dispatchers.Main) {
                    if (isAdded && isVisible) {
                        items.clear()
                        items.addAll(games.map { gameInfo ->
                            Customer(gameInfo.date, "", gameInfo.homeTeam.home, gameInfo.homeTeam.name, gameInfo.awayTeam.name, gameInfo.homeScore, gameInfo.awayScore, gameInfo.homeTeam.profileImage, gameInfo.awayTeam.profileImage, gameInfo.gameId, gameInfo.meetSeq)
                        })
                        adapter.notifyDataSetChanged()
                    } else {
                        Log.w("UI Update", "Activity is not added or visible, skipping UI update.")
                    }
                }

            } catch (e: Exception) {
                Log.e("WebCrawl", "Error: ${e.message}")
            }
        }
    }

    private fun processSchedule(scheduleElements: Elements, findDate: String, league: Int){
        val str = scheduleElements.outerHtml().toString().split(',')
        playOff.clear()
        var gameId = 0
        for (i in 1 until str.size step 7) {
            val date = str[i]

            // 만약 다음 경기가 이번 경기와 같은 날짜이며, 같은 팀끼리 한다면 카운트 안함
            if((i + 9 < str.size)&&(((str[i+2] == str[i+9]) || (str[i+4] == str[i+9])) && date == str[i+7])){
                continue
            }
            gameId += 1

            var meetSeq = league
            //TODO: 여기에 meetseq도 추가해줘야함
            if((league == 1 && gameId>228) || playOff.isNotEmpty()){
                val awayTeam = str[i + 4]
                // 홈팀이 다음 경기에서는 어웨이가 됨
                gameId = if (awayTeam in playOff) {
                    playOff.indexOf(awayTeam) + 3 // 기존 팀이면 인덱스 + 3
                } else {
                    playOff.add(str[i + 2]) // 새로운 팀 추가
                    playOff.size // 현재 플레이오프 팀 수
                }

                meetSeq = 3
            }

            if (findDate == date) {
                Log.d("date", date)
                val homeTeam = str[i + 2]
                val awayTeam = str[i + 4]
                val homeScore = str[i + 3]
                val awayScore = str[i + 5]

                val homeTeamId = teamWithId[homeTeam]
                val awayTeamId = teamWithId[awayTeam]
                
                // 승강 플레이오프는 먼저 경기를 한 순서로 ID가 주어지지 않고
                // 먼저 붙은 팀은 1이고, 먼저 붙은 팀들이 다른 팀들보다 먼저 2번째 경기를 해도 3번째 경기로 친다.


                if (homeTeamId != null && awayTeamId != null) {
                    Log.d("gameId", gameId.toString()) // 1, 3
                    Log.d("meetSeq", meetSeq.toString())

                    games.add(
                        GameInfo(
                            date,
                            mainTeamList.getByPosMainTeamList(homeTeamId),
                            mainTeamList.getByPosMainTeamList(awayTeamId),
                            homeScore,
                            awayScore,
                            gameId,
                            meetSeq
                        )
                    )
                }
            }
        }
    }
}
