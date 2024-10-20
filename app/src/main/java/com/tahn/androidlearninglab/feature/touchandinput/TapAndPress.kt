package com.tahn.androidlearninglab.feature.touchandinput

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.tahn.androidlearninglab.utils.rememberRandomSampleImageUrl

/**
 * ref: https://developer.android.com/develop/ui/compose/touch-input/pointer-input/tap-and-press
 */

// model
private class Photo(
    val id: Int,
    val url: String,
    val highResUrl: String,
)

@Preview
@Composable
private fun MyApp() {
    val photos = List(100) {
        val url = rememberRandomSampleImageUrl(width = 256)
        Photo(it, url, url.replace("256", "1024"))
    }
    ImageGrid(photos)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ImageGrid(photos: List<Photo>) {
    var activePhotoId by rememberSaveable { mutableStateOf<Int?>(null) }
    var contextMenuPhotoId by rememberSaveable { mutableStateOf<Int?>(null) }
    val haptics = LocalHapticFeedback.current
    LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 128.dp)) {
        items(photos, { it.id }) { photo ->
            ImageItem(
                photo,
                Modifier
                    .combinedClickable(
                        onClick = { activePhotoId = photo.id },
                        onLongClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            contextMenuPhotoId = photo.id
                        },
                        onLongClickLabel = "open_context_menu"
                    )
            )
        }
    }
    if (contextMenuPhotoId != null) {
        PhotoActionsSheet(
            photo = photos.first { it.id == contextMenuPhotoId },
            onDismissSheet = { contextMenuPhotoId = null }
        )
    }
    if (activePhotoId != null) {
        FullScreenImage(
            photo = photos.first { it.id == activePhotoId },
            onDismiss = { activePhotoId = null }
        )
    }
}

@Composable
private fun ImageItem(photo: Photo, modifier: Modifier = Modifier) {
    Image(
        painter = rememberAsyncImagePainter(model = photo.url),
        contentDescription = null,
        modifier = modifier.aspectRatio(1f)
    )
}

@Composable
private fun FullScreenImage(
    photo: Photo,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Scrim(onDismiss, Modifier.fillMaxSize())
        ImageWithZoom(photo, Modifier.aspectRatio(1f))
    }
}

@Composable
private fun Scrim(onClose: () -> Unit, modifier: Modifier = Modifier) {
    val strClose = "Close"
    Box(
        modifier
            // handle pointer input
            // [START android_compose_touchinput_pointerinput_scrim_highlight]
            .pointerInput(onClose) { detectTapGestures { onClose() } }
            // [END android_compose_touchinput_pointerinput_scrim_highlight]
            // handle accessibility services
//            .semantics(mergeDescendants = true) {
//                contentDescription = strClose
//                onClick {
//                    onClose()
//                    true
//                }
//            }
//            // handle physical keyboard input
//            .onKeyEvent {
//                if (it.key == Escape) {
//                    onClose()
//                    true
//                } else {
//                    false
//                }
//            }
            // draw scrim
            .background(Color.DarkGray.copy(alpha = 0.75f))
    )
}

@Composable
private fun ImageWithZoom(photo: Photo, modifier: Modifier = Modifier) {
    // [START android_compose_touchinput_pointerinput_double_tap_zoom]
    var zoomed by remember { mutableStateOf(false) }
    var zoomOffset by remember { mutableStateOf(Offset.Zero) }
    Image(
        painter = rememberAsyncImagePainter(model = photo.highResUrl),
        contentDescription = null,
        modifier = modifier
            // [START android_compose_touchinput_pointerinput_double_tap_zoom_highlight]
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { tapOffset ->
                        zoomOffset = if (zoomed) Offset.Zero else
                            calculateOffset(tapOffset, size)
                        zoomed = !zoomed
                    }
                )
            }
            // [END android_compose_touchinput_pointerinput_double_tap_zoom_highlight]
            .graphicsLayer {
                scaleX = if (zoomed) 2f else 1f
                scaleY = if (zoomed) 2f else 1f
                translationX = zoomOffset.x
                translationY = zoomOffset.y
            }
    )
    // [END android_compose_touchinput_pointerinput_double_tap_zoom]
}

private fun calculateOffset(tapOffset: Offset, size: IntSize): Offset {
    val offsetX = (-(tapOffset.x - (size.width / 2f)) * 2f)
        .coerceIn(-size.width / 2f, size.width / 2f)
    return Offset(offsetX, 0f)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PhotoActionsSheet(
    @Suppress("UNUSED_PARAMETER") photo: Photo,
    onDismissSheet: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissSheet
    ) {
        ListItem(
            headlineContent = { Text("Add to album") },
            leadingContent = { Icon(Icons.Default.Add, null) }
        )
        ListItem(
            headlineContent = { Text("Add to favorites") },
            leadingContent = { Icon(Icons.Default.FavoriteBorder, null) }
        )
        ListItem(
            headlineContent = { Text("Share") },
            leadingContent = { Icon(Icons.Default.Share, null) }
        )
        ListItem(
            headlineContent = { Text("Remove") },
            leadingContent = { Icon(Icons.Default.Delete, null) }
        )
    }
}