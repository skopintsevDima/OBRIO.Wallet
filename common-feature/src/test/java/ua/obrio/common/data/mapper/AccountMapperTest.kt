package ua.obrio.common.data.mapper

import org.junit.Assert.*
import org.junit.Test
import ua.obrio.common.data.db.entity.AccountEntity
import ua.obrio.common.domain.model.AccountModel

class AccountMapperTest {
    @Test
    fun `toDomain correctly maps AccountEntity to AccountModel`() {
        val entity = AccountEntity(id = 1, currentBalanceBTC = 100.5)

        val result = entity.toDomain()

        assertEquals(1, result.id)
        assertEquals(100.5, result.currentBalanceBTC, 0.0)
    }

    @Test
    fun `toEntity correctly maps AccountModel to AccountEntity`() {
        val model = AccountModel(id = 2, currentBalanceBTC = 200.75)

        val result = model.toEntity()

        assertEquals(2, result.id)
        assertEquals(200.75, result.currentBalanceBTC, 0.0)
    }

    @Test
    fun `toDomain and toEntity maintain data integrity`() {
        val model = AccountModel(id = 3, currentBalanceBTC = 500.0)

        val entity = model.toEntity()
        val mappedBackModel = entity.toDomain()

        assertEquals(model.id, mappedBackModel.id)
        assertEquals(model.currentBalanceBTC, mappedBackModel.currentBalanceBTC, 0.0)
    }
}