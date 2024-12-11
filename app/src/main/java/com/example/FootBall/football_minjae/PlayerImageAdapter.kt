package com.example.FootBall.football_minjae

import android.content.Context
import android.content.Intent
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
    private val players: List<PlayerInfo>
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
        positionView.text = player?.position
        nationalityView.text = player?.nationality
        jerseyNumberView.text = "No." + player?.jerseyNumber

        heightWeightBirthDateView.text = player?.height + "cm " + player?.weight + "kg "
        Glide.with(context).load(player?.imageUrl).into(imageView)

        // 클릭 이벤트 추가
        view.setOnClickListener {
            val intent = Intent(context, PlayerDetailsActivity::class.java)
            intent.putExtra("player", player)
            context.startActivity(intent)
        }

        return view
    }

}

