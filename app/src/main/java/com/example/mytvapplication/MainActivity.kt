package com.example.mytvapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileOpen
import androidx.tv.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.ui.PlayerView
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import com.example.mytvapplication.ui.theme.MyTVApplicationTheme
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.app
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTVApplicationTheme {
                Firebase.analytics.logEvent("Tv_started",null)
                val viewModel = hiltViewModel<MainViewModel>()
                val videoItems by viewModel.videoItems.collectAsState(emptyList())

                var lifeCycle by remember {
                    mutableStateOf(Lifecycle.Event.ON_CREATE)
                }

                val lifeCycleOwner = LocalLifecycleOwner.current

                DisposableEffect(key1 = lifeCycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        lifeCycle = event
                    }
                    lifeCycleOwner.lifecycle.addObserver(observer)
                    onDispose {
                        lifeCycleOwner.lifecycle.removeObserver(observer)
                    }
                }

                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxSize()
                ) {
                    AndroidView(
                        factory = { context ->
                            PlayerView(context).also {
                                it.player = viewModel.player
                            }
                        }, update = {
                            when (lifeCycle) {
                                Lifecycle.Event.ON_RESUME -> {
                                    it.onResume()
                                }

                                Lifecycle.Event.ON_PAUSE -> {
                                    it.onPause()
                                    it.player?.pause()
                                }

                                Lifecycle.Event.ON_CREATE -> {
                                }

                                Lifecycle.Event.ON_START -> {
                                }

                                Lifecycle.Event.ON_STOP -> {
                                    it.onPause()
                                    it.player?.pause()


                                }

                                Lifecycle.Event.ON_DESTROY -> {
                                }

                                Lifecycle.Event.ON_ANY -> {

                                }

                                else -> {
                                }
                            }
                        }, modifier = Modifier
                            .clickable {
                                Firebase.analytics.logEvent("hello",null)
                            }
                            .fillMaxSize()
                            .aspectRatio(16 / 9f)
                    )
                }
            }
        }
    }
}