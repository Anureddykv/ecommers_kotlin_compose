package com.fincare.ecommerceapp.model

data class Product(
    val id: Int,
    val title: String,
    val description: String,
    val image: String,
    val price: Double,
    val rating: Rating,

    )


data class Rating(
    val rate: Float,
    val count: Int
)
