package ua.obrio.feature.account.domain.usecase

import ua.obrio.common.domain.model.AccountModel

interface GetUserAccountUseCase {
    suspend fun execute(): AccountModel?
}