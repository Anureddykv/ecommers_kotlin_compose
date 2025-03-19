package com.fincare.ecommerceapp.domain.usecases

import com.fincare.ecommerceapp.domain.ProductRepository
import com.fincare.ecommerceapp.model.Category


class GetCategoriesUseCase(private val productRepository: ProductRepository) {

    suspend operator fun invoke(): List<Category> {
        return productRepository.getCategories()
    }
}
