package ua.obrio.common.presentation.util

object Constants {
    object ErrorCodes {
        object Account {
            const val ERROR_ACCOUNT_NO_DATA = 101
            const val ERROR_LOAD_USER_ACCOUNT = 102
            const val ERROR_LOAD_BITCOIN_PRICE = 103
            const val ERROR_DEPOSIT_FAILED = 104
        }

        object Common {
            const val ERROR_UNKNOWN = 100500
        }
    }

    object UI {
        object Common {
            const val FORMAT_TRANSACTION_DATE_TIME = "MMM dd, yyyy HH:mm"
        }

        object Account {
            const val ROUTE_ID = "ROUTE_ID_ACCOUNT_SCREEN"
            const val TAG = "AccountScreen"
        }

        object AddTransaction {
            const val ROUTE_ID = "ROUTE_ID_ADD_TRANSACTION_SCREEN"
            const val TAG = "AddTransactionScreen"
        }
    }
}