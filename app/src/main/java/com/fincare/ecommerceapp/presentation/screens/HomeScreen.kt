package com.fincare.ecommerceapp.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.fincare.ecommerceapp.R
import com.fincare.ecommerceapp.data.RetrofitInstance
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavHostController) {
    var productList by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    val favorites = remember { mutableStateOf(setOf<Int>()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val productListResponse = RetrofitInstance.api.getProducts()
                val tempList = productListResponse.map { product ->
                    mapOf(
                        "id" to product.id,
                        "title" to product.title,
                        "price" to product.price,
                        "image" to product.image,
                        "category" to product.category,
                        "rating" to product.rating.rate as Any
                    )
                }
                productList = tempList
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = "Location", tint = Color(0xFF6D4C41))
                    Text("New York, USA", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(start = 4.dp))
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                }

                Box {
                    Icon(Icons.Default.Notifications, contentDescription = "Notifications", modifier = Modifier.size(30.dp))
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Color.Red, shape = CircleShape)
                            .align(Alignment.TopEnd)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5), shape = MaterialTheme.shapes.medium)
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFF6D4C41))
                BasicTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                    decorationBox = { innerTextField ->
                        Box {
                            if (searchQuery.text.isEmpty()) {
                                Text("Search", color = Color.Gray)
                            }
                            innerTextField()
                        }
                    }
                )
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Filter",
                    tint = Color(0xFF6D4C41),
                    modifier = Modifier.size(30.dp)
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Column {
                Spacer(modifier = Modifier.height(16.dp))

                PromotionalBanner()
                Spacer(modifier = Modifier.height(16.dp))

                CategorySection()
                Spacer(modifier = Modifier.height(16.dp))

                FlashSaleSection(productList, navController, favorites)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun FlashSaleSection(
    productList: List<Map<String, Any>>,
    navController: NavHostController,
    favorites: MutableState<Set<Int>>
) {
    var selectedTab by remember { mutableStateOf("All") }
    val categories = listOf("All", "Newest", "Popular", "Men", "Women")

    val filteredProducts by remember(selectedTab, productList) {
        derivedStateOf {
            when (selectedTab) {
                "Newest" -> productList.sortedByDescending { (it["timestamp"] as? Long) ?: System.currentTimeMillis() }
                "Popular" -> productList.filter { (it["isPopular"] as? Boolean) ?: false }
                "Men" -> productList.filter { it["category"]?.toString()?.lowercase()?.trim() == "men's clothing" }
                "Women" -> productList.filter { it["category"]?.toString()?.lowercase()?.trim() == "women's clothing" }
                else -> productList
            }
        }
    }


    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Flash Sale", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            CountdownTimer(targetTime = System.currentTimeMillis() + 2 * 60 * 60 * 1000)
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                Button(
                    onClick = { selectedTab = category },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTab == category) Color(0xFF6D4C41) else Color.White,
                        contentColor = if (selectedTab == category) Color.White else Color.Black
                    ),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, Color.LightGray),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text(category, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (filteredProducts.isEmpty()) {
            Text(
                text = "No products available",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 16.sp
            )
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filteredProducts.chunked(2).forEach { rowProducts ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowProducts.forEach { product ->
                                val productId = product["id"] as? Int ?: return@forEach

                                Box(modifier = Modifier.weight(1f)) {
                                    ProductCard(
                                        product = product,
                                        isFavorite = favorites.value.contains(productId),
                                        onFavoriteClick = {
                                            favorites.value = favorites.value.toMutableSet().apply {
                                                if (contains(productId)) remove(productId) else add(productId)
                                            }
                                        },
                                        onClick = {
                                            val id = product["id"] as? Int
                                            if (id != null) {
                                                navController.navigate("ProductDetail/$id")
                                            }
                                        }
                                    )
                                }
                            }

                            if (rowProducts.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

        }
    }
}


@Composable
fun ProductCard(
    product: Map<String, Any>,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit
) {
    val imageUrl = product["image"] as? String ?: ""
    val title = product["title"] as? String ?: "No Title"
    val price = product["price"] as? Double ?: 0.0
    val rating = product["rating"] as? Double


    Card(
        modifier = Modifier
            .width(180.dp)
            .height(260.dp)
            .padding(8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Product Image",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .background(Color.White, CircleShape)
                        .size(28.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color.Red else Color(0xFF6D4C41)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Rating",
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$rating",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Text(
                text = "$$price",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF6D4C41),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}


@Composable
fun CountdownTimer(targetTime: Long) {
    var timeLeft by remember { mutableStateOf(targetTime - System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000L)
            timeLeft = targetTime - System.currentTimeMillis()
        }
    }

    val hours = (timeLeft / (1000 * 60 * 60)) % 24
    val minutes = (timeLeft / (1000 * 60)) % 60
    val seconds = (timeLeft / 1000) % 60

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text("Closing in:", fontSize = 14.sp, color = Color.Gray)
        TimeBox(hours)
        Text(":", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        TimeBox(minutes)
        Text(":", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        TimeBox(seconds)
    }
}

@Composable
fun TimeBox(value: Long) {
    Box(
        modifier = Modifier
            .background(Color(0xFFF2E7DB), shape = RoundedCornerShape(4.dp))
            .padding(6.dp)
    ) {
        Text(text = value.toString().padStart(2, '0'), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF6D4C41))
    }
}


@Composable
fun PromotionalBanner() {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val banners = listOf(
        R.drawable.img1,
        R.drawable.img2,
        R.drawable.img3    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFFFF3E0))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("New Collection", style = MaterialTheme.typography.titleLarge, color = Color.Black)
                        Text("Discount 50% for\nthe first transaction", color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {  },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D4C41))
                            ,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Shop Now", color = Color.White)
                        }
                    }

                    AsyncImage(
                        model = banners[page],
                        contentDescription = "Banner Image",
                        modifier = Modifier.size(120.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        DotsIndicator(
            totalDots = banners.size,
            selectedIndex = pagerState.currentPage
        )
    }
}

@Composable
fun DotsIndicator(
    totalDots: Int,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    dotSize: Dp = 10.dp,
    selectedDotSize: Dp = 12.dp,
    dotSpacing: Dp = 6.dp
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(dotSpacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until totalDots) {
            Box(
                modifier = Modifier
                    .size(if (i == selectedIndex) selectedDotSize else dotSize)
                    .clip(CircleShape)
                    .background(if (i == selectedIndex) Color(0xFF6D4C41) else Color.LightGray)
            )
        }
    }
}

@Composable
fun CategorySection() {
    var categories by remember { mutableStateOf(listOf<Pair<String, String?>>()) } // Pair: Category Name, Image URL (nullable)
    var showAll by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val fallbackImages = listOf(
        R.drawable.elctronic,
        R.drawable.jw,
        R.drawable.mens,
        R.drawable.img1
    )
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val categoryList = RetrofitInstance.api.getCategories()
                val tempList = categoryList.map { categoryName ->
                    categoryName to null
                }
                categories = tempList
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Category", style = MaterialTheme.typography.titleLarge)
            Text(
                text = if (showAll) "Show Less" else "See All",
                color = Color.Gray,
                modifier = Modifier.clickable { showAll = !showAll }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        val displayedCategories = if (showAll) categories else categories.take(4)

        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            itemsIndexed(displayedCategories) { index, (category, imageUrl) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageUrl != null) {
                            Image(
                                painter = rememberImagePainter(data = imageUrl),
                                contentDescription = category,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = painterResource(id = fallbackImages[index % fallbackImages.size]),
                                contentDescription = category,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(category.capitalize(), color = Color.Black)
                }
            }
        }
    }
}
