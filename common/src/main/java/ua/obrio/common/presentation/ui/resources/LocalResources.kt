package ua.obrio.common.presentation.ui.resources

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.obrio.common.R

// TODO: Remove unused, if needed
object LocalResources {
    object Icons {
        val Refresh = R.drawable.ic_refresh
    }

    object Images {
//        val PlaceholderImage = R.drawable.placeholder_image
    }

    object Strings {
        val IdleMessage = R.string.idle_message
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
        val Black = Color.Black
        @Stable
        val White = Color.White
        @Stable
        val Gray = Color.Gray
        @Stable
        val LightGray = Color.LightGray
    }

    object Dimensions {
        object Padding {
            val ExtraSmall = 4.dp
            val Small = 8.dp
            val Medium = 16.dp
            val Large = 24.dp
            val XLarge = 32.dp
            val XXLarge = 40.dp
            val XXXLarge = 48.dp
        }

//        object Image {
//            val Height = 320.dp
//            val Width = 220.dp
//            val CornerRadius = 8.dp
//        }
//
//        object Button {
//            val HeightSmall = 40.dp
//            val WidthSmall = 100.dp
//        }

        object Icon {
            val ExtraLarge = 96.dp
            val Large = 48.dp
            val Medium = 40.dp
        }

        object Text {
            val SizeLarge = 24.sp
            val SizeMedium = 16.sp
            val SizeSmall = 14.sp
            val SizeTiny = 10.dp

            val HeightSmall = 20.dp
            val HeightMedium = 60.dp

            val SpacingLarge = 1.5.sp
            val SpacingSmall = 0.5.sp
        }

        object Size {
            val ProgressIndicatorSmall = 32.dp
            val ProgressIndicatorBig = 96.dp

            val BorderWidth = 1.dp
            val ButtonCornerRadius = 8.dp

            object FillWidth {
                const val HALF = 0.5f
                const val LARGE = 0.8f
                const val FULL = 1f
            }
        }
    }
}