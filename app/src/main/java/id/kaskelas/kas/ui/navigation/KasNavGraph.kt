package id.kaskelas.kas.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import id.kaskelas.kas.ui.dashboard.DashboardScreen
import id.kaskelas.kas.ui.lock.LockScreen
import id.kaskelas.kas.ui.report.ReportScreen
import id.kaskelas.kas.ui.settings.SettingsScreen
import id.kaskelas.kas.ui.transaction.TransactionFormScreen
import id.kaskelas.kas.ui.transaction.TransactionListScreen

object Routes {
    const val LOCK = "lock"
    const val MAIN_GRAPH_START = "beranda"
    const val TRANSACTION_FORM = "transaksi_form"
    const val TRANSACTION_FORM_ARG = "transaksi_form?transactionId={transactionId}"
}

@Composable
fun KasNavGraph(navController: NavHostController = rememberNavController()) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = currentRoute != null && currentRoute != Routes.LOCK

    // Di layar lock, tombol back tidak boleh keluar app ke main graph.
    if (currentRoute == Routes.LOCK) {
        BackHandler(enabled = true) { /* tetap di lock */ }
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                KasBottomBar(navController, currentRoute)
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.LOCK,
            modifier = Modifier.padding(padding),
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(250))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(250))
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(250))
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(250))
            },
        ) {
            composable(Routes.LOCK) {
                LockScreen(onUnlocked = {
                    navController.navigate(Tab.BERANDA.route) {
                        popUpTo(Routes.LOCK) { inclusive = true }
                    }
                })
            }
            composable(Tab.BERANDA.route) {
                DashboardScreen()
            }
            composable(Tab.TRANSAKSI.route) {
                TransactionListScreen(
                    onAddTransaction = {
                        navController.navigate(Routes.TRANSACTION_FORM)
                    },
                    onEditTransaction = { id ->
                        navController.navigate("transaksi_form?transactionId=$id")
                    },
                )
            }
            composable(Tab.LAPORAN.route) {
                ReportScreen()
            }
            composable(Tab.PENGATURAN.route) {
                SettingsScreen()
            }
            composable(
                route = "transaksi_form?transactionId={transactionId}",
                arguments = listOf(
                    navArgument("transactionId") {
                        type = NavType.LongType
                        defaultValue = -1L
                    },
                ),
            ) { entry ->
                val id = entry.arguments?.getLong("transactionId") ?: -1L
                TransactionFormScreen(
                    transactionId = if (id == -1L) null else id,
                    onBack = { navController.popBackStack() },
                    onTransactionSaved = { navController.popBackStack() },
                )
            }
        }
    }
}
