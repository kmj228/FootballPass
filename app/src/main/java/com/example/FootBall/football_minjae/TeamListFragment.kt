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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.FootBall.MainTeamList
import com.example.FootBall.R

class TeamListFragment : Fragment() {

    private val teamList = MainTeamList().getMainTeamList()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TeamAdapter
    private var currentFilter: String = "K리그 1" // 초기 필터 설정

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.activity_team_list, container, false)

        recyclerView = rootView.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // 초기 어댑터 설정
        adapter = TeamAdapter(teamList.filter { it.league == currentFilter })
        recyclerView.adapter = adapter

        // 검색 입력 처리
        val searchBar: EditText = rootView.findViewById(R.id.searchBar)
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterTeams(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // 필터 버튼 처리
        val btnKLeague1: Button = rootView.findViewById(R.id.btnKLeague1)
        val btnKLeague2: Button = rootView.findViewById(R.id.btnKLeague2)

        btnKLeague1.setOnClickListener {
            currentFilter = "K리그 1"
            filterTeams(searchBar.text.toString())
        }

        btnKLeague2.setOnClickListener {
            currentFilter = "K리그 2"
            filterTeams(searchBar.text.toString())
        }

        return rootView
    }

    private fun filterTeams(query: String) {
        val filteredList = teamList.filter { team ->
            team.name.contains(query, ignoreCase = true) && team.league == currentFilter
        }
        adapter.updateData(filteredList)
    }
}
