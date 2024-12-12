package com.example.FootBall.football_minjae

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.FootBall.FireStoreConnection
import com.example.FootBall.R

class PlayerDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_details)

        // Intent로 전달된 PlayerInfo 객체 받기
        val player = intent.getParcelableExtra<PlayerInfo>("player")
        val teamName = intent.getStringExtra("teamName") // 추가: 팀 이름 받기

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

        try {
            FireStoreConnection.onGetDocument("/teamData/${teamName}/playerList/${player.name}") { document ->
                document.getString("song")?.let {
                    val song = it

                    val cheerSongButton = findViewById<Button>(R.id.cheerSongButton)

                    if (song.isNullOrEmpty()) {
                        cheerSongButton.setOnClickListener {
                            Toast.makeText(this, "응원곡이 없습니다", Toast.LENGTH_SHORT).show()
                        }
                    } else if (song.contains("http")) {
                        cheerSongButton.setOnClickListener {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(song))
                            startActivity(intent)
                        }
                    } else {
                        findViewById<TextView>(R.id.playerSong).text = "응원곡 : ${song}"

                        cheerSongButton.setOnClickListener {
                            Toast.makeText(this, "응원곡 링크가 없습니다", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "데이터를 불러오는 중 문제가 발생했습니다.", Toast.LENGTH_LONG).show()

            val cheerSongButton = findViewById<Button>(R.id.cheerSongButton)

            cheerSongButton.setOnClickListener {
                Toast.makeText(this, "응원곡을 불러오지 못하였습니다", Toast.LENGTH_SHORT).show()
            }
        }

    }
}
