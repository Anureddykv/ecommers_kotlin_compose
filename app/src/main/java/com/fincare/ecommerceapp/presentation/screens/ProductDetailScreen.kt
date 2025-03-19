package com.fincare.ecommerceapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.fincare.ecommerceapp.R
import com.fincare.ecommerceapp.data.RetrofitInstance
import com.fincare.ecommerceapp.model.Product

@Composable
fun ProductDetailScreen(productId: Int?, navController: NavController/*, viewModel: MainViewModel*/) {
    val product = remember { mutableStateOf<Product?>(null) }
    val isLoading = remember { mutableStateOf(true) }
    val selectedSize = remember { mutableStateOf<String?>(null) }
    val selectedColor = remember { mutableStateOf<Color?>(null) }
    val isFavorite = remember { mutableStateOf(false) }

    LaunchedEffect(productId) {
        if (productId != null) {
            try {
                val response = RetrofitInstance.api.getProduct(productId)
                product.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading.value = false
            }
        }
    }

    if (isLoading.value) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (product.value == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Product not found", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Red)
        }
    } else {
        val data = product.value!!

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Text(
                        text = "Product Details",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    IconButton(onClick = { isFavorite.value = !isFavorite.value }) {
                        Icon(
                            imageVector = if (isFavorite.value) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite.value) Color.Red else Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            item {
                AsyncImage(
                    model = data.image,
                    contentDescription = "Product Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(500.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            item {
                val imageUrls = listOf(
                    R.drawable.img1,
                    R.drawable.img2,
                    R.drawable.img1,
                    R.drawable.img3,

                    )

                Text(
                    text = "Product Gallery",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                ProductGallery(images = imageUrls)
            }


            item {
                Text(text = data.title, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, contentDescription = "Rating", tint = Color(0xFFFFC107))
                    Text(text = data.rating.rate.toString(), fontSize = 16.sp, color = Color.Gray)
                }
                Text(text = "$${data.price}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6D4C41))
                Text(
                    text = data.description,
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                Text(text = "Select Size", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val availableSizes = listOf("S", "M", "L", "XL", "XXL")
                    items(availableSizes) { size ->
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selectedSize.value == size) Color.Gray else Color.LightGray)
                                .clickable { selectedSize.value = size },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = size, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                Text(text = "Select Color", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val availableColors = listOf(
                        Color.Red,
                        Color.Green,
                        Color.Blue,
                        Color.Yellow,
                        Color.Cyan
                    )
                    items(availableColors) { color ->
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (selectedColor.value == color) 3.dp else 0.dp,
                                    color = Color.Black,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor.value = color }
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total Price: $${data.price}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Button(
                        onClick = {
                            println("Added to cart: Color = ${selectedColor.value}, Size = ${selectedSize.value}")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B4513)),
                        modifier = Modifier
                            .height(50.dp)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        Text(text = "Add to Cart", color = Color.White, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ProductGallery(images: List<Int>) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(images) { imageUrl ->
            AsyncImage(
                model = imageUrl,
                contentDescription = "Product Image",
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}
