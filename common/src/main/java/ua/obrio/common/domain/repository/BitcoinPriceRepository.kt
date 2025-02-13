package ua.obrio.common.domain.repository

interface BitcoinPriceRepository {
    suspend fun getPriceUSD(): Float
}