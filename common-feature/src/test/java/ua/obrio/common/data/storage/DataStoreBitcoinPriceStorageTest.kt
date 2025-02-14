package ua.obrio.common.data.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DataStoreBitcoinPriceStorageTest {
    private val mockDataStore: DataStore<Preferences> = mockk(relaxed = true)
    private lateinit var storage: DataStoreBitcoinPriceStorage
    private val testCoroutineScope = TestScope()
    private val testDispatcher = StandardTestDispatcher(testCoroutineScope.testScheduler)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        storage = DataStoreBitcoinPriceStorage(dataStore = mockDataStore)
    }

    @Test
    fun `savePrice correctly stores price in DataStore`() = testCoroutineScope.runTest {
        val mockPreferences = mockk<Preferences>(relaxed = true)
        coEvery { mockDataStore.updateData(any()) } coAnswers { mockPreferences }

        storage.savePrice(50000f)

        coVerify { mockDataStore.updateData(any()) }
    }

    @Test
    fun `getPriceUSD returns stored price`() = testCoroutineScope.runTest {
        coEvery { mockDataStore.data } returns flowOf(
            preferencesOf(DataStoreBitcoinPriceStorage.KEY_BTC_PRICE_USD to 50000f)
        )

        val price = storage.getPriceUSD()

        assertEquals(50000f, price)
        coVerify { mockDataStore.data }
    }

    @Test
    fun `getPriceUSD returns NaN if no price is stored`() = testCoroutineScope.runTest {
        coEvery { mockDataStore.data } returns flowOf(preferencesOf())

        val price = storage.getPriceUSD()

        assertEquals(Float.NaN, price)
        coVerify { mockDataStore.data }
    }

    @Test
    fun `getPriceUSD handles DataStore exceptions and returns NaN`() = testCoroutineScope.runTest {
        coEvery { mockDataStore.data } returns flow { throw Exception("DataStore error") }

        val price = storage.getPriceUSD()

        assertEquals(Float.NaN, price)
        coVerify { mockDataStore.data }
    }

    @Test
    fun `savePrice handles DataStore exceptions gracefully`() = testCoroutineScope.runTest {
        coEvery { mockDataStore.updateData(any()) } throws Exception("DataStore error")

        try {
            storage.savePrice(50000f)
        } catch (e: Exception) {
            fail("Exception should not be thrown")
        }

        coVerify { mockDataStore.updateData(any()) }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}