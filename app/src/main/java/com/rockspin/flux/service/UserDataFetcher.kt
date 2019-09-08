package com.rockspin.flux.service

import com.rockspin.rxfluxcore.Result
import io.reactivex.Observable
import io.reactivex.ObservableSource
import timber.log.Timber


sealed class LoadUserResult : Result {
    object Loading : LoadUserResult()
    data class Error(val userId: String, val error: String) : LoadUserResult()
    data class Success(val user: User) : LoadUserResult()
}

sealed class LoginResult : Result {
    object Loading : LoginResult()
    data class Error(val error: String) : LoginResult()
    data class Success(val user: User) : LoginResult()
}

class UserDataFetcher(private val server: Server) {

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

    fun login(email :String, password: String): ObservableSource<out Result> =
        server.login(email, password)
            .map<LoginResult> {
                LoginResult.Success(it)
            }.onErrorReturn {
                Timber.e(it,"error loading User")
                LoginResult.Error(it.message.orEmpty())
            }
            .toObservable()
            .startWith(LoginResult.Loading)

}