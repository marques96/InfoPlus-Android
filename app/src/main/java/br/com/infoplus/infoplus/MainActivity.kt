package br.com.infoplus.infoplus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import br.com.infoplus.infoplus.navigation.AppNavGraph
import br.com.infoplus.infoplus.ui.theme.InfoPlusTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.runtime.collectAsState

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            val themeVm: br.com.infoplus.infoplus.core.settings.ThemeViewModel =
                androidx.hilt.navigation.compose.hiltViewModel()

            val isDark = themeVm.isDark.collectAsState().value

            InfoPlusTheme(darkTheme = isDark) {
                AppNavGraph()
            }
        }
    }
}
