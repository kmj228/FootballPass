package com.example.FootBall.football_minjae

import android.content.Intent
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
import com.example.FootBall.FireStorageConnection
import com.example.FootBall.MyApplication
import com.example.FootBall.R

class MyProfileFragment : Fragment() {

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

        val app = requireActivity().application as MyApplication
        val user = app.currentUser
        if (user != null) {
            FireStorageConnection.bindImageByPath(
                requireContext(), // 수정: Fragment의 Context를 전달
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
                "사용자 데이터를 읽어오지 못하였습니다\n로그아웃 후 다시 로그인해주세요",
                Toast.LENGTH_SHORT
            ).show()
            view.findViewById<TextView>(R.id.textViewName).text = "이름"
            view.findViewById<TextView>(R.id.textViewInfo).text = "자기소개"
            view.findViewById<TextView>(R.id.textViewTeamName).text = "팀"
        }

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

        // 최근 방문 경기 리스트 (임의 데이터)
        val recentMatches = listOf(
            Match("FC 서울 vs 수원 FC", "2024-11-01", R.drawable.team12),
            Match("전북 FC vs FC 서울", "2024-10-20", R.drawable.team06),
            Match("전북 FC vs 수원 FC", "2024-09-15", R.drawable.team09)
        )

        // 최근 방문 경기 추가
        val recentMatchesLayout = view.findViewById<LinearLayout>(R.id.layoutRecentMatches)
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

        matchImageView.setImageResource(match.iconResId)
        matchNameTextView.text = match.name
        matchDateTextView.text = match.date

        return itemView
    }

    // 경기 데이터 클래스
    data class Match(
        val name: String,
        val date: String,
        val iconResId: Int
    )
}
