package ua.obrio.wallet.presentation.ui.composable

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ua.obrio.common.presentation.util.Constants.UI.Account
import ua.obrio.common.presentation.util.Constants.UI.AddTransaction
import ua.obrio.feature.account.presentation.ui.screen.AccountScreen
import ua.obrio.feature.add_transaction.presentation.ui.screen.AddTransactionScreen

@Composable
fun MainNavHost(
    navController: NavHostController = rememberNavController(),
    padding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = Account.ROUTE_ID,
        modifier = Modifier.padding(padding)
    ) {
        composable(Account.ROUTE_ID) {
            AccountScreen(navController)
        }
        composable(AddTransaction.ROUTE_ID) {
            AddTransactionScreen(navController)
        }
    }
}