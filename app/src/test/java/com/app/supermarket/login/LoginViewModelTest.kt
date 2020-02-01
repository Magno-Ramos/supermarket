package com.app.supermarket.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.app.supermarket.model.User
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {

    private val viewModel: LoginViewModel = LoginViewModel(LoginRepository())

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Test
    fun signInSuccess() {
        viewModel.signIn(LoginRepository.EMAIL, LoginRepository.PASS)
        Assert.assertTrue(viewModel.getSignInState().value == LoginState.SUCCESS)
        Assert.assertTrue(viewModel.getUserLiveData().value is User)
    }

    @Test
    fun signInError() {
        viewModel.signIn("", "")
        Assert.assertTrue(viewModel.getSignInState().value == LoginState.error("Email ou senha inválida"))
        Assert.assertTrue(viewModel.getUserLiveData().value == null)
    }
}