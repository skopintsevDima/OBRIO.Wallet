package ua.obrio.feature.account.presentation.usecase

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import ua.obrio.common.domain.model.AccountModel
import ua.obrio.common.domain.repository.AccountRepository
import ua.obrio.feature.account.domain.usecase.GetUserAccountUseCase

class GetUserAccountUseCaseImpl(
    private val accountRepository: AccountRepository
): GetUserAccountUseCase {
    override suspend fun execute(): Flow<AccountModel?> {
        delay(500L) // TODO: Remove
        return accountRepository.getUserAccount()
    }
}