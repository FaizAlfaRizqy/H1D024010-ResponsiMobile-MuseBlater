package com.pemmob.museblater

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.pemmob.museblater.ui.navigation.MuseBlaterNavGraph
import com.pemmob.museblater.ui.theme.MuseBlaterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MuseBlaterTheme {
                MuseBlaterNavGraph()
            }
        }
    }
}