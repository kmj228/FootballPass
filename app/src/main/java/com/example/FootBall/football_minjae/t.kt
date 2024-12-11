/*
package com.example.soccer3

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.FootBall.football_minjae.PlayerImageAdapter
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


data class PlayerInfo(
    var imageUrl: String = "",
    var name: String = "",
    var position: String = "",
    var nationality: String = "",
    var jerseyNumber: String = "",
    var height: String = "",
    var weight: String = "",
    var birthDate: String = "",
    var playerId: String = ""
)


class MainActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var fetchButton: Button
    private lateinit var leagueInput: EditText
    private lateinit var teamInput: EditText
    private lateinit var adapter: PlayerImageAdapter
    private val players = ArrayList<PlayerInfo>() // PlayerInfo 리스트
    private val hashMap: HashMap<String, String> = HashMap() // Key와 Value 타입 지정

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // UI 컴포넌트 초기화
        listView = findViewById(R.id.resultListView)
        fetchButton = findViewById(R.id.fetchButton)
        leagueInput = findViewById(R.id.leagueInput)
        teamInput = findViewById(R.id.teamInput)

        // 어댑터 생성
        adapter = PlayerImageAdapter(this, R.layout.player_item, players)
        listView.adapter = adapter

        // 버튼 클릭 이벤트 설정
        fetchButton.setOnClickListener {
            val leagueId = leagueInput.text.toString().trim()
            val teamId = teamInput.text.toString().trim()

            if (leagueId.isNotEmpty() && teamId.isNotEmpty()) {
                fetchPlayerData(leagueId, teamId)
            } else {
                Toast.makeText(this, "리그 ID와 팀 ID를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchPlayerData(leagueId: String, teamId: String) {
        lifecycleScope.launch {
            try {
                val htmlContent = RetrofitClient.apiService.getPlayers("active", leagueId, teamId)
                parseHtml(htmlContent)
                fetchPlayerDetails() // 각 선수의 세부 정보를 가져온다.
                updateListView()
            } catch (e: Exception) {
                Log.e("Fetch Error", "Error fetching data: ${e.message}")
                Toast.makeText(this@MainActivity, "데이터를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }




    private suspend fun fetchPlayerDetails() {
        for (player in players) {
            try {
                val detailHtml = withContext(Dispatchers.IO) {
                    val detailUrl = "https://www.kleague.com/record/playerDetail.do?playerId=${player.playerId}"
                    Log.d("Detail URL", "Fetching: $detailUrl")
                    Jsoup.connect(detailUrl).get()
                }

                // HTML에서 모든 <tr> 태그를 선택
                val rows = detailHtml.select("div.cont-box.right.player-rank table.style2.center tbody tr")
                Log.d("Player Info Rows", "Rows found: ${rows.size}")

                // 각 정보를 저장할 변수
                var name = player.name
                var position = player.position
                var jerseyNumber = player.jerseyNumber
                var nationality = player.nationality
                var height = player.height
                var weight = player.weight
                var birthDate = player.birthDate

                // 각 <tr>에서 데이터 가져오기
                for (row in rows) {
                    val headers = row.select("th") // <th> 태그들
                    val values = row.select("td") // <td> 태그들

                    // <th>와 <td>가 쌍을 이루는 경우
                    for (i in headers.indices) {
                        val header = headers[i].text().trim()
                        val value = if (i < values.size) values[i].text().trim() else ""

                        Log.d("Player Info Row", "Header: $header, Value: $value")

                        when (header) {
                            "이름" -> name = value
                            "포지션" -> position = value
                            "배번" -> jerseyNumber = value
                            "국적" -> nationality = value
                            "키" -> height = value
                            "몸무게" -> weight = value
                            "생년월일" -> birthDate = value
                        }
                    }
                }

                // PlayerInfo 업데이트
                player.name = name
                player.position = position
                player.jerseyNumber = jerseyNumber
                player.nationality = nationality
                player.height = height
                player.weight = weight
                player.birthDate = birthDate

                Log.d(
                    "Player Updated Info",
                    "Name: $name, Position: $position, Jersey Number: $jerseyNumber, Nationality: $nationality, Height: $height, Weight: $weight, BirthDate: $birthDate"
                )
            } catch (e: Exception) {
                Log.e("Detail Fetch Error", "Error fetching details for player: ${player.name}, error: ${e.message}")
            }
        }
    }







    private fun parseHtml(html: String) {
        val document = Jsoup.parse(html) // HTML 문서를 파싱
        val divdata = document.select("div.cont.active div.player.f-wrap")
        val rows = divdata.select("div.cont-box.f-wrap.left.player-hover")

        Log.d("parseHtml", rows.size.toString())
        players.clear() // 기존 데이터를 지워줍니다.

        for (row in rows) {
            val imgBox = row.selectFirst("div.img-box img")
            val txtBox = row.selectFirst("div.txt-box")

            // 이미지 URL
            val imageUrl = imgBox?.attr("src") ?: ""
            val fullImageUrl =
                if (imageUrl.startsWith("http")) imageUrl else "https://www.kleague.com$imageUrl"

            // txt-box 내부 정보
            val jerseyNumber = txtBox?.selectFirst("span.num")?.text() ?: "No Number"

            // 'No'가 포함되지 않으면 건너뛴다.
            if (!jerseyNumber.startsWith("No.")) {
                Log.d("Filtered Out", "Skipped: $jerseyNumber")
                continue
            }

            val nameElement = txtBox?.selectFirst("span.name")
            val name = nameElement?.ownText() ?: "Unknown Player"
            val position = txtBox?.selectFirst("span.position")?.text() ?: "Unknown Position"
            val nationality = txtBox?.selectFirst("span.nationality")?.text() ?: "Unknown Nationality"

            // playerId 추출
            val onclickAttr = row.attr("onclick") // onclick 속성 추출
            val playerId = Regex("""onPlayerClicked\((\d+)\)""").find(onclickAttr)?.groupValues?.get(1) ?: ""

            Log.d("Player ID", "Extracted Player ID for $name: $playerId")

            // PlayerInfo 객체 추가
            players.add(
                PlayerInfo(
                    imageUrl = fullImageUrl,
                    name = name,
                    position = position,
                    nationality = nationality,
                    jerseyNumber = jerseyNumber,
                    playerId = playerId
                )
            )
        }

        Log.d("Parsed Players", "Total players parsed: ${players.size}")
    }





    private fun updateListView() {
        if (players.isEmpty()) {
            Toast.makeText(this, "선수 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
        } else {
            // Adapter 업데이트 및 데이터 표시
            adapter.notifyDataSetChanged()
            Log.d("updateListView", "List updated with ${players.size} players")
        }
    }

    interface ApiService {
        @GET("player.do")
        suspend fun getPlayers(
            @Query("type") type: String,
            @Query("leagueId") leagueId: String,
            @Query("teamId") teamId: String
        ): String
    }

    object RetrofitClient {
        private const val BASE_URL = "https://www.kleague.com/"

        val apiService: ApiService = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

 */