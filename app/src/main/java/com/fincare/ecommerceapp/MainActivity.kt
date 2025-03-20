package com.fincare.ecommerceapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.fincare.ecommerceapp.presentation.screens.HomeScreen
import com.fincare.ecommerceapp.presentation.screens.ProductDetailScreen
import com.fincare.ecommerceapp.ui.theme.EcommerceappTheme

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

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnusedMaterial3ScaffoldPaddingParameter")
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

    Scaffold(
        bottomBar = { if (navController.currentDestination?.route != "ProductDetail/{productId}") CustomBottomNavigationBar(navController) }
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

    val currentRoute by navController.currentBackStackEntryAsState()
    val selectedTab = currentRoute?.destination?.route ?: "Home"

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
            items.forEach { (label, icon) ->
                val isSelected = selectedTab == label

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(
                            if (isSelected) Color.White else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable {
                            navController.navigate(label) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (isSelected) Color(0xFF815B3A) else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun NavigationGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController, startDestination = "Home", modifier = modifier) {
        composable("Home") { HomeScreen(navController) }
        composable("Cart") { CartScreen() }
        composable("Favorites") { FavoritesScreen() }
        composable("Messages") { MessagesScreen() }
        composable("Profile") { ProfileScreen() }
        composable("ProductDetail/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")?.toIntOrNull()
            ProductDetailScreen(productId, navController)
        }
    }
}

// Screens (Placeholders)
@Composable fun CartScreen() = ScreenContent("Cart")
@Composable fun FavoritesScreen() = ScreenContent("Favorites")
@Composable fun MessagesScreen() = ScreenContent("Messages")
@Composable fun ProfileScreen() = ScreenContent("Profile")

@Composable
fun ScreenContent(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "$title Screen", modifier = Modifier.padding(16.dp))
    }
}

@Preview
@Composable
fun PreviewMainScreen() {
    EcommerceappTheme {
        MainScreen()
    }
}
