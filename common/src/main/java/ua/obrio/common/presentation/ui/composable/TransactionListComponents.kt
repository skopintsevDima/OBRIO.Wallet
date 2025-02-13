package ua.obrio.common.presentation.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import ua.obrio.common.domain.model.TransactionModel
import ua.obrio.common.presentation.ui.resources.LocalResources
import ua.obrio.common.presentation.util.formatTransactionBTC

@Composable
fun TransactionsList(
    modifier: Modifier,
    transactions: LazyPagingItems<TransactionModel>
) {
    when {
        transactions.loadState.refresh is LoadState.Loading -> {
            TransactionsLoader(modifier)
        }
        transactions.loadState.refresh is LoadState.Error -> {
            ErrorTransactionsPlaceholder(modifier) { transactions.retry() }
        }
        transactions.itemCount == 0 && transactions.loadState.refresh is LoadState.NotLoading -> {
            EmptyTransactionsPlaceholder(modifier)
        }
        else -> {
            TransactionsLazyList(modifier, transactions)
        }
    }
}

@Composable
fun TransactionsLazyList(
    modifier: Modifier,
    transactions: LazyPagingItems<TransactionModel>
) {
    val listState = rememberLazyListState()

    LazyColumn(
        modifier = modifier,
        state = listState
    ) {
        items(
            count = transactions.itemCount,
            key = { index -> transactions[index]?.id ?: index }
        ) { index ->
            transactions[index]?.let { TransactionItem(it) }
        }

        when (transactions.loadState.append) {
            is LoadState.Loading -> item { TransactionsLoader() }
            is LoadState.Error -> item { TransactionsRetryButton { transactions.retry() } }
            is LoadState.NotLoading -> {} // Do nothing
        }
    }
}

@Composable
fun EmptyTransactionsPlaceholder(modifier: Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(LocalResources.Strings.NoTransactionsMessage),
            fontSize = LocalResources.Dimensions.Text.SizeMedium,
            color = LocalResources.Colors.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ErrorTransactionsPlaceholder(
    modifier: Modifier,
    onRetry: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(LocalResources.Strings.LoadingTransactionsFailedMessage),
            fontSize = LocalResources.Dimensions.Text.SizeMedium,
            color = LocalResources.Colors.Gray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(LocalResources.Dimensions.Padding.Medium))
        Button(
            onClick = onRetry,
            modifier = Modifier
                .padding(LocalResources.Dimensions.Padding.Medium)
        ) {
            Text(stringResource(LocalResources.Strings.Retry))
        }
    }
}

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

@Composable
fun TransactionsLoader(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(
                LocalResources.Dimensions.Size.ProgressIndicatorSmall
            )
        )
    }
}

@Composable
fun TransactionsRetryButton(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(LocalResources.Dimensions.Icon.Small)
        ) {
            Icon(
                modifier = Modifier.size(LocalResources.Dimensions.Icon.ExtraLarge),
                imageVector = ImageVector.vectorResource(LocalResources.Icons.Refresh),
                contentDescription = stringResource(id = LocalResources.Strings.Retry),
                tint = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
