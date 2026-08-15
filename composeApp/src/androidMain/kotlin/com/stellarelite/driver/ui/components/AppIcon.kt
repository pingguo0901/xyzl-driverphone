package com.stellarelite.driver.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.stellarelite.driver.R

@Composable
actual fun AppIcon(
    modifier: Modifier,
    size: Int
) {
    Image(
        painter = painterResource(id = R.drawable.app_icon),
        contentDescription = "星域司导",
        modifier = modifier
            .size(size.dp)
            .clip(RoundedCornerShape((size * 0.22).dp)),
        contentScale = ContentScale.Crop
    )
}
