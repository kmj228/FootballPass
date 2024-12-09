package com.example.FootBall.football_junsik


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.FootBall.R

data class CustomerMatch(
    val name: String,
    val imageResId: String
)

// 선수들의 정보들을 라인업에 틀을 만들어서 올려주는 역할
class GroundPlayers(private val items: MutableList<CustomerMatch>) : RecyclerView.Adapter<GroundPlayers.PlayerViewHolder>() {
    var mListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    inner class PlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val playerName: TextView = itemView.findViewById(R.id.playerName)
        val playerImage: ImageView = itemView.findViewById(R.id.playerProfile)

        init {
            itemView.setOnClickListener {
                mListener?.onItemClick(itemView, adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.player_item, parent, false)
        return PlayerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val currentPlayer = items[position]
        holder.playerName.text = currentPlayer.name

        // Glide를 사용하여 이미지를 비동기로 로드

        Glide.with(holder.itemView.context)
            .load(currentPlayer.imageResId)
            .into(holder.playerImage)


    }

    override fun getItemCount(): Int {
        return items.size
    }
}
