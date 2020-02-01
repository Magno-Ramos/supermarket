package com.app.supermarket.api

import com.app.supermarket.model.Product
import io.reactivex.Single
import retrofit2.http.GET

interface RetrofitService {

    @GET("products")
    fun fetchProducts(): Single<List<Product>>
}