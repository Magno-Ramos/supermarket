package com.app.supermarket.login

import androidx.lifecycle.MutableLiveData
import com.app.supermarket.model.User
import javax.inject.Inject

class LoginRepository @Inject constructor() : Repository {

    override val userLiveData: MutableLiveData<User?> = MutableLiveData()

    override val signInStateLiveData: MutableLiveData<LoginState?> = MutableLiveData()

    override fun signInWithEmailAndPassword(email: String, password: String) {
        signInStateLiveData.value = LoginState.LOADING
        if (email == EMAIL && password == PASS) {
            signInStateLiveData.value = LoginState.SUCCESS
            userLiveData.value = MOCK_USER
        } else {
            signInStateLiveData.value = LoginState.error("Email ou senha inválida")
        }
    }

    companion object {
        const val EMAIL = "user@admin.com"
        const val PASS = "admin123"
        val MOCK_USER = User("Josh", EMAIL)
    }
}