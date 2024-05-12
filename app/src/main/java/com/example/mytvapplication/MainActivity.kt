package com.example.mytvapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.example.mytvapplication.ui.theme.MyTVApplicationTheme
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = Firebase.database(Constants.database_url)
        val myRef = database.getReference(Constants.reference)
        setContent {
            MyTVApplicationTheme {

                val viewModel = hiltViewModel<MainViewModel>()
                var lifeCycle by remember {
                    mutableStateOf(Lifecycle.Event.ON_CREATE)
                }

                val lifeCycleOwner = LocalLifecycleOwner.current

                val postListener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val post = dataSnapshot.getValue<List<String>>()
                        viewModel.addVideoUri(post)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                    }
                }

                myRef.addValueEventListener(postListener)

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
                            .fillMaxSize()
                            .aspectRatio(16 / 9f)
                    )
                }
            }
        }
    }
}