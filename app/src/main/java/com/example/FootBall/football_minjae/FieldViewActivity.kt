package com.example.FootBall.football_minjae

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.FootBall.FireStorageConnection
import com.example.FootBall.R

class FieldViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_field_view)

        val teamName:String?=intent.getStringExtra("team")
        if(teamName==null){
            Log.e("FieldViewActivity","teamName is null")
            finish()
            return
        }

        val path="stadium/"+teamName
        val imageE:ImageView=findViewById(R.id.E)
        val imageW:ImageView=findViewById(R.id.W)
        val imageS:ImageView=findViewById(R.id.S)
        val imageN:ImageView=findViewById(R.id.N)

        FireStorageConnection.bindImageByPath(this,path+"/east.png",imageE)
        FireStorageConnection.bindImageByPath(this,path+"/west.png",imageW)
        FireStorageConnection.bindImageByPath(this,path+"/north.png",imageN)
        FireStorageConnection.bindImageByPath(this,path+"/south.png",imageS)
    }
}