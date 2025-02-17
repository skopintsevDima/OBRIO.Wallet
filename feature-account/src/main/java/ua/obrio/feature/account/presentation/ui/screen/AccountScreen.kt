package ua.obrio.feature.account.presentation.ui.screen

import android.content.res.Configuration
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import ua.obrio.common.domain.model.TransactionModel
import ua.obrio.common.presentation.ui.composable.DataScreenPlaceholder
import ua.obrio.common.presentation.ui.composable.ErrorSnackBar
import ua.obrio.common.presentation.ui.composable.LoadingScreen
import ua.obrio.common.presentation.ui.composable.MessageScreen
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

    LaunchedEffect(viewModel) {
        viewModel.tryHandleIntent(UiIntent.LoadUserAccount)
    }

    val context = LocalContext.current
    LaunchedEffect(viewModel.uiEvents) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                UiEvent.DepositSucceeded -> {
                    Toast.makeText(
                        context,
                        LocalResources.Strings.DepositSuccessful,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    Scaffold(snackbarHost = { ErrorSnackBar(snackbarHostState) }) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
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

    LaunchedEffect(error) {
        snackbarHostState.showSnackBarSafe(
            message = errorMessage,
            actionLabel = actionLabel,
            loggingTag = Account.TAG
        )
    }

    when (error) {
        is UiState.Error.LoadUserAccountError -> {
            val onRetryClick = remember(viewModel) {
                { viewModel.tryHandleIntent(UiIntent.LoadUserAccount) }
            }
            DataScreenPlaceholder(onRetryClick)
        }
        is UiState.Error.UnknownError -> {
            MessageScreen(message = stringResource(LocalResources.Strings.UnknownErrorMessage))
        }
    }
}

@Composable
private fun DataScreen(
    data: UiState.Data,
    viewModel: AccountViewModel,
    snackbarHostState: SnackbarHostState,
    navController: NavController
) {
    val nonCriticalErrorMsg = remember(data.nonCriticalError) { data.nonCriticalError?.errorMsg }
    val isNonCriticalErrorShown = rememberSaveable(data.nonCriticalError) { mutableStateOf(false) }
    val actionLabel = stringResource(LocalResources.Strings.Okay)

    LaunchedEffect(data.nonCriticalError) {
        if (!nonCriticalErrorMsg.isNullOrEmpty() && !isNonCriticalErrorShown.value) {
            snackbarHostState.showSnackBarSafe(
                message = nonCriticalErrorMsg,
                actionLabel = actionLabel,
                loggingTag = AddTransaction.TAG
            )
            isNonCriticalErrorShown.value = true
        }
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val balanceBTC = remember(data) { data.userBalanceBTC }
    val btcPriceUSD = remember(data) { data.bitcoinExchangeRateUSD }
    val depositEnteredAmountStr = remember(data) { data.depositEnteredAmountStr ?: "" }
    var showDepositDialog by remember { mutableStateOf(data.depositEnteredAmountStr != null) }
    val userTransactions = data.userTransactions.collectAsLazyPagingItems()
    val onDepositClicked = { showDepositDialog = true }
    val onDepositDismissed = { showDepositDialog = false }
    val onDepositConfirmed = remember(viewModel) {
        { depositAmountBTC: Double ->
            viewModel.tryHandleIntent(UiIntent.Deposit(depositAmountBTC))
            showDepositDialog = false
        }
    }
    val onDepositDisposed = remember(viewModel) {
        { depositEnteredAmountStr: String? ->
            viewModel.tryHandleIntent(UiIntent.SaveDepositEnteredAmount(depositEnteredAmountStr))
        }
    }
    val onAddTransactionClick = remember(viewModel) {
        { navController.navigate(AddTransaction.ROUTE_ID) }
    }
    if (isLandscape) {
        DataLandscapeScreen(
            balanceBTC,
            btcPriceUSD,
            showDepositDialog,
            depositEnteredAmountStr,
            userTransactions,
            onDepositClicked,
            onDepositDismissed,
            onDepositConfirmed,
            onDepositDisposed,
            onAddTransactionClick
        )
    } else {
        DataPortraitScreen(
            balanceBTC,
            btcPriceUSD,
            showDepositDialog,
            depositEnteredAmountStr,
            userTransactions,
            onDepositClicked,
            onDepositDismissed,
            onDepositConfirmed,
            onDepositDisposed,
            onAddTransactionClick
        )
    }
}

@Composable
private fun DataPortraitScreen(
    balanceBTC: Double,
    btcPriceUSD: Float,
    showDepositDialog: Boolean,
    depositEnteredAmountStr: String,
    userTransactions: LazyPagingItems<TransactionModel>,
    onDepositClicked: () -> Unit,
    onDepositDismissed: () -> Unit,
    onDepositConfirmed: (Double) -> Unit,
    onDepositDisposed: (String?) -> Unit,
    onAddTransactionClick: () -> Unit
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(LocalResources.Dimensions.Padding.Medium)
    ) {
        BitcoinPriceLabel(btcPriceUSD, modifier = Modifier.align(Alignment.End))
        Spacer(modifier = Modifier.height(LocalResources.Dimensions.Padding.Medium))

        DataScreenHeader(balanceBTC, onDepositClicked)
        Spacer(modifier = Modifier.height(LocalResources.Dimensions.Padding.Medium))

        TransactionsList(
            modifier = Modifier.weight(LocalResources.Dimensions.Size.FillWidth.FULL),
            userTransactions
        )

        AddTransactionButton(onAddTransactionClick)
    }

    if (showDepositDialog) {
        DepositDialog(
            depositEnteredAmountStr,
            onDismiss = onDepositDismissed,
            onConfirm = onDepositConfirmed,
            onDispose = onDepositDisposed
        )
    }
}

@Composable
private fun DataLandscapeScreen(
    balanceBTC: Double,
    btcPriceUSD: Float,
    showDepositDialog: Boolean,
    depositEnteredAmountStr: String,
    userTransactions: LazyPagingItems<TransactionModel>,
    onDepositClicked: () -> Unit,
    onDepositDismissed: () -> Unit,
    onDepositConfirmed: (Double) -> Unit,
    onDepositDisposed: (String?) -> Unit,
    onAddTransactionClick: () -> Unit
) {
    Row(modifier = Modifier
        .fillMaxSize()
        .padding(LocalResources.Dimensions.Padding.Medium)
        .padding(bottom = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(LocalResources.Dimensions.Size.FillWidth.FULL)
                .padding(end = LocalResources.Dimensions.Padding.Medium)
        ) {
            BitcoinPriceLabel(btcPriceUSD, modifier = Modifier.align(Alignment.End))
            Spacer(modifier = Modifier.height(LocalResources.Dimensions.Padding.Medium))

            DataScreenHeader(balanceBTC, onDepositClicked)
        }

        Column(modifier = Modifier.weight(LocalResources.Dimensions.Size.FillWidth.FULL)) {
            TransactionsList(
                modifier = Modifier.weight(LocalResources.Dimensions.Size.FillWidth.FULL),
                userTransactions
            )

            AddTransactionButton(onAddTransactionClick)
        }
    }

    if (showDepositDialog) {
        DepositDialog(
            depositEnteredAmountStr,
            onDismiss = onDepositDismissed,
            onConfirm = onDepositConfirmed,
            onDispose = onDepositDisposed
        )
    }
}

@Composable
private fun BitcoinPriceLabel(btcPriceUSD: Float, modifier: Modifier) {
    Text(
        text = stringResource(LocalResources.Strings.PriceBTCinUSD, btcPriceUSD),
        fontSize = LocalResources.Dimensions.Text.SizeSmall,
        color = LocalResources.Colors.Gray,
        modifier = modifier
    )
}

@Composable
private fun DataScreenHeader(balanceBTC: Double, onDepositClicked: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val formattedBalanceBTC = formatBalanceBTC(balanceBTC)
        Text(
            modifier = Modifier.weight(LocalResources.Dimensions.Size.FillWidth.FULL),
            text = stringResource(LocalResources.Strings.BalanceInBTC, formattedBalanceBTC),
            fontSize = LocalResources.Dimensions.Text.SizeLarge,
            fontWeight = FontWeight.Bold
        )
        Button(onClick = onDepositClicked) {
            Text(stringResource(LocalResources.Strings.Deposit))
        }
    }
}

@Composable
private fun AddTransactionButton(onAddTransactionClick: () -> Unit) {
    Button(
        onClick = onAddTransactionClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = LocalResources.Dimensions.Padding.Small)
    ) {
        Text(stringResource(LocalResources.Strings.AddTransaction))
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