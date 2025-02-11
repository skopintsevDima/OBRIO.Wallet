package ua.obrio.feature.add_transaction.presentation.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ua.obrio.common.domain.model.TransactionModel
import ua.obrio.common.presentation.ui.composable.ErrorSnackBar
import ua.obrio.common.presentation.ui.composable.LoadingScreen
import ua.obrio.common.presentation.ui.composable.showSnackBarSafe
import ua.obrio.common.presentation.ui.resources.LocalResources
import ua.obrio.common.presentation.ui.theme.OBRIOWalletTheme
import ua.obrio.common.presentation.ui.util.PreviewLightDarkBothOrientations
import ua.obrio.common.presentation.util.Constants.UI.AddTransaction
import ua.obrio.feature.add_transaction.presentation.ui.screen.mock.DataStatePreviewProvider
import ua.obrio.feature.add_transaction.presentation.ui.screen.mock.ErrorStatePreviewProvider
import ua.obrio.feature.add_transaction.presentation.ui.screen.mock.MockAddTransactionViewModelWithState
import ua.obrio.feature.add_transaction.presentation.ui.screen.mock.MockUiState

@Composable
fun AddTransactionScreen(
    navController: NavController,
    viewModel: AddTransactionViewModel = hiltViewModel<AddTransactionViewModelImpl>()
) {
    val uiState = viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.value) {
        if (uiState.value is UiState.Finish) {
            navController.popBackStack()
        }
    }

    Scaffold(snackbarHost = { ErrorSnackBar(snackbarHostState) }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val stateValue = uiState.value) {
                UiState.Loading -> LoadingScreen()
                is UiState.Error -> ErrorScreen(stateValue, viewModel, snackbarHostState)
                is UiState.Data -> DataScreen(stateValue, viewModel, snackbarHostState)
                UiState.Finish -> { /*Do nothing*/ }
            }
        }
    }
}

@Composable
private fun ErrorScreen(
    error: UiState.Error,
    viewModel: AddTransactionViewModel,
    snackbarHostState: SnackbarHostState
) {
    val errorMessage = rememberSaveable(error) { error.errorMsg }
    val actionLabel = stringResource(LocalResources.Strings.Okay)

    LaunchedEffect(errorMessage) {
        snackbarHostState.showSnackBarSafe(
            message = errorMessage,
            actionLabel = actionLabel,
            loggingTag = AddTransaction.TAG
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
    viewModel: AddTransactionViewModel,
    snackbarHostState: SnackbarHostState
) {
    val nonCriticalErrorMsg = remember(data.nonCriticalError) { data.nonCriticalError?.errorMsg }
    val actionLabel = stringResource(LocalResources.Strings.Okay)

    LaunchedEffect(data.nonCriticalError) {
        if (!nonCriticalErrorMsg.isNullOrEmpty()) {
            snackbarHostState.showSnackBarSafe(
                message = nonCriticalErrorMsg,
                actionLabel = actionLabel,
                loggingTag = AddTransaction.TAG
            )
        }
    }

    val strEnteredAmountBTC = remember(data) { data.strEnteredAmountBTC }
    val selectedCategory = remember(data) { data.selectedCategory }
    val categories = TransactionModel.Category.entries.map { it.nameFormatted }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(LocalResources.Dimensions.Padding.Medium)
    ) {
        TextField(
            value = strEnteredAmountBTC,
            onValueChange = {
                viewModel.tryHandleIntent(UiIntent.UpdateEnteredAmount(it))
            },
            label = { Text(stringResource(LocalResources.Strings.EnterAmount)) },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                autoCorrectEnabled = false
            ),
            modifier = Modifier.fillMaxWidth()
        )


        Spacer(modifier = Modifier.height(LocalResources.Dimensions.Padding.Medium))

        Column(
            modifier = Modifier
                .weight(LocalResources.Dimensions.Size.FillWidth.FULL)
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(LocalResources.Strings.SelectCategory),
                fontWeight = FontWeight.SemiBold
            )

            LazyColumn(modifier = Modifier.weight(LocalResources.Dimensions.Size.FillWidth.FULL)) {
                val onCategoryClick = { categoryName: String ->
                    val categoryClicked = TransactionModel.Category.of(categoryName)
                    viewModel.tryHandleIntent(UiIntent.UpdateSelectedCategory(categoryClicked))
                }
                itemsIndexed(categories) { _, categoryName ->
                    TransactionCategory(
                        selectedCategoryName = selectedCategory?.nameFormatted,
                        categoryName = categoryName,
                        onCategoryClick = onCategoryClick
                    )
                }
            }
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = LocalResources.Dimensions.Padding.Medium)
                .padding(horizontal = LocalResources.Dimensions.Padding.XXLarge),
            onClick = {
                viewModel.tryHandleIntent(UiIntent.AddTransaction)
            }
        ) {
            Text(stringResource(LocalResources.Strings.Add))
        }
    }
}

@Composable
private fun TransactionCategory(
    selectedCategoryName: String?,
    categoryName: String,
    onCategoryClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCategoryClick(categoryName) }
            .padding(LocalResources.Dimensions.Padding.Small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selectedCategoryName == categoryName,
            onClick = { onCategoryClick(categoryName) }
        )
        Text(
            text = categoryName,
            modifier = Modifier.padding(start = LocalResources.Dimensions.Padding.Small)
        )
    }
}

@PreviewLightDarkBothOrientations
@Composable
private fun LoadingPreview() {
    AddTransactionScreenPreviewWrapper(MockUiState.Loading)
}

@PreviewLightDarkBothOrientations
@Composable
private fun ErrorPreview(
    @PreviewParameter(ErrorStatePreviewProvider::class) error: UiState.Error
) {
    AddTransactionScreenPreviewWrapper(error)
}

@PreviewLightDarkBothOrientations
@Composable
private fun DataPreview(
    @PreviewParameter(DataStatePreviewProvider::class) data: UiState.Data
) {
    AddTransactionScreenPreviewWrapper(data)
}

@Composable
private fun AddTransactionScreenPreviewWrapper(
    uiState: UiState
) {
    val navController = rememberNavController()

    OBRIOWalletTheme {
        AddTransactionScreen(
            navController = navController,
            viewModel = MockAddTransactionViewModelWithState(uiState)
        )
    }
}