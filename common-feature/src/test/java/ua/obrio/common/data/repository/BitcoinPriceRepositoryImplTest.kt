package ua.obrio.common.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import ua.obrio.common.data.source.BitcoinPriceSource
import ua.obrio.common.data.source.BitcoinPriceStorage

@OptIn(ExperimentalCoroutinesApi::class)
class BitcoinPriceRepositoryImplTest {
    private val mockBitcoinPriceSource: BitcoinPriceSource = mockk()
    private val mockBitcoinPriceStorage: BitcoinPriceStorage = mockk(relaxed = true)
    private val mockDataStore: DataStore<Preferences> = mockk(relaxed = true)

    private lateinit var repository: BitcoinPriceRepositoryImpl
    private val testCoroutineScope = TestScope()
    private val testDispatcher = StandardTestDispatcher(testCoroutineScope.testScheduler)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = BitcoinPriceRepositoryImpl(
            bitcoinPriceSource = mockBitcoinPriceSource,
            bitcoinPriceStorage = mockBitcoinPriceStorage,
            dataStore = mockDataStore
        )
    }

    @Test
    fun `getPriceUSD returns stored price when no update needed`() = testCoroutineScope.runTest {
        coEvery { mockBitcoinPriceStorage.getPriceUSD() } returns 50000f
        coEvery { mockDataStore.data } returns flowOf(
            preferencesOf(BitcoinPriceRepositoryImpl.KEY_LAST_UPDATE to System.currentTimeMillis())
        )

        val price = repository.getPriceUSD()

        assertEquals(50000f, price)
        coVerify { mockBitcoinPriceStorage.getPriceUSD() }
        coVerify(exactly = 0) { mockBitcoinPriceSource.getPriceUSD() }
    }

    @Test
    fun `getPriceUSD fetches new price when update is required`() = testCoroutineScope.runTest {
        var storedPrice = 50000f

        coEvery { mockBitcoinPriceStorage.getPriceUSD() } answers { storedPrice }
        coEvery { mockBitcoinPriceSource.getPriceUSD() } returns 51000f
        coEvery { mockDataStore.data } returns flowOf(
            preferencesOf(BitcoinPriceRepositoryImpl.KEY_LAST_UPDATE to 0L) // Force update
        )
        coEvery { mockBitcoinPriceStorage.savePrice(any()) } answers { storedPrice = firstArg() }

        val price = repository.getPriceUSD()

        assertEquals(51000f, price) // Ensure updated value is used
        coVerify { mockBitcoinPriceSource.getPriceUSD() }
        coVerify { mockBitcoinPriceStorage.savePrice(51000f) }
    }

    @Test
    fun `getPriceUSD handles fetch failure and returns stored price`() = testCoroutineScope.runTest {
        coEvery { mockBitcoinPriceStorage.getPriceUSD() } returns 50000f
        coEvery { mockBitcoinPriceSource.getPriceUSD() } throws Exception("Network error")
        coEvery { mockDataStore.data } returns flowOf(
            preferencesOf(BitcoinPriceRepositoryImpl.KEY_LAST_UPDATE to 0L) // Force update
        )

        val price = repository.getPriceUSD()

        assertEquals(50000f, price)
        coVerify { mockBitcoinPriceStorage.getPriceUSD() }
        coVerify { mockBitcoinPriceSource.getPriceUSD() }
    }

    @Test
    fun `refreshPriceIfNeeded updates price only when needed`() = testCoroutineScope.runTest {
        coEvery { mockBitcoinPriceSource.getPriceUSD() } returns 51000f
        coEvery { mockBitcoinPriceStorage.getPriceUSD() } returns 50000f
        coEvery { mockDataStore.data } returns flowOf(
            preferencesOf(BitcoinPriceRepositoryImpl.KEY_LAST_UPDATE to 0L) // Force update
        )

        repository.getPriceUSD() // Triggers update

        coVerify { mockBitcoinPriceSource.getPriceUSD() }
        coVerify { mockBitcoinPriceStorage.savePrice(51000f) }
    }

    @Test
    fun `isPriceUpdateRequired returns false if session is not new and last update was recent`() = testCoroutineScope.runTest {
        coEvery { mockDataStore.data } returns flowOf(
            preferencesOf(BitcoinPriceRepositoryImpl.KEY_LAST_UPDATE to System.currentTimeMillis() - 5000L)
        )

        repository.getPriceUSD() // Calls refreshPriceIfNeeded()

        coVerify(exactly = 0) { mockBitcoinPriceSource.getPriceUSD() }
    }

    @Test
    fun `isPriceUpdateRequired returns true if session is new`() = testCoroutineScope.runTest {
        coEvery { mockDataStore.data } returns flowOf(
            preferencesOf(BitcoinPriceRepositoryImpl.KEY_LAST_UPDATE to 0L)
        )

        repository.getPriceUSD() // Calls refreshPriceIfNeeded()

        coVerify { mockBitcoinPriceSource.getPriceUSD() }
    }

    @Test
    fun `savePriceLastUpdateTime saves timestamp correctly`() = testCoroutineScope.runTest {
        val mockPreferences = mockk<Preferences>(relaxed = true)
        coEvery { mockDataStore.updateData(any()) } coAnswers { mockPreferences }
        coEvery { mockDataStore.data } returns flowOf(
            preferencesOf(BitcoinPriceRepositoryImpl.KEY_LAST_UPDATE to 0L) // Force update
        )

        repository.getPriceUSD() // Triggers refresh

        coVerify { mockDataStore.updateData(any()) }
    }

    @Test
    fun `getPriceLastUpdateTime returns default value if no previous update exists`() = testCoroutineScope.runTest {
        coEvery { mockDataStore.data } returns flowOf(preferencesOf())

        val lastUpdateTime = repository.getPriceUSD() // Triggers check

        assertNotNull(lastUpdateTime)
        coVerify { mockDataStore.data }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}