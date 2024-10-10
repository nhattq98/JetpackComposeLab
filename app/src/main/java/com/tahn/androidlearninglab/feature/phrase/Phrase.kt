package com.tahn.androidlearninglab.feature.phrase

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.tahn.androidlearninglab.R


@Composable
fun PhraseExample() {
    Box {
        var imageHeightPx by remember { mutableStateOf(0) }
        Log.d("debugLog", "PhraseExampleEntry imageHeightPx: ${imageHeightPx}")


        Image(
            painter = painterResource(R.drawable.ic_launcher_background),
            contentDescription = "I'm above the text",
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { size ->
                    // Don't do this
                    Log.d("debugLog", "onSizeChanged")
                    imageHeightPx = size.height
                }
        )

        Text(
            text = "I'm below the image",
            modifier = Modifier.padding(top = with(LocalDensity.current) { imageHeightPx.toDp() })
        )
    }
}

@Preview
@Composable
private fun preview() {
    PhraseExample()
}