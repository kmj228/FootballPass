package com.example.FootBall.footBall_damyeong.boardAndPost.boardSelectAndCreate

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.FootBall.FireStorageConnection
import com.example.FootBall.FireStoreConnection
import com.example.FootBall.R
import com.example.FootBall.footBall_damyeong.boardAndPost.BoardActivity
import com.example.FootBall.databinding.FragmentUserBoardsBinding

class UserBoardsFragment : Fragment() {

    private var _binding: FragmentUserBoardsBinding? = null
    private val binding get() = _binding!!

    private val boardList = ArrayList<BoardListItem>()
    private lateinit var adapter: BoardListAdapter

    private fun refresh() {
        FireStoreConnection.onGetCollection("userBoards/") { documents ->
            boardList.clear()
            for (document in documents) {
                val board = document.toObject(BoardListItem::class.java)
                if (board?.boardName.isNullOrEmpty()) continue
                if (board != null) {
                    boardList.add(board)
                }
            }
            adapter.notifyDataSetChanged()
            binding.swipeRefreshLayout.isRefreshing = false // 새로고침 애니메이션 종료
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserBoardsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listView: ListView = binding.userBoardsListView
        val searchButton = binding.userBoardsSearchButtun
        val searchEditText = binding.userBoardsEditText
        val deleteButton = binding.userBoardsDeleteButtun
        val createButton = binding.userBoardsCreateButtun

        // 어댑터 생성 및 연결
        adapter = BoardListAdapter(requireContext(), R.layout.item_board_preview, boardList, "userBoards/")
        listView.adapter = adapter

        // 리스트 새로고침
        refresh()

        // 스와이프 리프레시 설정
        binding.swipeRefreshLayout.setOnRefreshListener {
            refresh()
        }

        // 리스트 아이템 클릭 시 게시글 화면으로 이동
        listView.setOnItemClickListener { _, _, position, _ ->
            val board = boardList[position]
            val intent = Intent(requireContext(), BoardActivity::class.java)
            intent.putExtra("boardPath", "userBoards/${board.boardName}")
            intent.putExtra("boardName", board.boardName)
            startActivity(intent)
        }

        // 게시판 검색
        searchButton.setOnClickListener {
            val searchTxt = searchEditText.text.toString()
            if (searchTxt.isEmpty()) {
                refresh()
                Toast.makeText(requireContext(), "검색어를 입력해주십시요", Toast.LENGTH_SHORT).show()
            } else {
                FireStoreConnection.onGetDocument("userBoards/$searchTxt") { success, document ->
                    if (success) {
                        boardList.clear()
                        val board = document!!.toObject(BoardListItem::class.java)
                        board?.let { boardList.add(it) }
                        adapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(requireContext(), "검색된 내용이 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // 게시판 삭제
        deleteButton.setOnClickListener {
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.delete_board_window, null)

            val dialog = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(true)
                .create()

            val boardNameEditText = dialogView.findViewById<EditText>(R.id.deleteBoardWindow_EditText)
            val deleteDialogButton = dialogView.findViewById<Button>(R.id.deleteBoardWindow_deleteButton)
            val cancelDialogButton = dialogView.findViewById<Button>(R.id.deleteBoardWindow_cancelButton)

            cancelDialogButton.setOnClickListener { dialog.dismiss() }

            deleteDialogButton.setOnClickListener {
                val boardName = boardNameEditText.text.toString()
                if (boardName.isNotEmpty()) {
                    FireStoreConnection.documentDelete("userBoards/$boardName") { success ->
                        if (success) {
                            FireStorageConnection.deleteDirectory("userBoards/$boardName")
                            Toast.makeText(requireContext(), "게시판 삭제완료.", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                            refresh()
                        } else {
                            Toast.makeText(requireContext(), "게시판 삭제실패.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "입력한 내용이 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            dialog.show()
        }

        // 게시판 생성
        createButton.setOnClickListener {
            val intent = Intent(requireContext(), BoardCreateActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
