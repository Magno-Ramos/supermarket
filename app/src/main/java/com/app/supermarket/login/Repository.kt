package com.app.supermarket.login

import androidx.lifecycle.MutableLiveData
import com.app.supermarket.model.User

interface Repository {

    val userLiveData: MutableLiveData<User?>

    val signInStateLiveData: MutableLiveData<LoginState?>

    fun signInWithEmailAndPassword(email: String, password: String)
}