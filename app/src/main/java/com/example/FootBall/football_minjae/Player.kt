package com.example.FootBall.football_minjae

import android.os.Parcel
import android.os.Parcelable

data class Player(
    val birthday: String = "",
    val height: String = "",
    val imagePath: String = "",
    val name: String = "",
    val number: Int = 0,
    val position: String = "",
    val song: String? = "",
    val team: String = "",
    val teamID: Int = 0,
    val weight: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(birthday)
        parcel.writeString(height)
        parcel.writeString(imagePath)
        parcel.writeString(name)
        parcel.writeInt(number)
        parcel.writeString(position)
        parcel.writeString(song)
        parcel.writeString(team)
        parcel.writeInt(teamID)
        parcel.writeString(weight)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Player> {
        override fun createFromParcel(parcel: Parcel): Player {
            return Player(parcel)
        }

        override fun newArray(size: Int): Array<Player?> {
            return arrayOfNulls(size)
        }
    }
}
