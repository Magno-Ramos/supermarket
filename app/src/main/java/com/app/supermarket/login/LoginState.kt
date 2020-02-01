package com.app.supermarket.login

data class LoginState(
    val status: Int,
    val error: String? = null
) {

    fun isSuccess() = status == STATUS_SUCCESS

    fun isError() = status == STATUS_ERROR

    fun isLoading() = status == STATUS_LOADING

    companion object {
        private const val STATUS_LOADING = 1
        private const val STATUS_SUCCESS = 2
        private const val STATUS_ERROR = 3

        val LOADING = LoginState(STATUS_LOADING)
        val SUCCESS = LoginState(STATUS_SUCCESS)

        fun error(message: String): LoginState {
            return LoginState(STATUS_ERROR, message)
        }
    }
}