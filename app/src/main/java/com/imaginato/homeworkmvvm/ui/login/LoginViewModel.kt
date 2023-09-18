package com.imaginato.homeworkmvvm.ui.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.imaginato.homeworkmvvm.R
import com.imaginato.homeworkmvvm.data.local.login.User
import com.imaginato.homeworkmvvm.data.local.login.UserDao
import com.imaginato.homeworkmvvm.data.remote.login.LoginRepository
import com.imaginato.homeworkmvvm.data.remote.login.request.LoginRequest
import com.imaginato.homeworkmvvm.data.remote.login.response.LoginResponse
import com.imaginato.homeworkmvvm.exts.LOGIN_URL
import com.imaginato.homeworkmvvm.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.inject


@KoinApiExtension
class LoginViewModel:BaseViewModel() {

    private val loginRepository:LoginRepository by inject()
    private val userDao:UserDao by inject()
    var uiMessage: MutableLiveData<Int> = MutableLiveData(-1)
    var apiProgress: MutableLiveData<Boolean> = MutableLiveData(false)
    var apiResponse: MutableLiveData<User> = MutableLiveData()
    var errorMessage: MutableLiveData<String> = MutableLiveData()

    fun isDataValid(username: String, password: String):Boolean {
        if (username.isEmpty()) {
            uiMessage.postValue(R.string.add_username)
            return false
        }
        if (password.isEmpty()) {
            uiMessage.postValue(R.string.add_password)
            return false
        }

        if (password.length<5){
            uiMessage.postValue(R.string.invalid_password)
            return false
        }
        return true
    }

    fun login(request:LoginRequest){

        viewModelScope.launch (Dispatchers.IO){
            loginRepository.getLoginData(LOGIN_URL,request)
                .onStart {
                   apiProgress.postValue(true)
                }
                .catch {
                    errorMessage.postValue(it.message)
                    apiProgress.postValue(false)
                }.onCompletion {
                    apiProgress.postValue(false)
                }.collect {
                    apiResponse.postValue(it.data)
                    it.data?.let { it1 -> userDao.insertUser(it1) }
                }
        }
    }

}