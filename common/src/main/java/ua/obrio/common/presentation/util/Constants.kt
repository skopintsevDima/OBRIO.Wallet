package ua.obrio.common.presentation.util

object Constants {
    object ErrorCodes {
        object Account {
            const val ERROR_USER_ACCOUNT_NOT_CREATED = 101
            const val ERROR_LOAD_USER_ACCOUNT = 102
            const val ERROR_DEPOSIT_FAILED = 103
            const val ERROR_NO_DATA_FOR_TRANSACTION = 104
            const val ERROR_INCORRECT_AMOUNT_FOR_TRANSACTION = 105 // TODO: Show error for user
            const val ERROR_MISSING_CATEGORY_FOR_TRANSACTION = 106
            const val ERROR_ADDING_TRANSACTION_FAILED = 107
            const val ERROR_USER_ACCOUNT_UPDATE_FAILED = 108
        }

        object Common {
            const val ERROR_UNKNOWN = 100500
        }
    }

    object Database {
        const val DATABASE_NAME = "wallet_database"
        const val DATABASE_VERSION = 1
    }

    object UI {
        object Common {
            const val FORMAT_TRANSACTION_DATE_TIME = "MMM dd, yyyy HH:mm"
        }

        object Account {
            const val ROUTE_ID = "ROUTE_ID_ACCOUNT_SCREEN"
            const val TAG = "AccountScreen"

            const val TRANSACTIONS_PAGE_SIZE = 20
        }

        object AddTransaction {
            const val ROUTE_ID = "ROUTE_ID_ADD_TRANSACTION_SCREEN"
            const val TAG = "AddTransactionScreen"
        }
    }
}