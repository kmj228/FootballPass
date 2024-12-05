package com.example.FootBall

import android.os.Parcel
import android.os.Parcelable

data class MyUser(
    var email: String = "",
    var name: String = "",
    var info: String = "",
    var profile: String = "",
    var team: String = "",
    var admin: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(email)
        parcel.writeString(name)
        parcel.writeString(info)
        parcel.writeString(profile)
        parcel.writeString(team)
        parcel.writeByte(if (admin) 1 else 0)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<MyUser> {
        override fun createFromParcel(parcel: Parcel): MyUser {
            return MyUser(parcel)
        }

        override fun newArray(size: Int): Array<MyUser?> {
            return arrayOfNulls(size)
        }
    }
}
