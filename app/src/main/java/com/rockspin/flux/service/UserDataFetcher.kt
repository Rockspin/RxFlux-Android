package com.rockspin.flux.service

import com.rockspin.rxfluxcore.Result
import io.reactivex.Observable
import timber.log.Timber


sealed class LoadUserResult : Result {
    object Loading : LoadUserResult()
    data class Error(val userId: String, val error: String) : LoadUserResult()
    data class Success(val user: User) : LoadUserResult()
}

class UserDataFetcher(val server: Server) {

    fun loadUser(userId: String): Observable<LoadUserResult> =
        server.loadUser(userId)
            .map<LoadUserResult> {
                LoadUserResult.Success(it)
            }.onErrorReturn {
                Timber.e(it,"error loading User")
                LoadUserResult.Error(userId, it.message.orEmpty())
            }
            .toObservable()
            .startWith(LoadUserResult.Loading)

}