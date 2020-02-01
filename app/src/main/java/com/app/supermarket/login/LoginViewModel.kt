package com.app.supermarket.login

import androidx.lifecycle.ViewModel
import javax.inject.Inject

class LoginViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    fun getSignInState() = repository.signInStateLiveData

    fun getUserLiveData() = repository.userLiveData

    fun signIn(email: String, password: String) {
        repository.signInWithEmailAndPassword(email, password)
    }
}