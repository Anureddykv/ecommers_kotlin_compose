package com.fincare.ecommerceapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fincare.ecommerceapp.domain.usecases.GetCategoriesUseCase
import com.fincare.ecommerceapp.domain.usecases.GetProductsUseCase
import com.fincare.ecommerceapp.model.Category
import com.fincare.ecommerceapp.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    // StateFlows for Products and Categories
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Initialize Data Fetching
    init {
        fetchProducts()
        fetchCategories()
    }

    // Fetch Products from API
    private fun fetchProducts() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val productList = getProductsUseCase()
                _products.value = productList
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Fetch Categories from API
    private fun fetchCategories() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val categoryList = getCategoriesUseCase()
                _categories.value = categoryList
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage
            } finally {
                _isLoading.value = false
            }
        }
    }
}
