package ua.obrio.common.data.mapper

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import ua.obrio.common.data.db.entity.TransactionEntity
import ua.obrio.common.domain.model.TransactionModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TransactionMapperTest {
    private val dateFormatter = DateTimeFormatter.ISO_DATE_TIME

    @Test
    fun `toDomain correctly maps TransactionEntity to TransactionModel`() {
        val entity = TransactionEntity(
            id = 1,
            accountId = 1,
            dateTime = "2025-02-12T17:56:11.822",
            amountBTC = -5.0,
            category = "taxi"
        )

        val result = entity.toDomain()

        assertEquals(1, entity.id)
        assertEquals(LocalDateTime.parse("2025-02-12T17:56:11.822", dateFormatter), result.dateTime)
        assertEquals(-5.0, result.amountBTC, 0.0)
        assertEquals(TransactionModel.Category.TAXI, result.category)
    }

    @Test
    fun `toDomain handles null category correctly`() {
        val entity = TransactionEntity(
            id = 2,
            accountId = 1,
            dateTime = "2025-02-12T17:56:11.822",
            amountBTC = 10.0,
            category = null
        )

        val result = entity.toDomain()

        assertEquals(LocalDateTime.parse("2025-02-12T17:56:11.822", dateFormatter), result.dateTime)
        assertEquals(10.0, result.amountBTC, 0.0)
        assertNull(result.category)
    }

    @Test
    fun `toEntity correctly maps TransactionModel to TransactionEntity`() {
        val model = TransactionModel(
            dateTime = LocalDateTime.of(2025, 2, 12, 17, 56, 11),
            amountBTC = -5.0,
            category = TransactionModel.Category.RESTAURANT
        )

        val result = model.toEntity()

        assertEquals("2025-02-12T17:56:11", result.dateTime)
        assertEquals(-5.0, result.amountBTC, 0.0)
        assertEquals("restaurant", result.category)
    }

    @Test
    fun `toEntity handles null category correctly`() {
        val model = TransactionModel(
            dateTime = LocalDateTime.of(2025, 2, 12, 17, 56, 11),
            amountBTC = 15.0,
            category = null
        )

        val result = model.toEntity()

        assertEquals("2025-02-12T17:56:11", result.dateTime)
        assertEquals(15.0, result.amountBTC, 0.0)
        assertNull(result.category)
    }

    @Test
    fun `toDomain and toEntity maintain data integrity`() {
        val model = TransactionModel(
            dateTime = LocalDateTime.of(2025, 2, 12, 17, 56, 11),
            amountBTC = -5.0,
            category = TransactionModel.Category.GROCERIES
        )

        val entity = model.toEntity()
        val mappedBackModel = entity.toDomain()

        assertEquals(model.dateTime, mappedBackModel.dateTime)
        assertEquals(model.amountBTC, mappedBackModel.amountBTC, 0.0)
        assertEquals(model.category, mappedBackModel.category)
    }
}