package ua.obrio.common.data.api.coingecko

import retrofit2.http.GET
import ua.obrio.common.data.api.response.BitcoinPriceResponse
import ua.obrio.common.presentation.util.Constants.Network.COIN_GECKO_BTC_USD_REQUEST_URL

interface CoinGeckoApi {
    @GET(COIN_GECKO_BTC_USD_REQUEST_URL)
    suspend fun getBTCPriceUSD(): BitcoinPriceResponse
}