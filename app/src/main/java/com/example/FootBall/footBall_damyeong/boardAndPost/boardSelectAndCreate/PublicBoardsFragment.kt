package com.example.FootBall.footBall_damyeong.boardAndPost.boardSelectAndCreate

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.FootBall.FireStoreConnection
import com.example.FootBall.MyApplication
import com.example.FootBall.R
import com.example.FootBall.footBall_damyeong.boardAndPost.BoardActivity
import com.example.FootBall.ToMinjaeActivity
import com.example.FootBall.databinding.FragmentPublicBoardsBinding

class PublicBoardsFragment : Fragment() {
    private val boardList = ArrayList<BoardListItem>()
    private lateinit var adapter: BoardListAdapter
    private var _binding: FragmentPublicBoardsBinding? = null
    private val binding get() = _binding!!

    private fun refresh() {
        val app = requireActivity().application as MyApplication
        val user = app.currentUser

        FireStoreConnection.onGetCollection("publicBoards/") { documents ->
            if (_binding == null) return@onGetCollection // 뷰가 파괴되었으면 작업 중단
            boardList.clear()
            for (document in documents) {
                val board = document.toObject(BoardListItem::class.java)
                if (board != null) {
                    if (user!!.team == "") {
                        boardList.add(board)
                    } else if (board.boardName == user.team) {
                        boardList.add(board)
                    }
                    if (board.boardName == "모두의 풋볼") {
                        boardList.add(0, board)
                    }
                }
            }
            adapter.notifyDataSetChanged()
        }
    }


    override fun onStart() {
        super.onStart()
        refresh()
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}