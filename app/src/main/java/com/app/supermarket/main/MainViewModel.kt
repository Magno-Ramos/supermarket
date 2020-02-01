package com.app.supermarket.main

import androidx.lifecycle.ViewModel
import com.app.supermarket.api.RetrofitService
import com.app.supermarket.model.Product
import io.reactivex.Single
import javax.inject.Inject

class MainViewModel @Inject constructor(private val service: RetrofitService) : ViewModel() {

    fun loadProducts(): Single<List<Product>> {
        return service.fetchProducts()
    }
}