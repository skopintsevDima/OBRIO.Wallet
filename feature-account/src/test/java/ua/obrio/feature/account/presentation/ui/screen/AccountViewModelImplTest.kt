package ua.obrio.feature.account.presentation.ui.screen

import androidx.paging.PagingData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import ua.obrio.common.domain.model.AccountModel
import ua.obrio.common.domain.model.TransactionModel
import ua.obrio.common.presentation.ui.resources.provider.ResourceProvider
import ua.obrio.feature.account.domain.usecase.DepositUseCase
import ua.obrio.feature.account.domain.usecase.GetBitcoinExchangeRateUseCase
import ua.obrio.feature.account.domain.usecase.GetUserAccountFlowUseCase
import ua.obrio.feature.account.domain.usecase.GetUserTransactionsPagedUseCase
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class AccountViewModelImplTest {
    private val mockGetUserAccountFlowUseCase: GetUserAccountFlowUseCase = mockk()
    private val mockGetUserTransactionsPagedUseCase: GetUserTransactionsPagedUseCase = mockk()
    private val mockGetBitcoinExchangeRateUseCase: GetBitcoinExchangeRateUseCase = mockk()
    private val mockDepositUseCase: DepositUseCase = mockk()
    private val mockResourceProvider: ResourceProvider = mockk(relaxed = true)

    private lateinit var viewModel: AccountViewModelImpl
    private val testCoroutineScope = TestScope()
    private val testDispatcher = StandardTestDispatcher(testCoroutineScope.testScheduler)

    private val testAccount = AccountModel(id = 1, currentBalanceBTC = 50.0)
    private val testTransactions = listOf(
        TransactionModel(id = 1, LocalDateTime.now(), -5.0, TransactionModel.Category.TAXI),
        TransactionModel(id = 2, LocalDateTime.now(), -10.0, TransactionModel.Category.GROCERIES)
    )
    private val testPagingData = PagingData.from(testTransactions)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { mockResourceProvider.getString(any()) } returns "Error message"

        viewModel = AccountViewModelImpl(
            getUserAccountFlowUseCase = mockGetUserAccountFlowUseCase,
            getUserTransactionsPagedUseCase = mockGetUserTransactionsPagedUseCase,
            getBitcoinExchangeRateUseCase = mockGetBitcoinExchangeRateUseCase,
            depositUseCase = mockDepositUseCase,
            resourceProvider = mockResourceProvider,
            backgroundOpsDispatcher = testDispatcher
        )
    }

    @Test
    fun `initial state should be Loading`() = testCoroutineScope.runTest {
        coEvery { mockGetUserAccountFlowUseCase.execute() } returns flowOf(testAccount)
        coEvery { mockGetUserTransactionsPagedUseCase.execute() } returns flowOf(testPagingData)
        coEvery { mockGetBitcoinExchangeRateUseCase.execute() } returns 100500f

        assert(viewModel.uiState.value is UiState.Loading)
    }

    @Test
    fun `observeUserAccountUpdates updates state with account and transactions`() = testCoroutineScope.runTest {
        coEvery { mockGetUserAccountFlowUseCase.execute() } returns flowOf(testAccount)
        coEvery { mockGetUserTransactionsPagedUseCase.execute() } returns flowOf(testPagingData)
        coEvery { mockGetBitcoinExchangeRateUseCase.execute() } returns 100500f

        viewModel.tryHandleIntent(UiIntent.LoadUserAccount)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assert(state is UiState.Data)
        assertEquals(50.0, state.asData?.userBalanceBTC)
        assertEquals(100500f, state.asData?.bitcoinExchangeRateUSD)

        coVerify { mockGetUserAccountFlowUseCase.execute() }
        coVerify { mockGetBitcoinExchangeRateUseCase.execute() }
        verify { mockGetUserTransactionsPagedUseCase.execute() }
    }

    @Test
    fun `observeUserAccountUpdates handles errors gracefully`() = testCoroutineScope.runTest {
        coEvery { mockGetUserAccountFlowUseCase.execute() } returns flow { throw Exception("User data load failed") }
        coEvery { mockResourceProvider.getString(any()) } returns "Load error"

        viewModel.tryHandleIntent(UiIntent.LoadUserAccount)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assert(state is UiState.Error)
        assertEquals("Load error", (state as UiState.Error).errorMsg)

        coVerify { mockGetUserAccountFlowUseCase.execute() }
        verify { mockResourceProvider.getString(any()) }
    }

    @Test
    fun `deposit updates UI state on success`() = testCoroutineScope.runTest {
        coEvery { mockGetUserAccountFlowUseCase.execute() } returns flowOf(testAccount)
        coEvery { mockGetUserTransactionsPagedUseCase.execute() } returns flowOf(testPagingData)
        coEvery { mockGetBitcoinExchangeRateUseCase.execute() } returns 100500f
        coEvery { mockDepositUseCase.execute(any()) } returns Result.success(Unit)

        viewModel.tryHandleIntent(UiIntent.LoadUserAccount)
        advanceUntilIdle()
        val state1 = viewModel.uiState.value

        val depositAmountBTC = 10.0
        val expectedUserBalanceBTC = testAccount.currentBalanceBTC + depositAmountBTC
        coEvery { mockGetUserAccountFlowUseCase.execute() } returns flowOf(
            testAccount.copy(currentBalanceBTC = expectedUserBalanceBTC)
        )
        viewModel.tryHandleIntent(UiIntent.Deposit(depositAmountBTC))
        advanceUntilIdle()

        viewModel.tryHandleIntent(UiIntent.LoadUserAccount)
        advanceUntilIdle()
        val state2 = viewModel.uiState.value

        assert(state2 is UiState.Data)
        assertNotEquals(state1, state2)
        assertEquals(state2.asData?.userBalanceBTC, expectedUserBalanceBTC)

        coVerify { mockDepositUseCase.execute(depositAmountBTC) }
    }

    @Test
    fun `deposit updates UI state on failure`() = testCoroutineScope.runTest {
        coEvery { mockGetUserAccountFlowUseCase.execute() } returns flowOf(testAccount)
        coEvery { mockGetUserTransactionsPagedUseCase.execute() } returns flowOf(testPagingData)
        coEvery { mockGetBitcoinExchangeRateUseCase.execute() } returns 100500f
        coEvery { mockDepositUseCase.execute(any()) } returns Result.failure(Exception("Deposit failed"))
        coEvery { mockResourceProvider.getString(any()) } returns "Deposit error"

        viewModel.tryHandleIntent(UiIntent.LoadUserAccount)
        advanceUntilIdle()

        val depositAmountBTC = 10.0
        viewModel.tryHandleIntent(UiIntent.Deposit(depositAmountBTC))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assert(state is UiState.Data)
        assertNotNull(state.asData?.nonCriticalError)

        coVerify { mockDepositUseCase.execute(depositAmountBTC) }
        verify { mockResourceProvider.getString(any()) }
    }

    @Test
    fun `handle empty transactions correctly`() = testCoroutineScope.runTest {
        coEvery { mockGetUserAccountFlowUseCase.execute() } returns flowOf(testAccount)
        coEvery { mockGetUserTransactionsPagedUseCase.execute() } returns flowOf(PagingData.empty())
        coEvery { mockGetBitcoinExchangeRateUseCase.execute() } returns 100500f

        viewModel.tryHandleIntent(UiIntent.LoadUserAccount)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assert(state is UiState.Data)
        assertTrue(state.asData?.userTransactions is Flow<PagingData<TransactionModel>>)

        verify { mockGetUserTransactionsPagedUseCase.execute() }
    }

    @Test
    fun `bitcoin exchange rate update handles failure gracefully`() = testCoroutineScope.runTest {
        coEvery { mockGetUserAccountFlowUseCase.execute() } returns flowOf(testAccount)
        coEvery { mockGetUserTransactionsPagedUseCase.execute() } returns flowOf(testPagingData)
        coEvery { mockGetBitcoinExchangeRateUseCase.execute() } throws Exception("Network error")

        viewModel.tryHandleIntent(UiIntent.LoadUserAccount)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assert(state is UiState.Data)
        assertEquals(Float.NaN, state.asData?.bitcoinExchangeRateUSD)

        coVerify { mockGetBitcoinExchangeRateUseCase.execute() }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}