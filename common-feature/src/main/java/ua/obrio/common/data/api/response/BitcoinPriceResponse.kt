package ua.obrio.common.data.api.response

data class BitcoinPriceResponse(
    val bitcoin: BitcoinPrice
)

data class BitcoinPrice(
    val usd: Float
)