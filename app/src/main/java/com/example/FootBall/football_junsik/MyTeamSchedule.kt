package com.example.FootBall.football_junsik

import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.FootBall.MainTeamList
import com.example.FootBall.MyApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.select.Elements


// 본래의 의도는 크롤링의 함수화 하여 자신의 팀 일정을 DB저장함
/*
class MyTeamSchedule: Fragment(){
    private var items = arrayListOf<Customer>()
    private var games: ArrayList<GameInfo> = arrayListOf()
    var playOff = mutableListOf<String>()
    lateinit var userFavoritTeam: String
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

    interface ScheduleCallback {
        fun onScheduleLoaded(items: List<Customer>)
        fun onError(exception: Exception)
    }
    // pos = 1 : 전체 일정 중 날짜를 비교, 2 : 팀의 1년 일정을 리스트로
    public fun getInfo(findDate: String, year: String, pos: Int, callback: ScheduleCallback): ArrayList<Customer> {
        val k1Url =
            "https://www.thesportsdb.com/season/4689-South-Korean-K-League-1/${year}?csv=1&all=1#csv"
        val k2Url =
            "https://www.thesportsdb.com/season/4822-South-Korean-K-League-2/${year}?csv=1&all=1#csv"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val k1Deferred = async { Jsoup.connect(k1Url).get() }
                val k2Deferred = async { Jsoup.connect(k2Url).get() }

                val k1Document = k1Deferred.await()
                val k2Document = k2Deferred.await()

                val k1ScheduleElements = k1Document.select("textarea#myInput")
                val k2ScheduleElements = k2Document.select("textarea#myInput")

                Log.d(
                    "ScheduleElements",
                    "K1 Found elements: ${k1ScheduleElements.size}, K2 Found elements: ${k2ScheduleElements.size}"
                )
                games.clear() // 기존 게임 정보 초기화
                // K1 데이터 처리
                if (k1ScheduleElements.isNotEmpty()) {
                    processSchedule(k1ScheduleElements, findDate, 1, pos)
                } else {
                    Log.e("WebCrawl", "No K1 schedule elements found.")
                }

                // K2 데이터 처리
                if (k2ScheduleElements.isNotEmpty()) {
                    processSchedule(k2ScheduleElements, findDate, 2, pos)
                } else {
                    Log.e("WebCrawl", "No K2 schedule elements found.")
                }

                withContext(Dispatchers.Main) {
                    items.clear()
                    items.addAll(games.map { gameInfo ->
                        Customer(
                            gameInfo.date,
                            gameInfo.homeTeam.home,
                            gameInfo.homeTeam.name,
                            gameInfo.awayTeam.name,
                            gameInfo.homeScore,
                            gameInfo.awayScore,
                            gameInfo.homeTeam.profileImage,
                            gameInfo.awayTeam.profileImage,
                            gameInfo.gameId,
                            gameInfo.meetSeq
                        )
                    })
                    callback.onScheduleLoaded(items) // 콜백 호출
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onError(e) // 에러 발생 시 콜백 호출
                }
            }
        }
        return items
    }

    private fun processSchedule(scheduleElements: Elements, findDate: String, league: Int, pos: Int){
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
            if (pos == 1){
                if (findDate == date) {
                    //Log.d("date", date)
                    val homeTeam = str[i + 2]
                    val awayTeam = str[i + 4]
                    val homeScore = str[i + 3]
                    val awayScore = str[i + 5]

                    val homeTeamId = mainTeamList.findTeamNameEngToId(homeTeam)
                    val awayTeamId = mainTeamList.findTeamNameEngToId(awayTeam)

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
            else{
                // 자기가 좋아하는 팀의 일정을 리스트 형태로 반환
                val app = requireActivity().application as MyApplication
                val user = app.currentUser

                // 사용자 데이터 갱신
                if (user != null) {

                    if (user.team != "없음")
                        userFavoritTeam = mainTeamList.findTeamNameKorToEng(user.team).toString()
                    else{
                        userFavoritTeam = "Suwon Samsung Bluewings"
                    }


                } else {
                    Toast.makeText(
                        requireContext(),
                        "사용자 데이터를 읽어오지 못하였습니다. 로그아웃 후 다시 로그인해주세요",
                        Toast.LENGTH_SHORT
                    ).show()
                    userFavoritTeam = "Suwon Samsung Bluewings"
                }


                if (findDate == date) {
                    //Log.d("date", date)
                    val homeTeam = str[i + 2]
                    val awayTeam = str[i + 4]
                    val homeScore = str[i + 3]
                    val awayScore = str[i + 5]

                    val homeTeamId = mainTeamList.findTeamNameEngToId(homeTeam)
                    val awayTeamId = mainTeamList.findTeamNameEngToId(awayTeam)

                    // 승강 플레이오프는 먼저 경기를 한 순서로 ID가 주어지지 않고
                    // 먼저 붙은 팀은 1이고, 먼저 붙은 팀들이 다른 팀들보다 먼저 2번째 경기를 해도 3번째 경기로 친다.


                    if (homeTeamId != null && awayTeamId != null) {

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
}

 */