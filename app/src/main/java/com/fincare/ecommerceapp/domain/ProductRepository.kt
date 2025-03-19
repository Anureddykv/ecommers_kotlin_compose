package com.fincare.ecommerceapp.domain

import com.fincare.ecommerceapp.model.Category
import com.fincare.ecommerceapp.model.Product


interface ProductRepository {
    suspend fun getProductList(): List<Product>
    suspend fun getCategories(): List<Category>
}

