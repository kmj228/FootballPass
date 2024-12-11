package com.example.FootBall.football_junsik

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.FootBall.R
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException

class MatchLineUpFragment : Fragment() {
    private lateinit var leftRecyclerView: RecyclerView
    private lateinit var rightRecyclerView: RecyclerView
    private lateinit var homeAdapter: GroundPlayers
    private lateinit var awayAdapter: GroundPlayers

    private var groundHomePlayersList = mutableListOf<CustomerMatch>()
    private var groundAwayPlayersList = mutableListOf<CustomerMatch>()

    private lateinit var matchDetailInfoShare: MatchDetailInfoShare
    private lateinit var awayLineUpArray: JSONArray
    private lateinit var homeLineUpArray: JSONArray

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_match_line_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        leftRecyclerView = view.findViewById(R.id.leftRecyclerView)
        leftRecyclerView.layoutManager = LinearLayoutManager(context)

        rightRecyclerView = view.findViewById(R.id.rightRecyclerView)
        rightRecyclerView.layoutManager = LinearLayoutManager(context)

        homeAdapter = GroundPlayers(groundHomePlayersList)
        leftRecyclerView.adapter = homeAdapter

        awayAdapter = GroundPlayers(groundAwayPlayersList)
        rightRecyclerView.adapter = awayAdapter

        // Observe home and away lineups
        matchDetailInfoShare = ViewModelProvider(requireActivity()).get(MatchDetailInfoShare::class.java)

        matchDetailInfoShare.homeLineUp.observe(viewLifecycleOwner) { homeData ->
            homeLineUpArray = homeData
            updatePlayers(homeLineUpArray, groundHomePlayersList, homeAdapter)
        }

        matchDetailInfoShare.awayLineUp.observe(viewLifecycleOwner) { awayData ->
            awayLineUpArray = awayData
            updatePlayers(awayLineUpArray, groundAwayPlayersList, awayAdapter)
        }
    }

    private fun updatePlayers(lineUpArray: JSONArray, playersList: MutableList<CustomerMatch>, adapter: GroundPlayers) {
        lifecycleScope.launch {
            playersList.clear()
            try {
                for (i in 0 until lineUpArray.length()) {
                    val playerName = lineUpArray.getJSONArray(i).getString(0)
                    val playerUrl = lineUpArray.getJSONArray(i).getString(1)
                    playersList.add(CustomerMatch(playerName, playerUrl))
                }
                Log.d("찐 라인업", playersList.toString())
                adapter.notifyDataSetChanged()
            } catch (e: JSONException) {
                Log.e("MatchLineUpFragment", "Error parsing lineup array: ${e.message}")
            }
        }
    }
}
