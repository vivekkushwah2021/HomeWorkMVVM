package com.imaginato.homeworkmvvm.ui.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.imaginato.homeworkmvvm.data.remote.login.request.LoginRequest
import com.imaginato.homeworkmvvm.databinding.ActivityLoginBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinApiExtension

@KoinApiExtension
class LoginActivity : AppCompatActivity() {

    private lateinit var binding:ActivityLoginBinding
    private val viewModel by viewModel<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initListener()
        initObserver()

    }

    private fun initListener(){
        binding.btnLogin.setOnClickListener {
            if (viewModel.isDataValid(binding.etUserName.text.toString(),binding.etPassword.text.toString())){
                viewModel.login(LoginRequest(binding.etUserName.text.toString(),binding.etPassword.text.toString()))
            }
        }
    }

    private fun initObserver(){
            viewModel.uiMessage.observe(this){
                if (it != -1){
                    Toast.makeText(this,it,Toast.LENGTH_SHORT).show()
                }
            }

        viewModel.apiProgress.observe(this){
            if (it){
                binding.progressCircular.visibility = View.VISIBLE
                binding.btnLogin.visibility = View.INVISIBLE
            } else{
                binding.progressCircular.visibility = View.GONE
                binding.btnLogin.visibility = View.VISIBLE
            }

        }

        viewModel.apiResponse.observe(this){
            binding.progressCircular.visibility = View.GONE
            binding.btnLogin.visibility = View.VISIBLE
            Toast.makeText(this, "Login with User : "+it.userName, Toast.LENGTH_SHORT).show()
        }

        viewModel.errorMessage.observe(this){
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }

}