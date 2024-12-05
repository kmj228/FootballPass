package com.example.FootBall.football_minjae

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.FootBall.R
import com.example.FootBall.FireStorageConnection
import com.example.FootBall.FireStoreConnection
import com.example.FootBall.Team

class TeamDetailsActivity : AppCompatActivity() {
    @SuppressLint("QueryPermissionsNeeded", "InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_details)

        val team = intent.getParcelableExtra<Team>("team") // Parcelable 사용 시

        if (team != null) {
            findViewById<ImageView>(R.id.teamProfile).setImageResource(team.profileImage)
            findViewById<TextView>(R.id.teamName).text = team.name
            findViewById<TextView>(R.id.teamDescription).text = "연고지 : ${team.region}"
            findViewById<TextView>(R.id.teamHome).apply {
                text = "홈구장 : ${team.home}"

                setOnClickListener {
                    val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(team.address)}")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)

                    // Intent Chooser를 통해 사용자에게 앱 선택을 제공
                    val chooser = Intent.createChooser(mapIntent, "지도 앱 선택")
                    if (mapIntent.resolveActivity(packageManager) != null) {
                        startActivity(chooser)
                    } else {
                        Toast.makeText(context, "지도 앱을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
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
        }

        val playerListContainer = findViewById<LinearLayout>(R.id.playerListContainer)

        FireStoreConnection.onGetCollection("/teamData/${team?.name}/playerList") { documents ->
            for (d in documents) {

                val player = d.toObject(Player::class.java)

                val playerView = layoutInflater.inflate(R.layout.item_player, null)

                if (player != null) {
                    playerView.findViewById<TextView>(R.id.playerName).text = player.name
                    playerView.findViewById<TextView>(R.id.playerStats).text =
                        "키 : ${player.height} / 몸무게 : ${player.weight} / 포지션 : ${player.position}"

                    runOnUiThread {
                        if (!isFinishing && !isDestroyed) {
                            FireStorageConnection.bindImageByPath(
                                this,
                                player.imagePath,
                                playerView.findViewById(R.id.playerProfile)
                            )
                        }
                    }


                    playerView.setOnClickListener {
                        val intent = Intent(this, PlayerDetailsActivity::class.java)
                        intent.putExtra("player", player) // 팀 객체 전달
                        startActivity(intent)
                    }
                    playerListContainer.addView(playerView)
                }
            }
        }
    }
}
