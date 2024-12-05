package com.example.FootBall.football_junsik


data class GameInfo(
    val date: String,
    val homeTeam: Team,
    val awayTeam: Team,
    val homeScore: String, // 점수도 포함할 경우
    val awayScore: String,
    val gameId: Int,
    val meetSeq: Int
)
