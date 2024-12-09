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
        adapter = BoardListAdapter(requireContext(), R.layout.item_board_preview, boardList, "userBoards/")
        listView.adapter = adapter

        // 새로고침 설정
        binding.swipeRefreshLayout.setOnRefreshListener { refreshBoards() }

        // 초기 데이터 로드
        refreshBoards()

        // 게시판 클릭 이벤트
        listView.setOnItemClickListener { _, _, position, _ ->
            navigateToBoardDetail(boardList[position])
        }

        // 검색 버튼
        binding.userBoardsSearchButtun.setOnClickListener { performSearch() }

        // 삭제 버튼
        binding.userBoardsDeleteButtun.setOnClickListener { showDeleteDialog() }

        // 생성 버튼
        binding.userBoardsCreateButtun.setOnClickListener { navigateToBoardCreate() }
    }

    private fun refreshBoards() {
        binding.swipeRefreshLayout.isRefreshing = true
        FireStoreConnection.onGetCollection("userBoards/") { documents ->
            if (_binding == null) return@onGetCollection // 뷰가 파괴되었으면 아무 작업도 하지 않음
            boardList.clear()
            for (document in documents) {
                val board = document.toObject(BoardListItem::class.java)
                board?.let { boardList.add(it) }
            }
            adapter.notifyDataSetChanged()
            binding.swipeRefreshLayout.isRefreshing = false

            if (boardList.isEmpty()) {
                Toast.makeText(requireContext(), "게시판이 없습니다", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun performSearch() {
        val searchTxt = binding.userBoardsEditText.text.toString()
        if (searchTxt.isEmpty()) {
            refreshBoards()
        } else {
            FireStoreConnection.onGetDocument("userBoards/$searchTxt") { success, document ->
                if (success && document != null) {
                    boardList.clear()
                    document.toObject(BoardListItem::class.java)?.let { boardList.add(it) }
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(requireContext(), "검색된 게시판이 없습니다", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showDeleteDialog() {
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
                        dialog.dismiss()
                        Toast.makeText(requireContext(), "게시판이 삭제되었습니다", Toast.LENGTH_SHORT).show()
                        refreshBoards()
                    } else {
                        Toast.makeText(requireContext(), "게시판 삭제에 실패했습니다", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "입력된 내용이 없습니다", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun navigateToBoardDetail(board: BoardListItem) {
        val intent = Intent(requireContext(), BoardActivity::class.java)
        intent.putExtra("boardPath", "userBoards/${board.boardName}")
        intent.putExtra("boardName", board.boardName)
        startActivity(intent)
    }

    private fun navigateToBoardCreate() {
        val intent = Intent(requireContext(), BoardCreateActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
