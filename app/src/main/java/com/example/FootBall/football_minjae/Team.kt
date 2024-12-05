package com.example.FootBall.football_minjae

import android.os.Parcel
import android.os.Parcelable

data class Team(
    val id: Int, // 팀 ID
    val name: String, // 팀 이름
    val englishName: String, // 영문명
    val region: String, // 연고지
    val home: String, // 홈구장
    val address: String,  // 홈구장 주소
    val supporters: String, // 서포터즈 이름
    val cheerSong: String, // 응원가 링크
    val league: String, // 팀 리그
    val profileImage: Int // 프로필 이미지 리소스 ID
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(englishName)
        parcel.writeString(region)
        parcel.writeString(home)
        parcel.writeString(address)
        parcel.writeString(supporters)
        parcel.writeString(cheerSong)
        parcel.writeString(league)
        parcel.writeInt(profileImage)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Team> {
        override fun createFromParcel(parcel: Parcel): Team {
            return Team(parcel)
        }

        override fun newArray(size: Int): Array<Team?> {
            return arrayOfNulls(size)
        }
    }
}

