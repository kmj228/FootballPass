package com.example.FootBall.footBall_damyeong.boardAndPost

//매개변수 기본값으로 초기화를 안해주면 정체모를 오류나니 주의
data class Post(
    var title: String="",
    var author: String="",
    var content:String="",
    var imagePath:String?=null,
    var email:String="",
    var timestamp: Long=0
)
//현재 코드에선 안쓰이지만, 리스트에 ArrayAdapter를 넣어 사용한다면 항목표시를 위해 호출됨.
