package com.fincare.ecommerceapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.fincare.ecommerceapp.model.Product
import com.fincare.ecommerceapp.presentation.screens.HomeScreen
import com.fincare.ecommerceapp.presentation.screens.ProductDetailScreen
import com.fincare.ecommerceapp.ui.theme.EcommerceappTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.URL

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EcommerceappTheme {
                MainScreen()
            }
        }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "RememberReturnType",
    "UnusedMaterial3ScaffoldPaddingParameter"
)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val favorites = remember { mutableStateOf(setOf<Int>()) }

    val productList = remember {
        listOf(
            mapOf("id" to 1, "title" to "Brown Jacket", "price" to 83.97, "image" to "https://example.com/jacket1.jpg", "category" to "men's clothing"),
            mapOf("id" to 2, "title" to "Brown Suit", "price" to 120.00, "image" to "https://example.com/suit.jpg", "category" to "men's clothing"),
            mapOf("id" to 3, "title" to "Brown Jacket", "price" to 83.97, "image" to "https://example.com/jacket2.jpg", "category" to "women's clothing"),
            mapOf("id" to 4, "title" to "Yellow Shirt", "price" to 120.00, "image" to "https://example.com/shirt.jpg", "category" to "women's clothing")
        )
    }
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    Scaffold(
        bottomBar = {
            if (currentRoute != "ProductDetail/{productId}") {
                CustomBottomNavigationBar(navController)
            }
        }
    ) {
        NavigationGraph(navController)
    }
}
@Composable
fun CustomBottomNavigationBar(navController: NavController) {
    val items = listOf(
        "Home" to Icons.Outlined.Home,
        "Cart" to Icons.Outlined.ShoppingCart,
        "Favorites" to Icons.Outlined.FavoriteBorder,
        "Messages" to Icons.Outlined.MailOutline,
        "Profile" to Icons.Outlined.Person
    )

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val selectedItem = items.indexOfFirst { it.first == currentRoute }.takeIf { it != -1 } ?: 0

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(50))
            .background(Color(0xFF1A1A1E)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, (label, icon) ->
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(
                            if (selectedItem == index) Color.White else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable {
                            navController.navigate(label) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (selectedItem == index) Color(0xFF815B3A) else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun NavigationGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier) {
    NavHost(navController, startDestination = "Home", modifier = modifier) {
        composable("Home") { HomeScreen(navController) }
        composable("Cart") { CartScreen() }
        composable("Favorites") { FavoritesScreen() }
        composable("Messages") { MessagesScreen() }
        composable("Profile") { ProfileScreen() }
        composable("ProductDetail/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")?.toIntOrNull()
            ProductDetailScreen(productId, navController,)
        }


    }
}

@Composable
fun CartScreen() {
    Text("Cart Screen", modifier = Modifier.padding(16.dp))
}

@Composable
fun FavoritesScreen() {
    Text("Favorites Screen", modifier = Modifier.padding(16.dp))
}

@Composable
fun MessagesScreen() {
    Text("Messages Screen", modifier = Modifier.padding(16.dp))
}

@Composable
fun ProfileScreen() {
    Text("Profile Screen", modifier = Modifier.padding(16.dp))
}


@Preview
@Composable
fun PreviewMainScreen() {
    EcommerceappTheme {
        MainScreen()
    }
}
