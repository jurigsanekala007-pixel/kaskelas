package id.kaskelas.kas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import id.kaskelas.kas.ui.KasKelasRoot
import id.kaskelas.kas.ui.theme.KasKelasTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KasKelasTheme {
                KasKelasRoot()
            }
        }
    }
}
