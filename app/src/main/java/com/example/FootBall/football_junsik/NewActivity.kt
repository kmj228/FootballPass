package com.example.FootBall.football_junsik

// https://www.thesportsdb.com/season/4689-South-Korean-K-League-1/2024?csv=1&all=1#csv
// 2부는 234경기 + 승강 2경기 구분 없이 236
// 1부는 228경기
// 승강을 어떻게 할지 고민 (문제: 분명 경기가 먼저 있지만 게임 id가 뒤에 있음)
// Document.querySelector('.label span').textContent
// 경기 시작전: Before Match
// 경기 끝남: End
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.viewModels
import com.example.FootBall.MainTeamList
import com.example.FootBall.R
import com.example.FootBall.databinding.ActivityNewBinding
import com.example.FootBall.football_minjae.TeamDetailsActivity
import com.google.android.material.tabs.TabLayoutMediator
import org.json.JSONException
import org.json.JSONObject

class NewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewBinding
    private lateinit var camera: Camera
    private val textList = arrayListOf("라인업", "세부 정보")
    private val matchDetailInfoShare: MatchDetailInfoShare by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding
        binding = ActivityNewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup TabLayout and ViewPager
        val tabLayout = binding.tabLayout
        val viewPager = binding.viewPager
        val adapter = MatchInfoPagerAdapter(this)

        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = textList[position]
        }.attach()

        // Retrieve intent data
        val dateOfPlay = intent.getStringExtra("DATE")
        val placeOfPlay = intent.getStringExtra("PLACE")
        val homeScore = intent.getStringExtra("HOME_SCORE")
        val awayScore = intent.getStringExtra("AWAY_SCORE")
        val homeImage = intent.getIntExtra("HOME_IMAGE", R.drawable.ic_launcher_foreground)
        val awayImage = intent.getIntExtra("AWAY_IMAGE", R.drawable.ic_launcher_foreground)
        val gameId = intent.getIntExtra("GAMEID", 0)
        val meetSeq = intent.getIntExtra("MEETSEQ", 0)

        // Set UI elements
        binding.playDay.text = dateOfPlay ?: "No date available"
        binding.playPlace.text = placeOfPlay ?: "No place available"
        binding.homeScore.text = homeScore?.ifEmpty { "N" } ?: "N"
        binding.awayScore.text = awayScore?.ifEmpty { "N" } ?: "N"
        binding.homeTeamImage.setImageResource(homeImage)
        binding.awayTeamImage.setImageResource(awayImage)

        binding.homeTeamImage.setOnClickListener {
            val mainTeamList = MainTeamList()

            val homeTeam = mainTeamList.findTeamByImageResource(homeImage)
            val intent = Intent(this, TeamDetailsActivity::class.java).apply {
                putExtra("team", homeTeam) // Parcelable 객체 전달
            }

            startActivity(intent)
        }

        binding.awayTeamImage.setOnClickListener {
            val mainTeamList = MainTeamList()

            val awayTeam = mainTeamList.findTeamByImageResource(homeImage)
            val intent = Intent(this, TeamDetailsActivity::class.java).apply {
                putExtra("team", awayTeam) // Parcelable 객체 전달
            }

            startActivity(intent)
        }

        if (!homeScore.isNullOrEmpty()) {
            setupWebView()
            loadWebViewUrl("https://www.kleague.com/match.do?year=${dateOfPlay!!.split('-')[0]}&leagueId=${if (meetSeq % 2 == 0) { 2 } else { 1 }}&gameId=$gameId&meetSeq=$meetSeq&startTabNum=1")
        }
        else{

        }

        // Initialize camera
        camera = Camera(this)
        binding.cameraIcon.setOnClickListener {
            camera.startCamera()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        camera.onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        camera.onActivityResult(requestCode, resultCode, data)
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
        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.webView.evaluateJavascript("(function() { " +
                        "function extractImageUrl(style) {" +
                        "   var regex = /url\\(['\"]?(.*?)['\"]?\\)/;" +
                        "   var match = style.match(regex);" +
                        "   return match && match[1] ? match[1] : '';" +
                        "}" +
                        "var homeLineUp = [];" +
                        "var awayLineUp = [];" +
                        "var groundHomePlayersContainer = document.querySelectorAll('div.play-record-graph div.lineup:first-child div.ground div.player-data');" +
                        "var groundAwayPlayersContainer = document.querySelectorAll('div.play-record-graph div.lineup:last-child div.ground div.player-data');" +

                        "for(let i = 0; i < groundHomePlayersContainer.length; i++){" +
                        "   const playerName = groundHomePlayersContainer[i].querySelector('p').textContent;" +
                        "   const styleAttr = groundHomePlayersContainer[i].querySelector('div div').getAttribute('style');" +
                        "   const imageUrl = extractImageUrl(styleAttr);" +
                        "   homeLineUp.push([playerName, imageUrl]);}" +

                        "for(let i = 0; i < groundAwayPlayersContainer.length; i++){" +
                        "   const playerName = groundAwayPlayersContainer[i].querySelector('p').textContent;" +
                        "   const styleAttr = groundAwayPlayersContainer[i].querySelector('div div').getAttribute('style');" +
                        "   const imageUrl = extractImageUrl(styleAttr);" +
                        "   awayLineUp.push([playerName, imageUrl]);}" +

                        "var audience = document.querySelector('ul.game-sub-info li:first-child').textContent;" + // 관중수를 크롤링
                        "var versus = document.querySelector('div.versus');" + // 시간과 경기 상태를 모두 담고 있는 요소를 크롤링해서 시간 단축
                        "var time = versus.querySelector('p').textContent.split(' ')[2];" + // 시간 크롤링(년월일과 같이 있어 split 등을 써서 시간만 가져옴)
                        "var status = versus.querySelector('span#matchInfoGameStatus span').textContent;" + //
                        "var homeScorer = document.querySelector('p#scorerAreaHome') ? document.querySelector('p#scorerAreaHome').textContent : ' ';" +
                        "var awayScorer = document.querySelector('p#scorerAreaAway') ? document.querySelector('p#scorerAreaAway').textContent : ' ';" +
                        "var data = document.querySelector('div.graph'); " + // div.graph 아래 요소들을 가져온 다음
                        "if (!data) return JSON.stringify({ error: 'No data found' });" + // 데이터가 없는 경우 반환 수정
                        "var divs = data.querySelectorAll('div:not([class])'); " + // 태그가 div인 애들을 전부 가져온다
                        "var result = []; " + // 데이터를 담을 리스트

                        // 인덱스 번호에 따라 반복
                        "for (let index = 0; index < divs.length; index++) { " + // index를 기준으로 반복
                        "    if (divs[index] && divs[index].children.length > 1) { " + // div가 존재하는지 확인
                        "        var homeElement = divs[index].querySelector('ul li.home p'); " + // 홈 요소 선택
                        "        var awayElement = divs[index].querySelector('ul li.away p'); " + // 어웨이 요소 선택
                        "        if (homeElement && awayElement) { " + // null 체크 추가
                        "            result.push([parseInt(homeElement.textContent) || 0, parseInt(awayElement.textContent) || 0]); " + // 값 저장
                        "        } " +
                        "    } " +
                        "} " +

                        // 모든 값을 하나의 객체로 반환
                        "return { 'audience': audience.trim(), 'time': time, 'status': status, 'homeScorer': homeScorer, 'awayScorer': awayScorer, 'statistics': result, 'homeLineUp': homeLineUp, 'awayLineUp': awayLineUp};" +
                        "})();"
                ) { html ->
                    if (!html.isNullOrEmpty()) {
                        try {
                            // JSON 파싱
                            val jsonObject = JSONObject(html.trim()) // 앞뒤 공백 제거
                            val audience = jsonObject.getString("audience") // 관중수
                            val time = jsonObject.getString("time") // 시간
                            val status = jsonObject.getString("status") // 상태
                            val homeScorer = jsonObject.getString("homeScorer") // 홈 득점자
                            val awayScorer = jsonObject.getString("awayScorer") // 어웨이 득점자
                            val statistics = jsonObject.getJSONArray("statistics") // 통계
                            val homeLineUp = jsonObject.getJSONArray("homeLineUp")
                            val awayLineUp = jsonObject.getJSONArray("awayLineUp")

                            // 배열 길이 확인
                            Log.d("Home Line Up Length", homeLineUp.length().toString())
                            Log.d("Away Line Up Length", awayLineUp.length().toString())

                            // UI 업데이트
                            binding.audience.text = audience
                            binding.matchTime.text = time
                            binding.status.text = status
                            binding.homeScorer.text = homeScorer.replace(' ', '\n')
                            binding.awayScorer.text = awayScorer.replace(' ', '\n')

                            // statistics 배열 처리
                            matchDetailInfoShare.setStatus(statistics)
                            matchDetailInfoShare.setLineUp(homeLineUp, awayLineUp)

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
}
