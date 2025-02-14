package ua.obrio.common.presentation.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import ua.obrio.common.presentation.ui.resources.LocalResources

@Composable
fun DataScreenPlaceholder(onRetryClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = onRetryClick,
                modifier = Modifier
                    .size(LocalResources.Dimensions.Icon.ExtraLarge)
            ) {
                Icon(
                    modifier = Modifier.size(LocalResources.Dimensions.Icon.ExtraLarge),
                    imageVector = ImageVector.vectorResource(LocalResources.Icons.Refresh),
                    contentDescription = stringResource(id = LocalResources.Strings.Retry),
                    tint = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(LocalResources.Dimensions.Padding.Small))

            Text(
                text = stringResource(id = LocalResources.Strings.Retry),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = LocalResources.Dimensions.Text.SizeLarge
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}