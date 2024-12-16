package com.example.FootBall.football_minjae

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.FootBall.R
import com.example.FootBall.databinding.FragmentTeamRankAdapterBinding
import com.example.FootBall.football_minjae.TeamRankAdapterFragment.OnItemClickListener

class TeamRankAdapterFragment(private val rankData: MutableList<MutableList<String>>) : RecyclerView.Adapter<TeamRankAdapterFragment.RankViewHolder>() {
    var mListener: com.example.FootBall.football_minjae.TeamRankAdapterFragment.OnItemClickListener? = null

    // View Binding 변수 선언
    private var _binding: FragmentTeamRankAdapterBinding? = null
    private val binding get() = _binding!!

    fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // View Binding 초기화
        _binding = FragmentTeamRankAdapterBinding.inflate(inflater, container, false)
        return binding.root // 루트 뷰 반환
    }


    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    inner class RankViewHolder(val binding: FragmentTeamRankAdapterBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                mListener?.onItemClick(itemView, adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankViewHolder {
        val binding = FragmentTeamRankAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RankViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RankViewHolder, position: Int) {
        val currentPlayer = rankData[position]
        Glide.with(holder.itemView.context)
            .load(currentPlayer[12]) // 현재 플레이어의 이미지 URL
            .placeholder(R.drawable.team23)
            .error(R.drawable.team09)
            .into(holder.binding.teamProfile) // 바인딩 객체 사용
        holder.binding.rank.text = currentPlayer[0]
        holder.binding.teamName.text = currentPlayer[1]
        holder.binding.teamMatch.text = currentPlayer[2]
        holder.binding.teamPoint.text = currentPlayer[3]
        holder.binding.teamWinCount.text = currentPlayer[4]
        holder.binding.teamDrewCount.text = currentPlayer[5]
        holder.binding.teamLoseCount.text = currentPlayer[6]
        holder.binding.teamGoal.text = currentPlayer[7]
        holder.binding.teamConceded.text = currentPlayer[8]
        holder.binding.teamGLDiff.text = currentPlayer[9]
        holder.binding.teamHelp.text = currentPlayer[10]
    }


    override fun getItemCount(): Int {
        return rankData.size
    }

    fun onDestroyView() {
        // 메모리 누수를 방지하기 위해 바인딩 객체 해제
        _binding = null
    }
}
