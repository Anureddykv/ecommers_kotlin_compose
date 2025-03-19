package com.fincare.ecommerceapp.domain.usecases

import com.fincare.ecommerceapp.domain.ProductRepository
import com.fincare.ecommerceapp.model.Product


class GetProductsUseCase(private val productRepository: ProductRepository) {
    suspend operator fun invoke(): List<Product> {
        return productRepository.getProductList()
    }
}
