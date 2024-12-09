package com.example.FootBall.football_minjae

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.FootBall.FireStorageConnection
import com.example.FootBall.MainTeamList
import com.example.FootBall.MyApplication
import com.example.FootBall.R
import com.example.FootBall.football_junsik.GameDBHelper

class MyProfileFragment : Fragment() {

    private lateinit var dbHelper: GameDBHelper
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Fragment 레이아웃을 inflate
        return inflater.inflate(R.layout.activity_my_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 스와이프 리프레시 설정
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            refreshData(view) // 새로고침 시 데이터 갱신
            swipeRefreshLayout.isRefreshing = false // 새로고침 애니메이션 종료
        }

        refreshData(view) // 초기 데이터 로드

        val editProfileButton = view.findViewById<Button>(R.id.btnEditProfile)
        val logoutButton = view.findViewById<Button>(R.id.logout)

        editProfileButton.setOnClickListener {
            val intent = Intent(requireContext(), EditActivity::class.java)
            startActivity(intent)

            // 현재 액티비티 종료
            requireActivity().finish()
        }

        logoutButton.setOnClickListener {
            val sharedPreferences =
                requireContext().getSharedPreferences("AutoLogin", AppCompatActivity.MODE_PRIVATE)
            sharedPreferences.edit().clear().apply()

            // 로그인 화면으로 이동
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

            // 현재 액티비티 종료
            requireActivity().finish()
        }
    }

    private fun refreshData(view: View) {
        val app = requireActivity().application as MyApplication
        val user = app.currentUser

        // 사용자 데이터 갱신
        if (user != null) {
            FireStorageConnection.bindImageByPath(
                requireContext(),
                user.profile,
                view.findViewById(R.id.imageViewProfile)
            )

            view.findViewById<TextView>(R.id.textViewName).text = user.name
            view.findViewById<TextView>(R.id.textViewInfo).text = user.info
            view.findViewById<TextView>(R.id.textViewTeamName).text = user.team

            val teamList = MainTeamList()
            val team = teamList.findTeamByName(user.team)

            if (team != null) {
                view.findViewById<ImageView>(R.id.imageViewTeamLogo).setImageResource(team.profileImage)
            }

        } else {
            Toast.makeText(
                requireContext(),
                "사용자 데이터를 읽어오지 못하였습니다. 로그아웃 후 다시 로그인해주세요",
                Toast.LENGTH_SHORT
            ).show()
            view.findViewById<TextView>(R.id.textViewName).text = "이름"
            view.findViewById<TextView>(R.id.textViewInfo).text = "자기소개"
            view.findViewById<TextView>(R.id.textViewTeamName).text = "팀"
        }

        // 최근 방문 경기 데이터 갱신
        val recentMatches = mutableListOf<Match>()
        dbHelper = GameDBHelper(view.context)
        val db = dbHelper.readableDatabase

        val cursor: Cursor = db.rawQuery("SELECT * FROM gameDataTBL ORDER BY date DESC;", null)
        while (cursor.moveToNext()) {
            recentMatches.add(
                Match(
                    cursor.getString(1) + " VS " + cursor.getString(2),
                    cursor.getString(0),
                    cursor.getInt(5),
                    cursor.getInt(3),
                    cursor.getInt(4)
                )
            )
        }
        cursor.close()

        // 최근 방문 경기 추가
        val recentMatchesLayout = view.findViewById<LinearLayout>(R.id.layoutRecentMatches)
        recentMatchesLayout.removeAllViews() // 이전 뷰 제거
        for (match in recentMatches) {
            val matchView = createMatchView(match, recentMatchesLayout)
            recentMatchesLayout.addView(matchView)
        }
    }

    // 경기 항목 뷰 생성
    private fun createMatchView(match: Match, parent: ViewGroup): View {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_match, parent, false)

        val matchImageView = itemView.findViewById<ImageView>(R.id.imageViewMatchIcon)
        val matchNameTextView = itemView.findViewById<TextView>(R.id.textViewMatchName)
        val matchDateTextView = itemView.findViewById<TextView>(R.id.textViewMatchDate)
        val deleteButton = itemView.findViewById<ImageView>(R.id.buttonDeleteMatch)

        matchImageView.setImageResource(match.iconResId)
        matchNameTextView.text = match.name
        matchDateTextView.text = match.date

        // 삭제 버튼 클릭 리스너
        deleteButton.setOnClickListener {
            // AlertDialog 생성
            val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            builder.setTitle("삭제 확인")
            builder.setMessage("정말로 이 경기를 삭제하시겠습니까?")

            // 확인 버튼 클릭 시
            builder.setPositiveButton("삭제") { _, _ ->
                dbHelper = GameDBHelper(requireContext())
                val db = dbHelper.writableDatabase

                val deleteQuery = "DELETE FROM gameDataTBL WHERE gameId = ? AND meetSeq = ? AND date = ?"
                db.execSQL(deleteQuery, arrayOf(match.gameId, match.meetSeq, match.date))

                Toast.makeText(requireContext(), "경기 데이터가 삭제되었습니다.", Toast.LENGTH_SHORT).show()

                // 삭제된 항목을 화면에서 제거
                parent.removeView(itemView)
            }

            // 취소 버튼 클릭 시
            builder.setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss() // 대화 상자 닫기
            }

            // 다이얼로그 표시
            builder.show()
        }


        return itemView
    }

    // 경기 데이터 클래스
    data class Match(
        val name: String,
        val date: String,
        val iconResId: Int,
        val gameId: Int,
        val meetSeq: Int
    )
}
