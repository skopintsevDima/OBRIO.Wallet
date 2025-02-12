package ua.obrio.feature.account.presentation.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import ua.obrio.common.domain.model.TransactionModel
import ua.obrio.common.presentation.ui.composable.ErrorSnackBar
import ua.obrio.common.presentation.ui.composable.LoadingScreen
import ua.obrio.common.presentation.ui.composable.TransactionsList
import ua.obrio.common.presentation.ui.composable.showSnackBarSafe
import ua.obrio.common.presentation.ui.resources.LocalResources
import ua.obrio.common.presentation.ui.theme.OBRIOWalletTheme
import ua.obrio.common.presentation.ui.util.PreviewLightDarkBothOrientations
import ua.obrio.common.presentation.util.Constants.UI.Account
import ua.obrio.common.presentation.util.Constants.UI.AddTransaction
import ua.obrio.common.presentation.util.formatBalanceBTC
import ua.obrio.feature.account.presentation.ui.screen.composable.DepositDialog
import ua.obrio.feature.account.presentation.ui.screen.mock.DataStatePreviewProvider
import ua.obrio.feature.account.presentation.ui.screen.mock.ErrorStatePreviewProvider
import ua.obrio.feature.account.presentation.ui.screen.mock.MockAccountViewModelWithState
import ua.obrio.feature.account.presentation.ui.screen.mock.MockUiState

@Composable
fun AccountScreen(
    navController: NavController,
    viewModel: AccountViewModel = hiltViewModel<AccountViewModelImpl>()
) {
    val uiState = viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(snackbarHost = { ErrorSnackBar(snackbarHostState) }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val stateValue = uiState.value) {
                UiState.Loading -> LoadingScreen()
                is UiState.Error -> ErrorScreen(stateValue, viewModel, snackbarHostState)
                is UiState.Data -> DataScreen(
                    stateValue,
                    viewModel,
                    snackbarHostState,
                    navController
                )
            }
        }
    }
}

@Composable
private fun ErrorScreen(
    error: UiState.Error,
    viewModel: AccountViewModel,
    snackbarHostState: SnackbarHostState
) {
    val errorMessage = rememberSaveable(error) { error.errorMsg }
    val actionLabel = stringResource(LocalResources.Strings.Okay)

    LaunchedEffect(errorMessage) {
        snackbarHostState.showSnackBarSafe(
            message = errorMessage,
            actionLabel = actionLabel,
            loggingTag = Account.TAG
        )
    }

    // TODO: Handle different errors
//    when (error) {
//        is UiState.Error.LoadBookDataError,
//        is UiState.Error.NoDataForPlayerError -> {
//            val onRetryClick = remember(viewModel) { { viewModel.tryHandleIntent(UiIntent.FetchBookSummary) } }
//            DataScreenPlaceholder(onRetryClick)
//        }
//
//        is UiState.Error.PlaybackError,
//        is UiState.Error.PlayerInitError,
//        is UiState.Error.UnknownError -> {
//            MessageScreen(message = stringResource(LocalResources.Strings.UnknownErrorMessage))
//        }
//    }
}

@Composable
private fun DataScreenPlaceholder(onRetryClick: () -> Unit) {

}

@Composable
private fun DataScreen(
    data: UiState.Data,
    viewModel: AccountViewModel,
    snackbarHostState: SnackbarHostState,
    navController: NavController
) {
    val nonCriticalErrorMsg = remember(data.nonCriticalError) { data.nonCriticalError?.errorMsg }
    val actionLabel = stringResource(LocalResources.Strings.Okay)

    LaunchedEffect(data.nonCriticalError) {
        if (!nonCriticalErrorMsg.isNullOrEmpty()) {
            snackbarHostState.showSnackBarSafe(
                message = nonCriticalErrorMsg,
                actionLabel = actionLabel,
                loggingTag = Account.TAG
            )
        }
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val onDepositConfirmed = remember(viewModel) {
        { depositAmountBTC: Double ->
            viewModel.tryHandleIntent(UiIntent.Deposit(depositAmountBTC))
        }
    }
    val onAddTransactionClick = remember(viewModel) {
        { navController.navigate(AddTransaction.ROUTE_ID) }
    }
    val userTransactions = data.userTransactions.collectAsLazyPagingItems()
    if (isLandscape) {
        DataLandscapeScreen(data, userTransactions, onDepositConfirmed, onAddTransactionClick)
    } else {
        DataPortraitScreen(data, userTransactions, onDepositConfirmed, onAddTransactionClick)
    }
}

@Composable
fun DataPortraitScreen(
    data: UiState.Data,
    userTransactions: LazyPagingItems<TransactionModel>,
    onDepositConfirmed: (Double) -> Unit,
    onAddTransactionClick: () -> Unit
) {
    val balanceBTC = remember(data) { data.userBalanceBTC }
    val btcPriceUSD = remember(data) { data.bitcoinExchangeRateUSD }
    var showDepositDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(LocalResources.Dimensions.Padding.Medium)) {
        Text(
            text = stringResource(LocalResources.Strings.PriceBTCinUSD, btcPriceUSD),
            fontSize = LocalResources.Dimensions.Text.SizeSmall,
            color = LocalResources.Colors.Gray,
            modifier = Modifier.align(Alignment.End)
        )
        Spacer(modifier = Modifier.height(LocalResources.Dimensions.Padding.Medium))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val formattedBalanceBTC = formatBalanceBTC(balanceBTC)
            Text(
                text = stringResource(LocalResources.Strings.BalanceInBTC, formattedBalanceBTC),
                fontSize = LocalResources.Dimensions.Text.SizeLarge,
                fontWeight = FontWeight.Bold
            )
            Button(onClick = { showDepositDialog = true }) {
                Text(stringResource(LocalResources.Strings.Deposit))
            }
        }

        Spacer(modifier = Modifier.height(LocalResources.Dimensions.Padding.Medium))
        Text(
            text = stringResource(LocalResources.Strings.Transactions),
            fontSize = LocalResources.Dimensions.Text.SizeMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(LocalResources.Dimensions.Padding.Small))

        TransactionsList(
            modifier = Modifier.weight(
                LocalResources.Dimensions.Size.FillWidth.FULL
            ),
            userTransactions
        )

        Button(
            onClick = onAddTransactionClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = LocalResources.Dimensions.Padding.Small)
        ) {
            Text(stringResource(LocalResources.Strings.AddTransaction))
        }
    }

    if (showDepositDialog) {
        DepositDialog(
            onDismiss = { showDepositDialog = false },
            onConfirm = onDepositConfirmed
        )
    }
}

@Composable
private fun DataLandscapeScreen(
    data: UiState.Data,
    userTransactions: LazyPagingItems<TransactionModel>,
    onDepositConfirmed: (Double) -> Unit,
    onAddTransactionClick: () -> Unit
) {
    val balanceBTC = remember(data) { data.userBalanceBTC }
    val btcPriceUSD = remember(data) { data.bitcoinExchangeRateUSD }
    var showDepositDialog by remember { mutableStateOf(false) }

    Row(modifier = Modifier
        .fillMaxSize()
        .padding(LocalResources.Dimensions.Padding.Medium)) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = LocalResources.Dimensions.Padding.Medium)
        ) {
            Text(
                text = stringResource(LocalResources.Strings.PriceBTCinUSD, btcPriceUSD),
                fontSize = LocalResources.Dimensions.Text.SizeSmall,
                color = LocalResources.Colors.Gray,
                modifier = Modifier.align(Alignment.End)
            )
            Spacer(modifier = Modifier.height(LocalResources.Dimensions.Padding.Medium))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val formattedBalanceBTC = formatBalanceBTC(balanceBTC)
                Text(
                    text = stringResource(LocalResources.Strings.BalanceInBTC, formattedBalanceBTC),
                    fontSize = LocalResources.Dimensions.Text.SizeLarge,
                    fontWeight = FontWeight.Bold
                )
                Button(onClick = { showDepositDialog = true }) {
                    Text(stringResource(LocalResources.Strings.Deposit))
                }
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(LocalResources.Strings.Transactions),
                fontSize = LocalResources.Dimensions.Text.SizeMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(LocalResources.Dimensions.Padding.Small))

            TransactionsList(
                modifier = Modifier.weight(
                    LocalResources.Dimensions.Size.FillWidth.FULL
                ),
                userTransactions
            )

            Button(
                onClick = onAddTransactionClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = LocalResources.Dimensions.Padding.Small)
            ) {
                Text(stringResource(LocalResources.Strings.AddTransaction))
            }
        }
    }

    if (showDepositDialog) {
        DepositDialog(
            onDismiss = { showDepositDialog = false },
            onConfirm = onDepositConfirmed
        )
    }
}

@PreviewLightDarkBothOrientations
@Composable
private fun LoadingPreview() {
    AccountScreenPreviewWrapper(MockUiState.Loading)
}

@PreviewLightDarkBothOrientations
@Composable
private fun ErrorPreview(
    @PreviewParameter(ErrorStatePreviewProvider::class) error: UiState.Error
) {
    AccountScreenPreviewWrapper(error)
}

@PreviewLightDarkBothOrientations
@Composable
private fun DataPreview(
    @PreviewParameter(DataStatePreviewProvider::class) data: UiState.Data
) {
    AccountScreenPreviewWrapper(data)
}

@Composable
private fun AccountScreenPreviewWrapper(
    uiState: UiState
) {
    val navController = rememberNavController()

    OBRIOWalletTheme {
        AccountScreen(
            navController = navController,
            viewModel = MockAccountViewModelWithState(uiState)
        )
    }
}