package com.rockspin.rxfluxcore

/**
 * Reducers specify how the application's state changes in response to [Result]s sent to the [Dispatcher].
 * Remember that [Result]   s only describe what happened, but don't describe how the application's state changes.
 */
interface Reducer<VS : State> {

    fun reduceToState(oldState: VS, result: Result): VS
}

fun <T : State> emptyReducer() = object : Reducer<T> {
    override fun reduceToState(oldState: T, result: Result): T = oldState
}

fun <VS : State> combineReducers(vararg reducers: Reducer<VS>): Reducer<VS> =
    object : Reducer<VS> {
        override fun reduceToState(oldState: VS, result: Result): VS {
            return reducers.fold(oldState) { acc, reducer -> reducer.reduceToState(acc, result) }
        }
    }
