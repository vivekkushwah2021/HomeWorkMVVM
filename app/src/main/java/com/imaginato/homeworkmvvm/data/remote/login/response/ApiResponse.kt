package com.imaginato.homeworkmvvm.data.remote.login.response

data class ApiResponse<out T>(
    val errorCode: String? = null,
    val data: T? = null,
    val errorMessage: String? = null,
)