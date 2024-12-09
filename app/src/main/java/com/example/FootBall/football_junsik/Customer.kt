package com.example.FootBall.football_junsik


data class Customer(
    val date: String,
    val place: String,
    val homeTeam: String,
    val awayTeam: String,
    val homeScore: String,
    val awayScore: String,
    val homeDraw: Int,
    val awayDraw: Int,
    val gameId: Int,
    val meetSeq: Int
)
