package ua.obrio.common.presentation.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
    LazyColumn(
        modifier = modifier,
        state = rememberForeverLazyListState(key = "transactions" + transactions[0]?.id)
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

private val SaveMap = mutableMapOf<String, KeyParams>()

private data class KeyParams(
    val params: String = "",
    val index: Int,
    val scrollOffset: Int
)

@Composable
private fun rememberForeverLazyListState(
    key: String,
    params: String = "",
    initialFirstVisibleItemIndex: Int = 0,
    initialFirstVisibleItemScrollOffset: Int = 0
): LazyListState {
    val scrollState = remember {
        var savedValue = SaveMap[key]
        if (savedValue?.params != params) savedValue = null
        val savedIndex = savedValue?.index ?: initialFirstVisibleItemIndex
        val savedOffset = savedValue?.scrollOffset ?: initialFirstVisibleItemScrollOffset
        LazyListState(
            savedIndex,
            savedOffset
        )
    }
    DisposableEffect(scrollState) {
        onDispose {
            val lastIndex = scrollState.firstVisibleItemIndex
            val lastOffset = scrollState.firstVisibleItemScrollOffset
            SaveMap[key] = KeyParams(params, lastIndex, lastOffset)
        }
    }
    return scrollState
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(LocalResources.Dimensions.Padding.Small),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(LocalResources.Dimensions.Corners.Medium),
        elevation = CardDefaults.elevatedCardElevation(LocalResources.Dimensions.Elevation.Small)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(LocalResources.Dimensions.Padding.Medium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.category?.nameFormatted
                        ?: stringResource(LocalResources.Strings.Deposit).lowercase(),
                    fontSize = LocalResources.Dimensions.Text.SizeMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(LocalResources.Dimensions.Padding.Tiny))
                Text(
                    text = transaction.dateTimeFormatted,
                    fontSize = LocalResources.Dimensions.Text.SizeSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                modifier = Modifier
                    .widthIn(max = LocalResources.TextLabel.TransactionAmountWidthMax)
                    .padding(start = LocalResources.Dimensions.Padding.Small),
                text = formatTransactionBTC(transaction.amountBTC),
                fontSize = LocalResources.Dimensions.Text.SizeMedium,
                fontWeight = FontWeight.Bold,
                color = if (transaction.amountBTC > 0) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.End
            )
        }
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