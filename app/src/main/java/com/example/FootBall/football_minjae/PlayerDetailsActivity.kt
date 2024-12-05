package com.example.FootBall.football_minjae

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.FootBall.R
import com.example.FootBall.FireStorageConnection

class PlayerDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_details)

        val player = intent.getParcelableExtra<Player>("player") // Parcelable 사용

        if (player == null) {
            Log.e("PlayerDetailsActivity", "플레이어 데이터가 비어있음")
            finish()
            return
        }

        findViewById<TextView>(R.id.playerName).text = player.name
        findViewById<TextView>(R.id.playerTeam).text = "소속 팀 : ${player.team}"
        findViewById<TextView>(R.id.playerNum).text = "등번호 : ${player.number}"
        findViewById<TextView>(R.id.playerPos).text = "포지션 : ${player.position}"
        findViewById<TextView>(R.id.playerBirthdate).text = "생년월일 : ${player.birthday}"
        findViewById<TextView>(R.id.playerStats).text = "신장/체중 : ${player.height}/${player.weight}"
        FireStorageConnection.bindImageByPath(this, player.imagePath, findViewById(R.id.playerProfile))

        val cheerSongButton = findViewById<Button>(R.id.cheerSongButton)

        if (player.song.isNullOrEmpty()) {
            cheerSongButton.setOnClickListener {
                Toast.makeText(this, "응원곡이 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        else if (player.song.contains("http")) {
            cheerSongButton.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(player.song))
                startActivity(intent)
            }
        }
        else {
            findViewById<TextView>(R.id.playerSong).text = "응원곡 : ${player.song}"

            cheerSongButton.setOnClickListener {
                Toast.makeText(this, "응원곡이 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
