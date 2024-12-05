package com.example.FootBall.footBall_damyeong.boardAndPost.boardSelectAndCreate

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.example.FootBall.FireStoreConnection
import com.example.FootBall.R
import com.example.FootBall.footBall_damyeong.boardAndPost.BoardActivity
import com.example.FootBall.databinding.FragmentPublicBoardsBinding

class PublicBoardsFragment : Fragment() {
    private val boardList = ArrayList<BoardListItem>()
    private lateinit var adapter: BoardListAdapter
    private var _binding: FragmentPublicBoardsBinding? = null
    private val binding get() = _binding!!

    private fun refresh() {
        FireStoreConnection.onGetCollection("publicBoards/") { documents ->
            boardList.clear() // 기존 데이터 초기화
            for (document in documents) {
                val board = document.toObject(BoardListItem::class.java)
                if (board != null) {
                    boardList.add(board)
                }
            }
            adapter.notifyDataSetChanged()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPublicBoardsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listView: ListView = binding.publicBoardsListView

        // 어댑터 만들기
        adapter = BoardListAdapter(requireContext(), R.layout.item_board_preview, boardList, "publicBoards/")
        listView.adapter = adapter

        // 리스트 아이템 클릭 시 게시글 화면으로 이동
        listView.setOnItemClickListener { _, _, position, _ ->
            val board = boardList[position]
            val intent = Intent(requireContext(), BoardActivity::class.java)
            intent.putExtra("boardPath", "publicBoards/" + board.boardName)
            intent.putExtra("boardName", board.boardName)
            startActivity(intent)
        }

        refresh()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
