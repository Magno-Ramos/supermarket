package com.app.supermarket.model

import java.io.Serializable

data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val description: String,
    val images: List<String>
) : Serializable