package ua.obrio.common.presentation.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import ua.obrio.common.domain.model.TransactionModel
import ua.obrio.common.presentation.ui.resources.LocalResources
import ua.obrio.common.presentation.util.formatTransactionBTC

@Composable
fun TransactionItem(transaction: TransactionModel) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(LocalResources.Dimensions.Padding.Small)
            .background(LocalResources.Colors.LightGray)
            .padding(LocalResources.Dimensions.Padding.Small),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = transaction.category?.nameFormatted ?: "",
                fontSize = LocalResources.Dimensions.Text.SizeMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = transaction.dateTimeFormatted,
                fontSize = LocalResources.Dimensions.Text.SizeSmall,
                color = LocalResources.Colors.Gray
            )
        }
        Text(
            text = formatTransactionBTC(transaction.amountBTC),
            fontSize = LocalResources.Dimensions.Text.SizeMedium,
            color = if (transaction.amountBTC > 0) Color.Green else Color.Red,
            fontWeight = FontWeight.Bold
        )
    }
}