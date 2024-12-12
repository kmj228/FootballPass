package com.example.FootBall

import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONException
import java.util.Calendar

class ToMinjaeActivity : AppCompatActivity() {

    lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webView = findViewById(R.id.webView)

        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val date = String.format("%d%02d%02d", year, cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DATE))

        // url에 있는 뉴스 기사 가져오기
        setupWebView()
        loadWebViewUrl("https://sports.news.naver.com/kfootball/news/index?isphoto=N&type=popular&date=$date")
    }

    private fun setupWebView() {
        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.mediaPlaybackRequiresUserGesture = false

        // User-Agent 설정해서 PC와 비슷하게 만든다.
        val userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36"
        webView.settings.userAgentString = userAgent
    }

    private fun loadWebViewUrl(url: String) {
        webView.loadUrl(url)
        Log.d("크롤링 시작", "시작")

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                webView.evaluateJavascript("""
                (function() {
                    var kLeagueNewsList = [];
                    var popularNewsElements = document.querySelector('div.news_list ul');

                    // 최대 5개의 뉴스 항목을 가져온다
                    var newsItems = popularNewsElements.querySelectorAll('div.text');
                    for (let i = 0; i < Math.min(5, newsItems.length); i++) {
                        var news = newsItems[i];

                        // 각 뉴스 항목에서 필요한 정보 추출
                        var newsSite = news.querySelector('a').href; // href 속성 가져오기
                        var newsTitle = news.querySelector('a').textContent; // 뉴스 제목
                        var newsInfo = news.querySelector('p').textContent; // 뉴스 정보
                        var newsPublisher = news.querySelector('div.source span.press').textContent; // 뉴스 출처

                        kLeagueNewsList.push([newsSite, newsTitle, newsInfo, newsPublisher]);
                    }

                    return kLeagueNewsList;
                })()
            """.trimIndent()) { html ->
                    if (!html.isNullOrEmpty()) {
                        try {
                            // JSON 파싱
                            Log.d("result", html)

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