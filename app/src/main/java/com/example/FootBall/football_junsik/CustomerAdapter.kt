package com.example.FootBall.football_junsik


import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.FootBall.R

class CustomerAdapter(val items: ArrayList<Customer>) : RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder>() {

    private lateinit var dbHelper: myDBHelper


    var mListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }


    inner class CustomerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // TODO: 홈팀과 어웨이팀 이름 받아오기
        val dateTextView: TextView = itemView.findViewById(R.id.playDay)
        val placeTextView: TextView = itemView.findViewById(R.id.playPlace)
        val homeTeamImageView: ImageView = itemView.findViewById(R.id.homeTeamImage)
        val awayTeamImageView: ImageView = itemView.findViewById(R.id.awayTeamImage)
        val homeScoreTextView: TextView = itemView.findViewById(R.id.homeScore)
        val awayScoreTextView: TextView = itemView.findViewById(R.id.awayScore)
        val addPlane: ImageView = itemView.findViewById<ImageView>(R.id.addPlane)

        init {
            // 경기 일정을 클릭했을 때 생기는 정보들
            itemView.setOnClickListener {
                val pos = adapterPosition // 몇 번째 아이템인지 알아내기
                if (pos != RecyclerView.NO_POSITION) { // 삭제되지 않았다면
                    val context = itemView.context
                    // 이걸 NewActivit에 넘겨줌 TODO: 팀 이미지를 크롤링할지 고민이 필요
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
            addPlane.setOnClickListener{
                val pos = adapterPosition
                // TODO: 0번째부터 시작임
                if(pos != RecyclerView.NO_POSITION){
                    try {
                        dbHelper = myDBHelper(itemView.context)
                        val db = dbHelper.writableDatabase
                        val values = ContentValues().apply {
                            put("date", items[pos].date)
                            put("homeTeamName", items[pos].homeTeam)
                            put("awayTeamName", items[pos].awayTeam)
                            put("gameId", items[pos].gameId)
                            put("meetSeq", items[pos].meetSeq)
                            put("homeTeamImage", items[pos].homeDraw)
                            put("awayTeamImage", items[pos].awayDraw)
                        }


                        db.insert("gameDataTBL", null, values)
                        Toast.makeText(itemView.context, "성공적으로 저장됨", Toast.LENGTH_SHORT).show()
                    } catch (e: SQLiteConstraintException){
                        // 중복시 오류 처리
                        Toast.makeText(itemView.context, "중복됨", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        fun bind(item: Customer) {
            dateTextView.text = item.date
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

    inner class myDBHelper(context: Context) : SQLiteOpenHelper(context, "gameDataDB", null, 1){
        override fun onCreate(p0: SQLiteDatabase?) {
            // 처음 만들었을 때 DB 생성하는 구역
            // 날짜, 홈팀 이름, 어웨이팀 이름, gameId, meetSeq, 홈팀 이미지, 어웨이팀 이미지
            // gameId는 승강PO를 할 때 1부터 세기때문에 시즌 초반과 중복될 수 있어서 meetSeq도 추가함
            // ++ date는 시즌마다 gameId를 1부터 세기 때문에 년도는 다른데 gameId와 meetSeq가 같을 수 있어서 기본키에 추가함
            p0!!.execSQL("""
            CREATE TABLE gameDataTBL (
                date VARCHAR(30),
                homeTeamName VARCHAR(50),
                awayTeamName VARCHAR(50),
                gameId INTEGER,
                meetSeq INTEGER,
                homeTeamImage INTEGER,
                awayTeamImage INTEGER,
                PRIMARY KEY (gameId, meetSeq, date)
            );
        """.trimIndent())   }

        override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
            // DB를 삭제하고 다시 생성
            val sql : String = "DROP TABLE if exists mytable"

            p0!!.execSQL(sql)
            onCreate(p0)
        }
    }
}
