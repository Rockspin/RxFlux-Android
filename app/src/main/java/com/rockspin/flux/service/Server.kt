package com.rockspin.flux.service

import io.reactivex.Observable
import io.reactivex.Single
import java.util.concurrent.TimeUnit


/**
 * Represents basic calls to the server i.e a Retrofit service.
 */
class Server {

    /**
     * Mock call that loads a user
     */
    fun loadUser(userId: String): Single<User> {
        return Single.just(User(id = userId, name = "Rory")).delay(3, TimeUnit.SECONDS)
    }

}

data class User(val id: String, val name: String)
