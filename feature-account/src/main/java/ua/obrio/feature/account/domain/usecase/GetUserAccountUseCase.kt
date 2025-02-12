package ua.obrio.feature.account.domain.usecase

import kotlinx.coroutines.flow.Flow
import ua.obrio.common.domain.model.AccountModel

interface GetUserAccountUseCase {
    suspend fun execute(): Flow<AccountModel>
}