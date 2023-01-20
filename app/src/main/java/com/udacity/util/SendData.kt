package com.udacity.util

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class SendData(val downloadUtils : DownloadUtils,val state : Int) : Parcelable {

}