package com.example.FootBall.football_minjae

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.FootBall.R

class PlayerImageAdapter(
    context: Context,
    private val resource: Int,
    private val players: List<PlayerInfo>,
    private val teamName: String
) : ArrayAdapter<PlayerInfo>(context, resource, players) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.player_list_item, parent, false)
        val player = getItem(position)

        val imageView = view.findViewById<ImageView>(R.id.playerImage)
        val nameView = view.findViewById<TextView>(R.id.playerName)
        val positionView = view.findViewById<TextView>(R.id.playerPosition)
        val nationalityView = view.findViewById<TextView>(R.id.playerNationality)
        val jerseyNumberView = view.findViewById<TextView>(R.id.playerJerseyNumber)
        val heightWeightBirthDateView = view.findViewById<TextView>(R.id.playerHeightWeight)

        nameView.text = player?.name
        positionView.text = "포지션 : " + player?.position
        nationalityView.text = player?.nationality
        jerseyNumberView.text = player?.jerseyNumber + "번"

        heightWeightBirthDateView.text = player?.height + "cm " + player?.weight + "kg "
        Glide.with(context).load(player?.imageUrl).into(imageView)

        // 클릭 이벤트 추가
        view.setOnClickListener {
            val intent = Intent(context, PlayerDetailsActivity::class.java)
            intent.putExtra("player", player) // 기존에 PlayerInfo를 전달
            intent.putExtra("teamName", teamName) // 추가: 팀 이름 전달
            context.startActivity(intent)

            Log.d("Player", player.toString())
        }


        return view
    }

}

