package ua.obrio.feature.add_transaction.presentation.ui.screen

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import ua.obrio.common.domain.model.TransactionModel
import ua.obrio.common.presentation.ui.resources.provider.ResourceProvider
import ua.obrio.feature.add_transaction.domain.usecase.AddTransactionUseCase

@OptIn(ExperimentalCoroutinesApi::class)
class AddTransactionViewModelTest {
    private val mockResourceProvider: ResourceProvider = mockk(relaxed = true)
    private val mockAddTransactionUseCase: AddTransactionUseCase = mockk()

    private lateinit var viewModel: AddTransactionViewModelImpl
    private val testCoroutineScope = TestScope()
    private val testDispatcher = StandardTestDispatcher(testCoroutineScope.testScheduler)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AddTransactionViewModelImpl(
            resourceProvider = mockResourceProvider,
            addTransactionUseCase = mockAddTransactionUseCase,
            backgroundOpsDispatcher = testDispatcher
        )
    }

    @Test
    fun `initial state should be Data`() = testCoroutineScope.runTest {
        assert(viewModel.uiState.value is UiState.Data)
    }

    @Test
    fun `UpdateEnteredAmount updates uiState correctly`() = testCoroutineScope.runTest {
        viewModel.tryHandleIntent(UiIntent.UpdateEnteredAmount("10.5"))
        advanceUntilIdle()

        val state = viewModel.uiState.value as UiState.Data
        assertEquals("10.5", state.strEnteredAmountBTC)
    }

    @Test
    fun `UpdateSelectedCategory updates uiState correctly`() = testCoroutineScope.runTest {
        val category = TransactionModel.Category.GROCERIES

        viewModel.tryHandleIntent(UiIntent.UpdateSelectedCategory(category))
        advanceUntilIdle()

        val state = viewModel.uiState.value as UiState.Data
        assertEquals(category, state.selectedCategory)
    }

    @Test
    fun `AddTransaction succeeds with valid input`() = testCoroutineScope.runTest {
        coEvery { mockAddTransactionUseCase.execute(any(), any(), any()) } returns Result.success(Unit)

        viewModel.tryHandleIntent(UiIntent.UpdateEnteredAmount("10.0"))
        viewModel.tryHandleIntent(UiIntent.UpdateSelectedCategory(TransactionModel.Category.TAXI))

        viewModel.tryHandleIntent(UiIntent.AddTransaction)
        advanceUntilIdle()

        assert(viewModel.uiState.value is UiState.Finish)
        coVerify { mockAddTransactionUseCase.execute(any(), 10.0, TransactionModel.Category.TAXI) }
    }

    @Test
    fun `AddTransaction fails with invalid amount`() = testCoroutineScope.runTest {
        viewModel.tryHandleIntent(UiIntent.UpdateEnteredAmount("invalid_amount"))

        viewModel.tryHandleIntent(UiIntent.AddTransaction)
        advanceUntilIdle()

        val state = viewModel.uiState.value as UiState.Data
        assertNotNull(state.nonCriticalError)
    }

    @Test
    fun `AddTransaction fails with missing category`() = testCoroutineScope.runTest {
        viewModel.tryHandleIntent(UiIntent.UpdateEnteredAmount("10.0"))

        viewModel.tryHandleIntent(UiIntent.AddTransaction)
        advanceUntilIdle()

        val state = viewModel.uiState.value as UiState.Data
        assertNotNull(state.nonCriticalError)
    }

    @Test
    fun `AddTransaction fails if use case execution fails`() = testCoroutineScope.runTest {
        coEvery { mockAddTransactionUseCase.execute(any(), any(), any()) } returns Result.failure(Throwable("Error"))

        viewModel.tryHandleIntent(UiIntent.UpdateEnteredAmount("10.0"))
        viewModel.tryHandleIntent(UiIntent.UpdateSelectedCategory(TransactionModel.Category.TAXI))

        viewModel.tryHandleIntent(UiIntent.AddTransaction)
        advanceUntilIdle()

        val state = viewModel.uiState.value as UiState.Data
        assertNotNull(state.nonCriticalError)
        coVerify { mockAddTransactionUseCase.execute(any(), 10.0, TransactionModel.Category.TAXI) }
    }

    @Test
    fun `AddTransaction prevents duplicate transactions`() = testCoroutineScope.runTest {
        coEvery { mockAddTransactionUseCase.execute(any(), any(), any()) } returns Result.success(Unit)

        viewModel.tryHandleIntent(UiIntent.UpdateEnteredAmount("10.0"))
        viewModel.tryHandleIntent(UiIntent.UpdateSelectedCategory(TransactionModel.Category.TAXI))

        viewModel.tryHandleIntent(UiIntent.AddTransaction)
        advanceUntilIdle()

        viewModel.tryHandleIntent(UiIntent.AddTransaction)
        advanceUntilIdle()

        coVerify(exactly = 1) { mockAddTransactionUseCase.execute(any(), 10.0, TransactionModel.Category.TAXI) }
    }

    @Test
    fun `AddTransaction handles floating point precision correctly`() = testCoroutineScope.runTest {
        coEvery { mockAddTransactionUseCase.execute(any(), any(), any()) } returns Result.success(Unit)

        viewModel.tryHandleIntent(UiIntent.UpdateEnteredAmount("10.0000001"))
        viewModel.tryHandleIntent(UiIntent.UpdateSelectedCategory(TransactionModel.Category.RESTAURANT))

        viewModel.tryHandleIntent(UiIntent.AddTransaction)
        advanceUntilIdle()

        coVerify { mockAddTransactionUseCase.execute(any(), eq(10.0000001), any()) }
    }

    @Test
    fun `AddTransaction handles large amount values correctly`() = testCoroutineScope.runTest {
        coEvery { mockAddTransactionUseCase.execute(any(), any(), any()) } returns Result.success(Unit)

        viewModel.tryHandleIntent(UiIntent.UpdateEnteredAmount("1000000000.0"))
        viewModel.tryHandleIntent(UiIntent.UpdateSelectedCategory(TransactionModel.Category.ELECTRONICS))

        viewModel.tryHandleIntent(UiIntent.AddTransaction)
        advanceUntilIdle()

        coVerify { mockAddTransactionUseCase.execute(any(), eq(1000000000.0), any()) }
    }

    @Test
    fun `AddTransaction handles negative values gracefully`() = testCoroutineScope.runTest {
        viewModel.tryHandleIntent(UiIntent.UpdateEnteredAmount("-10.0"))
        viewModel.tryHandleIntent(UiIntent.UpdateSelectedCategory(TransactionModel.Category.OTHER))

        viewModel.tryHandleIntent(UiIntent.AddTransaction)
        advanceUntilIdle()

        val state = viewModel.uiState.value as UiState.Data
        assertNotNull(state.nonCriticalError)
    }

    @Test
    fun `AddTransaction handles zero amount gracefully`() = testCoroutineScope.runTest {
        viewModel.tryHandleIntent(UiIntent.UpdateEnteredAmount("0.0"))
        viewModel.tryHandleIntent(UiIntent.UpdateSelectedCategory(TransactionModel.Category.GROCERIES))

        viewModel.tryHandleIntent(UiIntent.AddTransaction)
        advanceUntilIdle()

        val state = viewModel.uiState.value as UiState.Data
        assertNotNull(state.nonCriticalError)
    }

    @Test
    fun `AddTransaction handles empty input gracefully`() = testCoroutineScope.runTest {
        viewModel.tryHandleIntent(UiIntent.UpdateEnteredAmount(""))
        viewModel.tryHandleIntent(UiIntent.UpdateSelectedCategory(TransactionModel.Category.GROCERIES))

        viewModel.tryHandleIntent(UiIntent.AddTransaction)
        advanceUntilIdle()

        val state = viewModel.uiState.value as UiState.Data
        assertNotNull(state.nonCriticalError)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}