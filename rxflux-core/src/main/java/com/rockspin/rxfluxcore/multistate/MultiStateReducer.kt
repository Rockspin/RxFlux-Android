package com.rockspin.rxfluxcore.multistate

import com.rockspin.rxfluxcore.Reducer
import com.rockspin.rxfluxcore.Result
import com.rockspin.rxfluxcore.State

/**
 * A class that manages a state of
 * @param create a function that returns new [State].
 * @param states a map of string to [State]s
 */
data class MultiState<VS>(private val create: () -> VS, val states: Map<String, VS> = mapOf()) :
    State {

    fun getOrCreate(key: String): VS = states.getOrElse(key, create)
}

class MultiStateReducer<VS: State>(
    private val keySelector: (result: Result) -> String?,
    private val wrappedReducer: Reducer<VS>
) :
    Reducer<MultiState<VS>> {

    override fun reduceToState(oldState: MultiState<VS>, result: Result): MultiState<VS> {
        val key = keySelector(result)

        val map = if (key == null) {
            // send the result to every reducer
            oldState.states.plus(
                oldState.states.entries.map { Pair(it.key, wrappedReducer.reduceToState(it.value, result)) }
            )
        } else {
            // send the result to one reducer
            val reduceToState = wrappedReducer.reduceToState(oldState.getOrCreate(key), result)
            oldState.states.plus(Pair(key, reduceToState))
        }
        return oldState.copy(states = map)
    }

}
