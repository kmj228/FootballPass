package com.example.FootBall.footBall_damyeong

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import android.content.Intent
import android.net.Uri
import com.example.FootBall.R

class SlideFragment(private val newsData: List<String>) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.slide_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageView: ImageView = view.findViewById(R.id.newsImageView)
        val newsTitle: TextView = view.findViewById(R.id.newsTitle)
        val newsInfo: TextView = view.findViewById(R.id.newsInfo)
        val newsPublisher: TextView = view.findViewById(R.id.newsPublisher)
        val newsURL: LinearLayout = view.findViewById(R.id.newsURL)

        // Glide를 사용하여 URL에서 이미지 로드
        Glide.with(view.context)
            .load(newsData[1]) // 뉴스 이미지 URL
            .into(imageView)

        newsTitle.text = newsData[2] // 뉴스 제목
        newsInfo.text = newsData[3] // 뉴스 정보
        newsPublisher.text = newsData[4] // 뉴스 출처


        // 뉴스로 넘어가기
        newsURL.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(newsData[0]))
            startActivity(intent)
        }
    }
}
