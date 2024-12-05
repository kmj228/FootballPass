package com.example.FootBall.football_junsik

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// 휴대폰에 DB저장하는 클래스
class GameDBHelper(context: Context) : SQLiteOpenHelper(context, "gameDataDB", null, 1){
    override fun onCreate(p0: SQLiteDatabase?) {
        // 처음 만들었을 때 DB 생성하는 구역
        // 날짜, 홈팀 이름, 어웨이팀 이름, gameId, meetSeq, 홈팀 이미지, 어웨이팀 이미지
        // gameId는 승강PO를 할 때 1부터 세기때문에 시즌 초반과 중복될 수 있어서 meetSeq도 추가함
        // ++ date는 시즌마다 gameId를 1부터 세기 때문에 년도는 다른데 gameId와 meetSeq가 같을 수 있어서 기본키에 추가함
        
        // 테이블 이름은 gameDataTBL로 통일
        p0!!.execSQL("""
            CREATE TABLE gameDataTBL (
                date VARCHAR(30),
                homeTeamName VARCHAR(50),
                awayTeamName VARCHAR(50),
                gameId INTEGER,
                meetSeq INTEGER,
                homeTeamImage INTEGER,
                awayTeamImage INTEGER,
                PRIMARY KEY (gameId, meetSeq, date)
            );
        """.trimIndent())   }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        // DB를 삭제하고 다시 생성
        val sql : String = "DROP TABLE if exists mytable"

        p0!!.execSQL(sql)
        onCreate(p0)
    }


}