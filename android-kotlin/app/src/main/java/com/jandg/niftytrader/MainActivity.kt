package com.jandg.niftytrader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jandg.niftytrader.ui.screens.*
import com.jandg.niftytrader.ui.theme.*
import com.jandg.niftytrader.viewmodel.MarketViewModel
import com.jandg.niftytrader.viewmodel.TradeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NiftyTraderTheme {
                NiftyTraderApp()
            }
        }
    }
}

data class NavItem(val route: String, val label: String, val icon: ImageVector)

@Composable
fun NiftyTraderApp() {
    val marketVm: MarketViewModel = hiltViewModel()
    val tradeVm: TradeViewModel   = hiltViewModel()
    val state by marketVm.state.collectAsState()

    var currentTab by remember { mutableStateOf("setup") }

    // Settings state (in-memory, persisted via DataStore in production)
    var apiKey  by remember { mutableStateOf("") }
    var apiUrl  by remember { mutableStateOf(BuildConfig.API_BASE_URL) }
    var lotSize by remember { mutableStateOf(65) }
    var portfolio by remember { mutableStateOf(3041058) }
    var paperMode by remember { mutableStateOf(true) }

    val navItems = listOf(
        NavItem("setup",    "Setup",    Icons.Default.Assessment),
        NavItem("position", "Position", Icons.Default.ShowChart),
        NavItem("journal",  "Journal",  Icons.Default.Book),
        NavItem("refs",     "Refs",     Icons.Default.MenuBook),
        NavItem("settings", "Settings", Icons.Default.Settings),
    )

    Scaffold(
        containerColor = BgDeep,
        bottomBar = {
            NavigationBar(containerColor = BgMid, tonalElevation = 0.dp) {
                navItems.forEach { item ->
                    NavigationBarItem(
                        selected = currentTab == item.route,
                        onClick  = { currentTab = item.route },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = {
                            Text(item.label, fontFamily = Syne,
                                fontWeight = if (currentTab == item.route) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 10.sp)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor   = Cyan,
                            selectedTextColor   = Cyan,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor      = BgCard,
                        ),
                    )
                }
            }
        }
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .background(BgDeep)
                .padding(padding)
        ) {
            when (currentTab) {
                "setup" -> SetupScreen(
                    state          = state,
                    onRefresh      = { marketVm.refresh() },
                    onTrackPosition = { currentTab = "position" },
                    onLogTrade     = { currentTab = "journal" },
                )
                "position" -> PositionScreen(
                    vm           = tradeVm,
                    currentNifty = state.market?.nifty ?: 0.0,
                )
                "journal" -> JournalScreen(vm = tradeVm)
                "refs"    -> RefsScreen()
                "settings" -> SettingsScreen(
                    apiKey    = apiKey,
                    apiUrl    = apiUrl,
                    lotSize   = lotSize,
                    portfolio = portfolio,
                    paperMode = paperMode,
                    onApiKeyChange      = { apiKey = it },
                    onApiUrlChange      = { apiUrl = it },
                    onLotSizeChange     = { lotSize = it; marketVm.setLotSize(it) },
                    onPortfolioChange   = { portfolio = it },
                    onPaperModeChange   = { paperMode = it },
                    onSeedHistorical    = { tradeVm.seedHistoricalTrades() },
                )
            }
        }
    }
}
