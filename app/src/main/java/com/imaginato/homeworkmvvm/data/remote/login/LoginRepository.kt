package com.imaginato.homeworkmvvm.data.remote.login

import com.imaginato.homeworkmvvm.data.local.login.User
import com.imaginato.homeworkmvvm.data.remote.login.request.LoginRequest
import com.imaginato.homeworkmvvm.data.remote.login.response.LoginResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface LoginRepository {

    suspend fun getLoginData(url:String,loginRequest: LoginRequest):Flow<Result<User>>
}