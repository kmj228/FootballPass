package com.example.FootBall.footBall_damyeong

/*
project structure에서 안드로이드 API를 35로 설정
파이어베이스 연동 안되있으면 tools에서 firebase에서 FireStore랑 FireStorage 연동 및 sdk다운받기
뷰바인딩 설정


-------------------------------------중요------------------------------------------
--파이어 스토어 경로체계
팀정보: teamData/팀문서
선수정보: teamData/팀이름/playerList/선수문서

공통게시판:publicBoards/게시판문서
공통게시판안에 있는 게시글:publicBoards/게시판이름/posts/게시글문서
공통게시판안에 있는 게시글들에 대한 참조문서(content를 제외한 문서)publicBoards/게시판이름/reference/참조문서

유저게시판:userBoards/게시판문서
유저게시판안에 있는 게시글:userBoards/게시판이름/posts/게시글문서
유저게시판안에 있는 게시글들에 대한 참조문서(content를 제외한 문서):userBoards/게시판이름/reference/참조문서
--스토리지 경로체계
선수사진:teamData/팀이름/선수이름.png
공통게시판 게시글에 올라간 사진들: publicBoards/게시판이름/
유저게시판 게시글에 올라간 사진들: userBoards/게시판이름/
 */
/*
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val binding= ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        //메인화면에 있는 '공통게시판모음' 버튼
        binding.mainGoPublicBoards.setOnClickListener{
            var myintent= Intent(applicationContext, PublicBoardsActivity::class.java)
            startActivity(myintent)
        }
        //메인화면에 있는 '유저게시판모음' 버튼
        binding.mainGoUserBoards.setOnClickListener{
            var myintent= Intent(applicationContext, UserBoardsActivity::class.java)
            startActivity(myintent)
        }

    }
}

 */