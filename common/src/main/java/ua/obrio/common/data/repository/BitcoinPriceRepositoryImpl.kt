package ua.obrio.common.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import ua.obrio.common.data.source.BitcoinPriceSource
import ua.obrio.common.data.source.BitcoinPriceStorage
import ua.obrio.common.domain.repository.BitcoinPriceRepository
import ua.obrio.common.presentation.util.Constants.Storage.BTC_PRICE_REFRESH_TIME_LIMIT_SEC

class BitcoinPriceRepositoryImpl(
    private val bitcoinPriceSource: BitcoinPriceSource,
    private val bitcoinPriceStorage: BitcoinPriceStorage,
    private val dataStore: DataStore<Preferences>
): BitcoinPriceRepository {
    private var isNewSession = true

    override suspend fun getPriceUSD(): Float {
        refreshPriceIfNeeded()
        return bitcoinPriceStorage.getPriceUSD()
    }

    private suspend fun refreshPriceIfNeeded() {
        if (isPriceUpdateRequired()) {
            val upToDateBitcoinPriceUSD = runCatching { bitcoinPriceSource.getPriceUSD() }.fold(
                onSuccess = { it },
                onFailure = { bitcoinPriceStorage.getPriceUSD() }
            )
            bitcoinPriceStorage.savePrice(upToDateBitcoinPriceUSD)

            isNewSession = false
            savePriceLastUpdateTime(System.currentTimeMillis())
        }
    }

    private suspend fun isPriceUpdateRequired(): Boolean {
        val lastUpdateTimestampMs = getPriceLastUpdateTime()
        val currentTimestampMs = System.currentTimeMillis()
        val timeAfterLastUpdateSeconds = (currentTimestampMs - lastUpdateTimestampMs) / 1000
        return (isNewSession && timeAfterLastUpdateSeconds > BTC_PRICE_REFRESH_TIME_LIMIT_SEC)
    }

    private suspend fun savePriceLastUpdateTime(lastUpdateTime: Long) {
        runCatching {
            dataStore.edit { preferences ->
                preferences[KEY_LAST_UPDATE] = lastUpdateTime
            }
        }
    }

    private suspend fun getPriceLastUpdateTime(): Long {
        return runCatching {
            dataStore.data
                .catch { Log.d(TAG, "getPriceLastUpdateTime error: $it") }
                .map { preferences -> preferences[KEY_LAST_UPDATE] ?: LAST_UPDATE_DEFAULT_VALUE }
        }.fold(
            onSuccess = { it.firstOrNull() ?: LAST_UPDATE_DEFAULT_VALUE },
            onFailure = { LAST_UPDATE_DEFAULT_VALUE }
        )
    }

    companion object {
        private const val LAST_UPDATE_DEFAULT_VALUE = 0L
        private val TAG = BitcoinPriceRepositoryImpl::class.simpleName
        internal val KEY_LAST_UPDATE = longPreferencesKey("KEY_LAST_UPDATE")
    }
}