package com.example.FootBall.football_minjae

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.FootBall.MainTeamList
import com.example.FootBall.R
import com.example.FootBall.Team
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

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
) : Parcelable {
    constructor(parcel: Parcel) : this(
        imageUrl = parcel.readString() ?: "",
        name = parcel.readString() ?: "",
        position = parcel.readString() ?: "",
        nationality = parcel.readString() ?: "",
        jerseyNumber = parcel.readString() ?: "",
        height = parcel.readString() ?: "",
        weight = parcel.readString() ?: "",
        birthDate = parcel.readString() ?: "",
        playerId = parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(imageUrl)
        parcel.writeString(name)
        parcel.writeString(position)
        parcel.writeString(nationality)
        parcel.writeString(jerseyNumber)
        parcel.writeString(height)
        parcel.writeString(weight)
        parcel.writeString(birthDate)
        parcel.writeString(playerId)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<PlayerInfo> {
        override fun createFromParcel(parcel: Parcel): PlayerInfo {
            return PlayerInfo(parcel)
        }

        override fun newArray(size: Int): Array<PlayerInfo?> {
            return arrayOfNulls(size)
        }
    }
}

class TeamDetailsActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var adapter: PlayerImageAdapter
    private val players = ArrayList<PlayerInfo>() // PlayerInfo 리스트

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_details)

        listView = findViewById(R.id.my_list_view)

        // Intent로 전달된 Parcelable 객체인지 확인
        var team: Team? = intent.getParcelableExtra("team")

        if (team != null) {
            // Parcelable 객체로 전달된 경우 처리
            Log.d("TeamDetailsActivity", "Team received via Parcelable: ${team.name}")
        } else {

            val teamurl = intent.getStringExtra("team") // 팀의 이름을 가져온다.
            team = teamurl?.let { MainTeamList().findURLByName(it) }
        }


        Log.d("넘겨받은 팀", team.toString())

        findViewById<ImageView>(R.id.teamLocation)

        if (team != null) {
            findViewById<ImageView>(R.id.teamProfile).setImageResource(team.profileImage)
            findViewById<TextView>(R.id.teamName).text = team.name
            findViewById<TextView>(R.id.teamDescription).text = "연고지 : ${team.region}"
            findViewById<TextView>(R.id.teamHome).apply {
                text = "홈구장 : ${team.home}"
                paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG

                setOnClickListener {
                    try {
                        val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(team.address)}")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)

                        // Google Maps 앱 우선 설정 (선택 사항)
                        mapIntent.setPackage("com.google.android.apps.maps")

                        val chooser = Intent.createChooser(mapIntent, "지도 앱 선택")
                        if (mapIntent.resolveActivity(context.packageManager) != null) {
                            startActivity(chooser)
                        } else {
                            showToast("지도 앱을 찾을 수 없습니다")
                        }
                    } catch (e: Exception) {
                        showToast("지도를 열 수 없습니다: ${e.message}")
                    }
                }
            }


            findViewById<ImageView>(R.id.teamLocation).apply {
                setOnClickListener {
                    val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(team.address)}")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)

                    // Intent Chooser를 통해 사용자에게 앱 선택을 제공
                    val chooser = Intent.createChooser(mapIntent, "지도 앱 선택")
                    if (mapIntent.resolveActivity(packageManager) != null) {
                        startActivity(chooser)
                    } else {
                        showToast("지도 앱을 찾을 수 없습니다")
                    }
                }
            }


            findViewById<TextView>(R.id.teamLeague).text = "리그 : ${team.league}"
            findViewById<TextView>(R.id.teamSupporters).text = "대표 서포터즈 : ${team.supporters}"

            val teamCheerSongButton = findViewById<Button>(R.id.teamCheerSongButton)
            teamCheerSongButton.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(team.cheerSong))
                startActivity(intent)
            }

            adapter = PlayerImageAdapter(this, R.layout.player_list_item, players, team.name)
            listView.adapter = adapter

            if (team.league == "K리그 1") {
                fetchPlayerData("1", team.kLeagueTeamId)
            }
            else {
                fetchPlayerData("2", team.kLeagueTeamId)
            }
        }
    }

    private fun fetchPlayerData(leagueId: String, teamId: String) {
        lifecycleScope.launch {
            try {
                var page = 1
                var hasMorePages = true

                while (hasMorePages) {
                    // 각 페이지의 데이터를 가져온다
                    val htmlContent = RetrofitClient.apiService.getPlayers("active", leagueId, teamId, page)
                    val playerCount = parseHtml(htmlContent) // 현재 페이지의 선수 데이터를 파싱

                    // 페이지에 데이터가 없거나 마지막 페이지에 도달한 경우 종료
                    hasMorePages = playerCount > 0
                    page++
                }

                fetchPlayerDetails() // 모든 페이지의 선수에 대한 세부 정보를 가져온다.
                updateListView()
            } catch (e: Exception) {
                Log.e("Fetch Error", "Error fetching data: ${e.message}")
                showToast("데이터를 가져오는데 실패했습니다")
            }
        }
    }


    private suspend fun fetchPlayerDetails() {
        for (player in players) {
            try {
                val detailHtml = withContext(Dispatchers.IO) {
                    val detailUrl = "https://www.kleague.com/record/playerDetail.do?playerId=${player.playerId}"
                    Jsoup.connect(detailUrl).get()
                }

                // HTML에서 모든 <tr> 태그를 선택
                val rows = detailHtml.select("div.cont-box.right.player-rank table.style2.center tbody tr")

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

            } catch (e: Exception) {
                Log.e("Detail Fetch Error", "Error fetching details for player: ${player.name}, error: ${e.message}")
            }
        }
    }

    private fun parseHtml(html: String): Int {
        val document = Jsoup.parse(html) // HTML 문서를 파싱
        val divdata = document.select("div.cont.active div.player.f-wrap")
        val rows = divdata.select("div.cont-box.f-wrap.left.player-hover")

        var addedPlayers = 0

        for (row in rows) {
            val imgBox = row.selectFirst("div.img-box img")
            val txtBox = row.selectFirst("div.txt-box")

            val imageUrl = imgBox?.attr("src") ?: ""
            val fullImageUrl =
                if (imageUrl.startsWith("http")) imageUrl else "https://www.kleague.com$imageUrl"

            val jerseyNumber = txtBox?.selectFirst("span.num")?.text() ?: "No Number"

            if (!jerseyNumber.startsWith("No.")) {
                Log.d("Filtered Out", "Skipped: $jerseyNumber")
                continue
            }

            val nameElement = txtBox?.selectFirst("span.name")
            val name = nameElement?.ownText() ?: "Unknown Player"
            val position = txtBox?.selectFirst("span.position")?.text() ?: "Unknown Position"
            val nationality = txtBox?.selectFirst("span.nationality")?.text() ?: "Unknown Nationality"

            val onclickAttr = row.attr("onclick")
            val playerId = Regex("""onPlayerClicked\((\d+)\)""").find(onclickAttr)?.groupValues?.get(1) ?: ""

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
            addedPlayers++
        }

        return addedPlayers
    }

    private fun updateListView() {
        if (players.isEmpty()) {
            showToast("선수 정보를 찾을 수 없습니다")
        } else {
            // Adapter 업데이트 및 데이터 표시
            adapter.notifyDataSetChanged()
        }
    }

    interface ApiService {
        @GET("player.do")
        suspend fun getPlayers(
            @Query("type") type: String,
            @Query("leagueId") leagueId: String,
            @Query("teamId") teamId: String,
            @Query("page") page: Int
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

    private fun showToast(message: String) {
        Toast.makeText(this@TeamDetailsActivity, message, Toast.LENGTH_SHORT).show()
    }

}
