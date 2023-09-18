package com.imaginato.homeworkmvvm.data.remote.login.request

import com.google.gson.annotations.SerializedName

data class LoginRequest (
    @SerializedName("username")
    var userName: String? = null,
    @SerializedName("password")
    var password: String? = null
)
