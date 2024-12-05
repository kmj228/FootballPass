package com.example.FootBall.football_junsik

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.FootBall.R

// 게시글을 클릭하면 다른 레이아웃으로 이동 가능함
// 리스트로 만든다고 쳤을때 data Class를 게시판 데이터에 맞게 세팅
// 그냥 페이지 정보를 받고 그 페이지에 맞게 만약 한 페이지에 10개를 보여줄거면 2페이지는 11부터 20까지
class SecondAdapter(val items: ArrayList<GameInfo>) : RecyclerView.Adapter<SecondAdapter.SecondViewHolder>(){

    var mListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    inner class SecondViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        // 화면에 보이는 부분을 편집함
        // findViewById로 레이아웃의 각 위젯 설정 가능
        // 게시글 추가 버튼과 관련된 내용
        //

        // init으로 게시글을 클릭했을 때 생기는 정보들

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SecondViewHolder {
        // 게시글이 올라왔을 때의 모습을 띄워줌 ex) 에타의 게시글 리스트 모습처럼
        // recyclerView_item은 게시글 목록에 뜨는 게시글 요약 정보(?)
        // 내려도 아래에 계속 생성할 수 있도록
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item, parent, false)
        return SecondViewHolder(view)
    }

    override fun onBindViewHolder(holder: SecondViewHolder, position: Int) {
        val item = items[position] // 데이터의 몇번째 데이터인지
        //holder.bind(item) // bind 메서드 호출해서 이동한 레이아웃의 정보 업데이트
    }

    override fun getItemCount(): Int {
        return items.size // 받은 아이템 리스트의 개수
    }
}