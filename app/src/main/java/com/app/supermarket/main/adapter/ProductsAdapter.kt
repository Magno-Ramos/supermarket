package com.app.supermarket.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.app.supermarket.R
import com.app.supermarket.model.Product
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.product_item.view.*
import java.text.NumberFormat

class ProductsAdapter(private val products: List<Product>) :
    RecyclerView.Adapter<ProductsAdapter.Holder>() {

    private val formatter = NumberFormat.getCurrencyInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val product = products[position]
        holder.bindView(product)
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(product: Product) {
            itemView.tv_name?.text = product.name
            itemView.tv_price?.text = formatter.format(product.price)

            itemView.image_view?.also {
                it.scaleType = ImageView.ScaleType.CENTER_CROP
                if (product.images.isNotEmpty()) {
                    Picasso.get()
                        .load(product.images[0])
                        .into(it)
                }
            }
        }
    }
}