package com.example.FootBall.football_junsik


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.lifecycle.ViewModelProvider
import com.example.FootBall.R
import org.json.JSONArray
import org.json.JSONException

class DetailMatchInfoFragment : Fragment() {
    private lateinit var matchDetailInfoShare: MatchDetailInfoShare
    private lateinit var statusArray: JSONArray

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_match_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModelProvider를 사용하여 matchDetailInfoShare 초기화
        matchDetailInfoShare = ViewModelProvider(requireActivity()).get(MatchDetailInfoShare::class.java)

        // LiveData 관찰
        matchDetailInfoShare.matchStatus.observe(viewLifecycleOwner) { data ->
            statusArray = data
            updateProgressBars(view)
        }
    }

    private fun updateProgressBars(view: View) {
        try {
            val progressName = listOf(R.id.possessionProgress, R.id.shootingProgress, R.id.validShotProgress)
            for (i in progressName.indices) {
                val progress = view.findViewById<ProgressBar>(progressName[i])
                val homeScore = statusArray.getJSONArray(i).getInt(0)
                val awayScore = statusArray.getJSONArray(i).getInt(1)
                progress.max = homeScore + awayScore // 총 점수를 MAX로 설정
                progress.progress = homeScore // 홈팀 점수로 ProgressBar 업데이트
            }
        } catch (e: JSONException) {
            Log.e("DetailMatchInfoFragment", "Error parsing status array: ${e.message}")
        } catch (e: IndexOutOfBoundsException) {
            Log.e("DetailMatchInfoFragment", "Index out of bounds: ${e.message}")
        }
    }
}
