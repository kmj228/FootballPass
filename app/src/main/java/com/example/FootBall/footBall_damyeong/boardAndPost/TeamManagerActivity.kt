package com.example.FootBall.footBall_damyeong.boardAndPost

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.FootBall.R
import com.example.FootBall.databinding.ActivityTeamManagerBinding

class TeamManagerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding=ActivityTeamManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val boardTitle:EditText=binding.teamManagerBoardTitle
        val boardExplanation:EditText=binding.teamManagerBoardExplanation
        val boardEnterBtn:Button=binding.teamManagerBoardEnterBtn

        val busContentEdit:EditText=binding.teamManagerBusContentEdit
        val busTimeEdit:EditText=binding.teamManagerBusTimeEdit
        val busStartEdit:EditText=binding.teamManagerBusStartEdit
        val busEndEdit:EditText=binding.teamManagerBusEndEdit
        val busPriceEdit:EditText=binding.teamManagerBusPriceEdit
        val busInfoEnterBtn:Button=binding.teamManagerBusInfoEnterBtn

        val teamManager_busListView:ListView=binding.teamManagerBusListView


    }
}