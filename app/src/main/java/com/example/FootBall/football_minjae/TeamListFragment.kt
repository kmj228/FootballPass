package com.example.FootBall.football_minjae

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
    private var currentFilter: String = "K리그 1" // 초기 필터를 K리그1로 설정

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.activity_team_list, container, false)

        teamContainer = rootView.findViewById(R.id.teamListContainer)

        // 초기 팀 목록 표시 (K리그1 필터 적용)
        filterAndDisplayTeams("", inflater)

        // 검색 입력 처리
        val searchBar: EditText = rootView.findViewById(R.id.searchBar)
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterAndDisplayTeams(s.toString(), inflater)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // 필터 버튼 처리
        val btnKLeague1: Button = rootView.findViewById(R.id.btnKLeague1)
        val btnKLeague2: Button = rootView.findViewById(R.id.btnKLeague2)

        btnKLeague1.setOnClickListener {
            currentFilter = "K리그 1"
            filterAndDisplayTeams(searchBar.text.toString(), inflater)
        }

        btnKLeague2.setOnClickListener {
            currentFilter = "K리그 2"
            filterAndDisplayTeams(searchBar.text.toString(), inflater)
        }

        return rootView
    }

    // 팀 목록을 검색어 및 리그 필터에 맞춰 표시
    private fun filterAndDisplayTeams(searchQuery: String, inflater: LayoutInflater) {
        val filteredList = teamList.filter { team ->
            val matchesSearch = team.name.contains(searchQuery, ignoreCase = true)
            val matchesFilter = when (currentFilter) {
                "K리그 1" -> team.league == "K리그 1"
                "K리그 2" -> team.league == "K리그 2"
                else -> team.league == "K리그 1"
            }
            matchesSearch && matchesFilter
        }
        displayTeams(filteredList, inflater)
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
