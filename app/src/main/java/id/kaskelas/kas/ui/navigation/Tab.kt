package id.kaskelas.kas.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class Tab(val route: String, val label: String, val icon: ImageVector, val iconActive: ImageVector) {
    BERANDA("beranda", "Beranda", Icons.Outlined.Home, Icons.Filled.Home),
    TRANSAKSI("transaksi", "Transaksi", Icons.AutoMirrored.Outlined.ReceiptLong, Icons.AutoMirrored.Filled.ReceiptLong),
    LAPORAN("laporan", "Laporan", Icons.AutoMirrored.Outlined.ListAlt, Icons.Filled.BarChart),
    PENGATURAN("pengaturan", "Pengaturan", Icons.Outlined.Settings, Icons.Filled.Settings),
}
