package com.imaginato.homeworkmvvm.data.remote.login

import com.imaginato.homeworkmvvm.data.local.login.User
import com.imaginato.homeworkmvvm.data.remote.login.request.LoginRequest
import com.imaginato.homeworkmvvm.data.remote.login.response.LoginResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.await


class UserDataRepository constructor( private var api: LoginApi):LoginRepository {

    override suspend fun getLoginData(url:String,loginRequest:LoginRequest) = flow {
        val loginResponse = api.doLogin(url, loginRequest)
        val data = loginResponse.body()?.data
        if (loginResponse.isSuccessful && data != null) {
            if (loginResponse.body()?.errorCode == "00") {
                val user = data.userId?.let {
                    User(
                        data.userId,
                        loginResponse.headers()["X-Acc"],
                        data.userName,
                        data.isDeleted
                    )
                }
                emit(Result.Success(user))
            } else {
                emit(Result.Error(loginResponse.body()?.errorMessage))
            }
        } else {
            emit(Result.Error("Some thing went wrong"))
        }

    }.flowOn(Dispatchers.IO)
}