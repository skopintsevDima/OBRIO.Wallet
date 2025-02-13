package ua.obrio.common.data.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ua.obrio.common.data.source.BitcoinPriceStorage

class DataStoreBitcoinPriceStorage(
    private val dataStore: DataStore<Preferences>
): BitcoinPriceStorage {
    override suspend fun savePrice(bitcoinPriceUSD: Float) {
        dataStore.edit { preferences ->
            preferences[KEY_BTC_PRICE_USD] = bitcoinPriceUSD
        }
    }

    override suspend fun getPriceUSD(): Float {
        return dataStore.data.map { preferences ->
            preferences[KEY_BTC_PRICE_USD] ?: Float.NaN
        }.first()
    }

    companion object {
        private val KEY_BTC_PRICE_USD = floatPreferencesKey("KEY_BTC_PRICE_USD")
    }
}