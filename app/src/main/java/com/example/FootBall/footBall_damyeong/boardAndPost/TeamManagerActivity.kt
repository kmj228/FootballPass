package com.example.FootBall.footBall_damyeong.boardAndPost

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.FootBall.R
import com.example.FootBall.databinding.ActivityTeamManagerBinding
import com.example.FootBall.footBall_damyeong.boardAndPost.Bus.BusManagerActivity
import com.example.FootBall.footBall_damyeong.boardAndPost.boardSelectAndCreate.BoardManagerActivity

class TeamManagerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding=ActivityTeamManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val goBoardManagerPageBtn=binding.teamManagerGoBoardManagerPage
        val goBusManagerPageBtn=binding.teamManagerGoBusManagerPage

        goBusManagerPageBtn.setOnClickListener{
            val myintent= Intent(applicationContext,BoardManagerActivity::class.java)
            startActivity(myintent)
        }
        goBusManagerPageBtn.setOnClickListener{
            val myintent= Intent(applicationContext,BusManagerActivity::class.java)
            startActivity(myintent)
        }
    }
}