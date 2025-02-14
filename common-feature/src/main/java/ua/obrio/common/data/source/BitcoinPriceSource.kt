package ua.obrio.common.data.source

interface BitcoinPriceSource {
    suspend fun getPriceUSD(): Float
}

interface BitcoinPriceStorage: BitcoinPriceSource {
    suspend fun savePrice(bitcoinPriceUSD: Float)
}