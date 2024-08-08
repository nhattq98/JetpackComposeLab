package com.tahn.androidlearninglab.feature.touchandinput

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.tahn.androidlearninglab.feature.mock.mockListData


/**
 * ref: https://developer.android.com/develop/ui/compose/touch-input/pointer-input/scroll
 * https://medium.com/androiddevelopers/understanding-nested-scrolling-in-jetpack-compose-eb57c1ea0af0
 */
@Composable
private fun ScrollBoxesSmooth() {
    // Smoothly scroll 100px on first composition
    val state = rememberScrollState()
    LaunchedEffect(Unit) { state.animateScrollTo(100) }

    Column(
        modifier = Modifier
            .background(Color.LightGray)
            .size(100.dp)
            .padding(horizontal = 8.dp)
            .verticalScroll(state)
    ) {
        repeat(10) {
            Text("Item $it", modifier = Modifier.padding(2.dp))
        }
    }
}

@Composable
private fun ScrollableSample() {
    // actual composable state
    var offset by remember { mutableFloatStateOf(0f) }
    Box(
        Modifier
            .size(150.dp)
            .scrollable(
                orientation = Orientation.Vertical,
                // Scrollable state: describes how to consume
                // scrolling delta and update offset
                state = rememberScrollableState { delta ->
                    offset += delta
                    delta
                }
            )
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        Text(offset.toString())
    }
}

@Composable
private fun AutomaticNestedScroll() {
    val gradient = Brush.verticalGradient(0f to Color.Gray, 1000f to Color.White)
    Box(
        modifier = Modifier
            .background(Color.LightGray)
            .verticalScroll(rememberScrollState())
            .padding(32.dp)
    ) {
        Column {
            repeat(6) {
                Box(
                    modifier = Modifier
                        .height(128.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        "Scroll here",
                        modifier = Modifier
                            .border(12.dp, Color.DarkGray)
                            .background(brush = gradient)
                            .padding(24.dp)
                            .height(150.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollapseToolbar() {
    val AppBarHeight = 56.dp
    val Purple40 = Color(0xFF6650a4)

    val appBarMaxHeightPx = with(LocalDensity.current) { AppBarHeight.roundToPx() }
    val connection = remember(appBarMaxHeightPx) {
        CollapsingAppBarNestedScrollConnection(appBarMaxHeightPx)
    }
    val density = LocalDensity.current
    val spaceHeight by remember(density) {
        derivedStateOf {
            with(density) {
                (appBarMaxHeightPx + connection.appBarOffset).toDp()
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(Modifier.nestedScroll(connection)) {
            Column {
                Spacer(
                    Modifier
                        .padding(4.dp)
                        .height(spaceHeight)
                )
                LazyColumn(contentPadding = PaddingValues(top = AppBarHeight)) {
                    items(mockListData) { item ->
                        ListItem(
                            headlineContent = { Text(item.title) },
                            supportingContent = { Text(item.description) },
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }

            TopAppBar(
                modifier = Modifier.offset { IntOffset(0, connection.appBarOffset) },
                title = { Text(text = "Jetpack Compose") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Purple40,
                    titleContentColor = Color.White
                )
            )
        }
    }
}

private class CollapsingAppBarNestedScrollConnection(
    val appBarMaxHeight: Int
) : NestedScrollConnection {

    var appBarOffset: Int by mutableIntStateOf(0)
        private set

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val delta = available.y.toInt()
        val newOffset = appBarOffset + delta
        val previousOffset = appBarOffset
        appBarOffset = newOffset.coerceIn(-appBarMaxHeight, 0)
        val consumed = appBarOffset - previousOffset
        return Offset(0f, consumed.toFloat())
    }
}

@Preview
@Composable
private fun preview() {
    CollapseToolbar()
}