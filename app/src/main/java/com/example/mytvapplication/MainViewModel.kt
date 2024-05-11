package com.example.mytvapplication

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.example.mytvapplication.model.VideoItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    val player: Player,
    private val metaDataReader: MetaDataReader
) : ViewModel() {

    private val videoUris = savedStateHandle.getStateFlow("videoUris", emptyList<Uri>())
    private val videoUris2 = savedStateHandle.getLiveData("videoUris2", emptyList<Uri>())

    val videoItems = videoUris.map { uris ->
        uris.map { uri ->
            VideoItem(
                contentUri = uri,
                mediaItem = MediaItem.fromUri(uri),
                name = metaDataReader.getMetaDataFromUri(uri)?.fileName ?: "No Name"
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

//    val videoItems2 = videoUris2.map { uris ->
//        uris.map { uri ->
//            VideoItem(
//                contentUri = uri,
//                mediaItem = MediaItem.fromUri(uri),
//                name = metaDataReader.getMetaDataFromUri(uri)?.fileName ?: "No Name"
//            )
//        }
//    }

    init {
        addVideoUri()
        player.prepare()
//        player.playWhenReady = true
    }

    fun addVideoUri() {
//        savedStateHandle["videoUris"] = videoUris.value + uri
//        savedStateHandle["videoUris2"] = videoUris2.value?.plus(uri)

//        player.addMediaItem(MediaItem.fromUri(uri))
//        player.addMediaItem(MediaItem.fromUri("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"))
        player.addMediaItem(MediaItem.fromUri("https://commondatastorage.googleapis.com/android-tv/Sample%20videos/Zeitgeist/Zeitgeist%202010_%20Year%20in%20Review.mp4"))
    }

    fun playVideo(uri: Uri) {
        player.setMediaItem(
            videoItems.value.find {
                it.contentUri == uri
            }?.mediaItem ?: return
        )

//        player.setMediaItem(
//            videoItems2.value?.find { it.contentUri == uri }?.mediaItem ?: return
//        )
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }
}