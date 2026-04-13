package com.bafanam.logistics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bafanam.logistics.ui.ConsignmentRoute
import com.bafanam.logistics.ui.ConsignmentViewModel
import com.bafanam.logistics.ui.theme.LogisticsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LogisticsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    LogisticsRoot()
                }
            }
        }
    }
}

@Composable
private fun LogisticsRoot() {
    val container = (LocalContext.current.applicationContext as LogisticsApplication).appContainer
    val vm: ConsignmentViewModel = viewModel(factory = ConsignmentViewModel.factory(container))
    ConsignmentRoute(vm)
}
