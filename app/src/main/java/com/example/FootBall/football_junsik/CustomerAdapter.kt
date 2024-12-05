package com.example.FootBall.football_junsik


import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CustomerAdapter(val items: ArrayList<Customer>) : RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder>() {

    var mListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    inner class CustomerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.playDay)
        val timeTextView: TextView = itemView.findViewById(R.id.playTime)
        val placeTextView: TextView = itemView.findViewById(R.id.playPlace)
        val homeTeamImageView: ImageView = itemView.findViewById(R.id.homeTeamImage)
        val awayTeamImageView: ImageView = itemView.findViewById(R.id.awayTeamImage)
        val homeScoreTextView: TextView = itemView.findViewById(R.id.homeScore)
        val awayScoreTextView: TextView = itemView.findViewById(R.id.awayScore)

        init {
            // 경기 일정을 클릭했을 때 생기는 정보들
            itemView.setOnClickListener {
                val pos = adapterPosition // 몇 번째 아이템인지 알아내기
                if (pos != RecyclerView.NO_POSITION) { // 삭제되지 않았다면
                    val context = itemView.context
                    val intent = Intent(context, NewActivity::class.java).apply {
                        putExtra("DATE", items[pos].date)
                        putExtra("TIME", items[pos].time)
                        putExtra("PLACE", items[pos].place)
                        putExtra("HOME_SCORE", items[pos].homeScore)
                        putExtra("AWAY_SCORE", items[pos].awayScore)
                        putExtra("HOME_TEAM", items[pos].homeTeam)
                        putExtra("AWAY_TEAM", items[pos].awayTeam)
                        putExtra("HOME_IMAGE", items[pos].homeDraw)
                        putExtra("AWAY_IMAGE", items[pos].awayDraw)
                        putExtra("GAMEID", items[pos].gameId)
                        putExtra("MEETSEQ", items[pos].meetSeq)
                    }

                    context.startActivity(intent) // Intent로 NewActivity 시작
                }
            }
        }

        fun bind(item: Customer) {
            dateTextView.text = item.date
            timeTextView.text = item.time
            placeTextView.text = item.place
            homeTeamImageView.setImageResource(item.homeDraw) // 실제 이미지로 변경 필요
            awayTeamImageView.setImageResource(item.awayDraw) // 실제 이미지로 변경 필요
            homeScoreTextView.text = item.homeScore.toString()
            awayScoreTextView.text = item.awayScore.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item, parent, false)
        return CustomerViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item) // bind 메서드 호출
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
