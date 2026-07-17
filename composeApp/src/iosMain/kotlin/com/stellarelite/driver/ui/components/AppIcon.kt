package com.stellarelite.driver.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.InternalResourceApi

@OptIn(InternalResourceApi::class)
@Composable
actual fun AppIcon(
    modifier: Modifier,
    size: Int
) {
    Image(
        painter = androidx.compose.ui.res.painterResource(
            DrawableResource(
                "drawable:app_icon",
                setOf(
                    org.jetbrains.compose.resources.ResourceItem(
                        emptySet(),
                        "composeResources/driverappphone.composeapp.generated.resources/drawable/app_icon.jpg",
                        -1, -1
                    )
                )
            )
        ),
        contentDescription = "星域臻旅",
        modifier = modifier
            .size(size.dp)
            .clip(RoundedCornerShape((size * 0.22).dp)),
        contentScale = ContentScale.Crop
    )
}
