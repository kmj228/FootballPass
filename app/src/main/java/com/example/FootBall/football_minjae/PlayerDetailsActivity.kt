package com.example.FootBall.football_minjae

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.FootBall.R

class PlayerDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_details)

        // Intent로 전달된 PlayerInfo 객체 받기
        val player = intent.getParcelableExtra<PlayerInfo>("player")

        if (player == null) {
            Log.e("PlayerDetailsActivity", "플레이어 데이터가 비어있음")
            finish()
            return
        }

        // 뷰 요소들 초기화 및 데이터 바인딩
        val imageView = findViewById<ImageView>(R.id.playerProfile)
        val nameView = findViewById<TextView>(R.id.playerName)

        val nationalityView = findViewById<TextView>(R.id.playerNationality)
        val numView = findViewById<TextView>(R.id.playerNum)
        val posView = findViewById<TextView>(R.id.playerPos)
        val birthdateView = findViewById<TextView>(R.id.playerBirthdate)
        val statsView = findViewById<TextView>(R.id.playerStats)

        // 이미지 및 텍스트 데이터 설정
        Glide.with(this).load(player.imageUrl).into(imageView)
        nameView.text = player.name
        nationalityView.text = "국적 : ${player.nationality}"
        numView.text = "등번호 : ${player.jerseyNumber}"
        posView.text = "포지션 : ${player.position}"
        birthdateView.text = "생년월일 : ${player.birthDate}"
        statsView.text = "신장/체중 : ${player.height}/${player.weight}"
    }
}
