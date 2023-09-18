package com.imaginato.homeworkmvvm.data.remote.login

import com.imaginato.homeworkmvvm.data.remote.login.request.LoginRequest
import com.imaginato.homeworkmvvm.data.remote.login.response.ApiResponse
import com.imaginato.homeworkmvvm.data.remote.login.response.LoginResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

interface LoginApi {

    @POST
    suspend fun doLogin(@Url url: String,
        @Body loginRequest: LoginRequest
    ): Response<ApiResponse<LoginResponse>>

}