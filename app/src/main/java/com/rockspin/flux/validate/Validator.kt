package com.rockspin.flux.validate

import com.rockspin.rxfluxcore.Result

sealed class EmailValidResult : Result {
    object Valid : EmailValidResult()
    object TooShort : EmailValidResult()
    object BadlyFormatted : EmailValidResult()
}

fun String.isEmailValid(): EmailValidResult =
    when {
        !contains("@") -> EmailValidResult.BadlyFormatted
        !contains(".com") -> EmailValidResult.BadlyFormatted
        length < 4 -> EmailValidResult.TooShort
        else -> EmailValidResult.Valid
    }


sealed class PasswordValidResult : Result {
    object Valid : PasswordValidResult()
    object TooShort : PasswordValidResult()
}

fun String.isValidPassword() = when {
    length < 4 -> PasswordValidResult.TooShort
    else -> PasswordValidResult.Valid
}
