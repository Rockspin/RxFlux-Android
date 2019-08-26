package com.rockspin.flux.ui

import com.rockspin.flux.service.LoadUserResult
import com.rockspin.flux.service.UserDataFetcher
import com.rockspin.rxfluxandroid.FluxViewModel
import com.rockspin.rxfluxcore.*
import io.reactivex.Observable
import org.koin.dsl.module


class MainFlux(reducer: MainReducer, resultCreator: MainResultCreator) :
    FluxViewModel<MainEvents, MainState, MainEffects>(reducer, MainState(), resultCreator = resultCreator)


class MainResultCreator(private val userDataFetcher: UserDataFetcher) : ResultCreator<MainEvents> {

    override fun createResults(events: Observable<MainEvents>): Observable<Result> =
        events.flatMap {
            when (it) {
                MainEvents.ButtonClicked -> userDataFetcher.loadUser("userId")
            }
        }
}

class MainReducer : Reducer<MainState> {
    override fun reduceToState(oldState: MainState, result: Result): MainState {
        return  when (result) {
            is LoadUserResult -> {
                when (result) {
                    LoadUserResult.Loading -> oldState.copy(loading = true)
                    is LoadUserResult.Error -> oldState.copy(loading = false)
                    is LoadUserResult.Success -> oldState.copy(loading = false, user = result.user)
                }
            }
            else -> oldState
        }
    }
}



val mainActivityModule = module {
    factory { MainFlux(get(), get()) }
    factory { MainResultCreator(get()) }
    factory { MainReducer() }
}

