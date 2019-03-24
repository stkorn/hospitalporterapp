package com.ekstkorn.hospitalporterapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.ekstkorn.hospitalporterapp.view.LoginViewModel
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {

    private val loginViewModel by viewModel<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginViewModel.apply {
            viewState.observe(this@LoginActivity, Observer {
                it?.viewState?.let {
                    when (it) {

                        ViewState.LOADING -> {
                        }
                        ViewState.SUCCESS -> {
                        }
                        ViewState.ERROR -> {
                        }
                    }
                }
            })

            loginSucces.observe(this@LoginActivity, EventObserver {
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            })
        }


        buttonLogin.setOnClickListener {
            loginViewModel.login(
                    editTextUserId.text.toString(),
                    editTextPassword.text.toString())
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        loginViewModel.viewState.removeObservers(this)
        loginViewModel.loginSucces.removeObservers(this)
    }

}