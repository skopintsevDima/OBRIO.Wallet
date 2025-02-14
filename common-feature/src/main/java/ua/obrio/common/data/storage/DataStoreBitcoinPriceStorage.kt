package ua.obrio.common.data.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import ua.obrio.common.data.source.BitcoinPriceStorage

class DataStoreBitcoinPriceStorage(
    private val dataStore: DataStore<Preferences>
): BitcoinPriceStorage {
    override suspend fun savePrice(bitcoinPriceUSD: Float) {
        runCatching {
            dataStore.edit { preferences ->
                preferences[KEY_BTC_PRICE_USD] = bitcoinPriceUSD
            }
        }
    }

    override suspend fun getPriceUSD(): Float {
        return runCatching {
            dataStore.data
                .catch { PRICE_DEFAULT_VALUE }
                .map { preferences -> preferences[KEY_BTC_PRICE_USD] ?: PRICE_DEFAULT_VALUE }
        }.fold(
            onSuccess = { it.firstOrNull() ?: PRICE_DEFAULT_VALUE },
            onFailure = { PRICE_DEFAULT_VALUE }
        )
    }

    companion object {
        private const val PRICE_DEFAULT_VALUE = Float.NaN
        internal val KEY_BTC_PRICE_USD = floatPreferencesKey("KEY_BTC_PRICE_USD")
    }
}