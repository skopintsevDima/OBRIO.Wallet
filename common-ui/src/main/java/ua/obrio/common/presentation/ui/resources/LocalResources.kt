package ua.obrio.common.presentation.ui.resources

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.obrio.common.ui.R

object LocalResources {
    object Icons {
        val Refresh = R.drawable.ic_refresh
    }

    object Strings {
        val Retry = R.string.retry
        val Okay = R.string.okay
        val Cancel = R.string.cancel
        val Transactions = R.string.transactions
        val Deposit = R.string.deposit
        val AddTransaction = R.string.add_transaction
        val PriceBTCinUSD = R.string.btc_price_in_usd
        val BalanceInBTC = R.string.balance_in_btc
        val EnterAmount = R.string.enter_amount
        val SelectCategory = R.string.select_category
        val Add = R.string.add
        val InvalidAmount = R.string.invalid_amount
        val DepositSuccessful = R.string.deposit_successful

        val ErrorLoadUserAccount = R.string.error_load_user_account
        val ErrorDepositFailed = R.string.error_deposit_failed
        val ErrorNoData = R.string.error_no_data
        val ErrorIncorrectAmountForTransaction = R.string.error_incorrect_amount_for_transaction
        val ErrorInsufficientBalance = R.string.error_insufficient_balance
        val ErrorMissingCategoryForTransaction = R.string.error_missing_category_for_transaction
        val ErrorAddingTransactionFailed = R.string.error_adding_transaction_failed
        val ErrorUnknown = R.string.error_unknown

        val UnknownErrorMessage = R.string.unknown_error_message_to_user
        val GlobalCrashMessage = R.string.global_crash_message_to_user
        val NoTransactionsMessage = R.string.no_transactions_message
        val LoadingTransactionsFailedMessage = R.string.loading_transactions_failed_message
        val NoDataErrorMessage = R.string.no_data_message
    }

    object Colors {
        @Stable
        val Gray = Color.Gray
        @Stable
        val Red = Color.Red
    }

    object TextLabel {
        val TransactionAmountWidthMax = 200.dp
    }

    object Dimensions {
        object Padding {
            val Tiny = 4.dp
            val Small = 8.dp
            val Medium = 16.dp
            val XXXLarge = 150.dp
        }

        object Icon {
            val Small = 32.dp
            val ExtraLarge = 96.dp
        }

        object Text {
            val SizeLarge = 24.sp
            val SizeMedium = 16.sp
            val SizeSmall = 14.sp
            val SizeTiny = 12.sp
        }

        object Size {
            val ProgressIndicatorSmall = 32.dp
            val ProgressIndicatorBig = 96.dp

            object FillWidth {
                const val FULL = 1f
            }
        }

        object Corners {
            val Medium = 12.dp
        }

        object Elevation {
            val Small = 2.dp
        }
    }
}