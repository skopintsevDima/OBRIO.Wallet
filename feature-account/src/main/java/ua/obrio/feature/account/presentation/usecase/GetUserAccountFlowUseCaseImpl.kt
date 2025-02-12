package ua.obrio.feature.account.presentation.usecase

import kotlinx.coroutines.flow.Flow
import ua.obrio.common.domain.model.AccountModel
import ua.obrio.common.domain.repository.AccountRepository
import ua.obrio.feature.account.domain.usecase.GetUserAccountFlowUseCase

class GetUserAccountFlowUseCaseImpl(
    private val accountRepository: AccountRepository
): GetUserAccountFlowUseCase {
    override suspend fun execute(): Flow<AccountModel> {
        return accountRepository.getUserAccountFlow()
    }
}