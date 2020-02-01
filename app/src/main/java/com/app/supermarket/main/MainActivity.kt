package com.app.supermarket.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.supermarket.MarketApplication
import com.app.supermarket.R
import com.app.supermarket.main.adapter.ProductsAdapter
import com.app.supermarket.model.Product
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var mainViewModel: MainViewModel

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as MarketApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val user = intent.extras?.getSerializable(INTENT_KEY_USER)
        user?.let { loadProducts() }
    }

    private fun loadProducts() {
        val disposable = mainViewModel.loadProducts()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { data -> setupProducts(data) },
                { err -> Toast.makeText(this, err.message, Toast.LENGTH_SHORT).show() }
            )

        compositeDisposable.add(disposable)
    }

    private fun setupProducts(products: List<Product>) {
        recycler_view?.let {
            val adapter = ProductsAdapter(products)

            it.layoutManager = LinearLayoutManager(this)
            it.adapter = adapter
        }
    }

    companion object {
        const val INTENT_KEY_USER = "com.app.USER_INTENT_KEY"
    }
}
