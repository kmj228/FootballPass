package com.example.FootBall.football_junsik

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// 휴대폰에 DB저장하는 클래스
class GameDBHelper(context: Context) : SQLiteOpenHelper(context, "gameDataDB", null, 1){
    private val CREATE_GAME_TABLE = """
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
    """.trimIndent()

    private val CREATE_TEAM_TABLE = """
        CREATE TABLE teamDataTBL (
            homeTeamName VARCHAR(50),
            awayTeamName VARCHAR(50),
            date VARCHAR(30),
            homeScore VARCHAR(10),
            awayScore VARCHAR(10),
            homeImage INTEGER,
            awayImage INTEGER,
            gameId INTEGER,
            meetSeq INTEGER,
            place VARCAHR(50),
            PRIMARY KEY (homeTeamName, date)
        );
    """.trimIndent()

    override fun onCreate(p0: SQLiteDatabase?) {
        // 처음 만들었을 때 DB 생성하는 구역
        // 날짜, 홈팀 이름, 어웨이팀 이름, gameId, meetSeq, 홈팀 이미지, 어웨이팀 이미지
        // gameId는 승강PO를 할 때 1부터 세기때문에 시즌 초반과 중복될 수 있어서 meetSeq도 추가함
        // ++ date는 시즌마다 gameId를 1부터 세기 때문에 년도는 다른데 gameId와 meetSeq가 같을 수 있어서 기본키에 추가함

        // 테이블 이름은 gameDataTBL로 통일
        if (p0 != null) {
            if (!isTableExists(p0, "gameDataTBL")) {
                p0.execSQL(CREATE_GAME_TABLE)
            }
            if (!isTableExists(p0, "teamDataTBL")) {
                p0.execSQL(CREATE_TEAM_TABLE)
            }
        }
    }



    // 테이블 존재 여부 확인
    private fun isTableExists(db: SQLiteDatabase, tableName: String): Boolean {
        val cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?", arrayOf(tableName))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        // DB를 삭제하고 다시 생성
        p0?.execSQL("DROP TABLE IF EXISTS teamDataTBL")
        p0?.execSQL("DROP TABLE IF EXISTS gameDataTBL")
        onCreate(p0)
    }

    // 팀 정보 추가
    fun addTeam(homeTeamName: String, awayTeamName: String,date: String, homeScore: String, awayScore: String, homeImage: Int, awayImage: Int, gameId: Int, meetSeq: Int, place:String) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("homeTeamName", homeTeamName)
            put("awayTeamName", awayTeamName)
            put("date", date)
            put("homeScore", homeScore)
            put("awayScore", awayScore)
            put("homeImage", homeImage)
            put("awayImage", awayImage)
            put("gameId", gameId)
            put("meetSeq", meetSeq)
            put("place", place)
        }
        db.insert("teamDataTBL", null, values)
        db.close()
    }

    // teamDataTBL 삭제
    fun deleteTeamCalTable() {
        val db = this.writableDatabase
        db.execSQL("DROP TABLE IF EXISTS teamDataTBL") // 존재 한다면 삭제를 시킴
        db.close()
    }

}