package com.example.FootBall.football_minjae

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.FootBall.R
import com.example.FootBall.Team

class TeamAdapter(private var teamList: List<Team>) :
    RecyclerView.Adapter<TeamAdapter.TeamViewHolder>() {

    inner class TeamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val teamName: TextView = itemView.findViewById(R.id.teamName)
        val teamRegion: TextView = itemView.findViewById(R.id.teamRegion)
        val teamLeague: TextView = itemView.findViewById(R.id.teamLeague)
        val teamProfile: ImageView = itemView.findViewById(R.id.teamProfile)

        init {
            itemView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, TeamDetailsActivity::class.java)
                intent.putExtra("team", teamList[adapterPosition])
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_team, parent, false)
        return TeamViewHolder(view)
    }

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        val team = teamList[position]
        holder.teamName.text = team.name
        holder.teamRegion.text = team.region
        holder.teamLeague.text = team.league
        holder.teamProfile.setImageResource(team.profileImage)
    }

    override fun getItemCount(): Int = teamList.size

    fun updateData(newTeamList: List<Team>) {
        teamList = newTeamList
        notifyDataSetChanged()
    }
}
