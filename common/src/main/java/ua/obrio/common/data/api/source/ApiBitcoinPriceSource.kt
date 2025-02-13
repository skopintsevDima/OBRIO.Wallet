package ua.obrio.common.data.api.source

import ua.obrio.common.data.api.coingecko.CoinGeckoApi
import ua.obrio.common.data.source.BitcoinPriceSource

class ApiBitcoinPriceSource(
    private val coinGeckoApi: CoinGeckoApi
): BitcoinPriceSource {
    override suspend fun getPriceUSD(): Float {
        return coinGeckoApi.getBTCPriceUSD().bitcoin.usd
    }
}