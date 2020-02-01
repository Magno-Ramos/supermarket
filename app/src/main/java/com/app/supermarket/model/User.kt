package com.app.supermarket.model

import java.io.Serializable

data class User(
    val name: String,
    val email: String
) : Serializable