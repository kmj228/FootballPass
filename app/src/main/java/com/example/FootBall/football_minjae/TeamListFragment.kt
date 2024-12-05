package com.example.FootBall.football_minjae

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.FootBall.MainTeamList
import com.example.FootBall.R
import com.example.FootBall.Team

class TeamListFragment : Fragment() {

    private val teamList = MainTeamList().getMainTeamList()
    private lateinit var teamContainer: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.activity_team_list, container, false)

        teamContainer = rootView.findViewById(R.id.teamListContainer)

        // 초기 팀 목록 표시
        displayTeams(teamList, inflater)

        // 검색 입력 처리
        val searchBar: EditText = rootView.findViewById(R.id.searchBar)
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val filteredList = teamList.filter { team ->
                    team.name.contains(s.toString(), ignoreCase = true)
                }
                displayTeams(filteredList, inflater) // 검색 결과 표시
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        return rootView
    }

    // 팀 목록을 LinearLayout에 표시
    private fun displayTeams(teams: List<Team>, inflater: LayoutInflater) {
        // 기존 항목 제거
        teamContainer.removeAllViews()

        // 각 팀을 LinearLayout에 추가
        for (team in teams) {
            val teamView = inflater.inflate(R.layout.item_team, teamContainer, false)
            val teamName: TextView = teamView.findViewById(R.id.teamName)
            val teamRegion: TextView = teamView.findViewById(R.id.teamRegion)
            val teamLeague: TextView = teamView.findViewById(R.id.teamLeague)

            teamView.findViewById<ImageView>(R.id.teamProfile).setImageResource(team.profileImage)

            teamName.text = team.name
            teamRegion.text = team.region
            teamLeague.text = team.league

            // 팀 항목 클릭 리스너 추가
            teamView.setOnClickListener {

                // 다른 Activity로 이동하려면 Intent 추가
                val intent = Intent(requireContext(), TeamDetailsActivity::class.java)
                intent.putExtra("team", team) // 팀 객체 전달
                startActivity(intent)
            }

            // 동적으로 추가
            teamContainer.addView(teamView)
        }
    }
}