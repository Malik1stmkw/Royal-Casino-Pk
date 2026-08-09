package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.CasinoDarkBackground

@Composable
fun FrostedGlassBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CasinoDarkBackground)
    ) {
        // Ambient Radial Blur Orbs
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Indigo Orb at Top Left
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x596366F1), // Indigo 600 with opacity
                        Color(0x1F4F46E5),
                        Color.Transparent
                    ),
                    center = Offset(width * 0.15f, height * 0.1f),
                    radius = width * 0.65f
                ),
                center = Offset(width * 0.15f, height * 0.1f),
                radius = width * 0.65f
            )

            // Emerald Orb at Bottom Right
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x4010B981), // Emerald 500 with opacity
                        Color(0x15059669),
                        Color.Transparent
                    ),
                    center = Offset(width * 0.85f, height * 0.85f),
                    radius = width * 0.65f
                ),
                center = Offset(width * 0.85f, height * 0.85f),
                radius = width * 0.65f
            )

            // Purple Subtle Glow at Center Left
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x2B8B5CF6),
                        Color.Transparent
                    ),
                    center = Offset(width * 0.9f, height * 0.35f),
                    radius = width * 0.45f
                ),
                center = Offset(width * 0.9f, height * 0.35f),
                radius = width * 0.45f
            )
        }

        content()
    }
}
