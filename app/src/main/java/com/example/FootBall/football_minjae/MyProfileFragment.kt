package com.example.FootBall.football_minjae

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.FootBall.FireStorageConnection
import com.example.FootBall.MainTeamList
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

        // SwipeRefreshLayout 설정
        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            refreshUserData(view) // 새로고침 로직
            swipeRefreshLayout.isRefreshing = false // 새로고침 완료 후 애니메이션 종료
        }

        refreshUserData(view) // 초기 데이터 로드

        // 수정 버튼 클릭
        val editProfileButton = view.findViewById<Button>(R.id.btnEditProfile)
        editProfileButton.setOnClickListener {
            val intent = Intent(requireContext(), EditActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        // 로그아웃 버튼 클릭
        val logoutButton = view.findViewById<Button>(R.id.logout)
        logoutButton.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    // 사용자 데이터 및 최근 방문 경기 새로고침
    private fun refreshUserData(view: View) {
        val app = requireActivity().application as MyApplication
        val user = app.currentUser

        if (user != null) {
            // 프로필 이미지 및 사용자 데이터 설정
            FireStorageConnection.bindImageByPath(
                requireContext(),
                user.profile,
                view.findViewById(R.id.imageViewProfile)
            )
            view.findViewById<TextView>(R.id.textViewName).text = user.name
            view.findViewById<TextView>(R.id.textViewInfo).text = user.info
            view.findViewById<TextView>(R.id.textViewTeamName).text = user.team

            // 팀 로고 설정
            val teamList = MainTeamList()
            val team = teamList.findTeamByName(user.team)
            if (team != null) {
                view.findViewById<ImageView>(R.id.imageViewTeamLogo)
                    .setImageResource(team.profileImage)
            }
        } else {
            Toast.makeText(
                requireContext(),
                "사용자 데이터를 읽어오지 못하였습니다. 로그아웃 후 다시 로그인해주세요.",
                Toast.LENGTH_LONG
            ).show()
            resetDefaultUserData(view)
        }

        // 최근 방문 경기 추가
        loadRecentMatches(view)
    }

    // 최근 방문 경기 동적 로드
    private fun loadRecentMatches(view: View) {
        val recentMatchesLayout = view.findViewById<LinearLayout>(R.id.layoutRecentMatches)
        recentMatchesLayout.removeAllViews()

        // Firestore에서 경기 데이터 가져오기 (임시 데이터 예시)
        val recentMatches = listOf(
            Match("FC 서울 vs 수원 FC", "2024-11-01", R.drawable.team12),
            Match("전북 FC vs FC 서울", "2024-10-20", R.drawable.team06),
            Match("전북 FC vs 수원 FC", "2024-09-15", R.drawable.team09)
        )

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

    // 로그아웃 확인 다이얼로그
    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("로그아웃")
            .setMessage("정말 로그아웃 하시겠습니까?")
            .setPositiveButton("예") { _, _ ->
                performLogout()
            }
            .setNegativeButton("아니오", null)
            .show()
    }

    // 로그아웃 로직
    private fun performLogout() {
        val sharedPreferences =
            requireContext().getSharedPreferences("AutoLogin", AppCompatActivity.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()

        // 로그인 화면으로 이동
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    // 사용자 기본 데이터 초기화
    private fun resetDefaultUserData(view: View) {
        view.findViewById<TextView>(R.id.textViewName).text = "이름 없음"
        view.findViewById<TextView>(R.id.textViewInfo).text = "정보 없음"
        view.findViewById<TextView>(R.id.textViewTeamName).text = "팀 없음"
        view.findViewById<ImageView>(R.id.imageViewTeamLogo).setImageResource(R.drawable.ic_launcher_foreground)
    }

    // 경기 데이터 클래스
    data class Match(
        val name: String,
        val date: String,
        val iconResId: Int
    )
}
