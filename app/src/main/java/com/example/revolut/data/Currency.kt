package com.example.revolut.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Currency(val name: String, var amount: Double = 1.0) : Parcelable