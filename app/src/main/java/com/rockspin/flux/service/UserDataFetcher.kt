package com.rockspin.flux.service

import com.rockspin.rxfluxcore.Result
import io.reactivex.Observable
import io.reactivex.ObservableSource
import timber.log.Timber


sealed class LoginResult : Result {
    object Loading : LoginResult()
    data class Error(val error: String) : LoginResult()
    data class Success(val user: User) : LoginResult()
}

class UserDataFetcher(private val server: Server) {

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