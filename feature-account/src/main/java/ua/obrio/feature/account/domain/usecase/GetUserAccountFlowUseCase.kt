package ua.obrio.feature.account.domain.usecase

import kotlinx.coroutines.flow.Flow
import ua.obrio.common.domain.model.AccountModel

interface GetUserAccountFlowUseCase {
    suspend fun execute(): Flow<AccountModel>
}