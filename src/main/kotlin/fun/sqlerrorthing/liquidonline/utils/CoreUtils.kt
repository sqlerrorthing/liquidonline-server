package `fun`.sqlerrorthing.liquidonline.utils

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
inline fun require(value: Boolean, lazyError: () -> Throwable) {
    contract {
        returns() implies value
    }

    if (!value) {
        throw lazyError()
    }
}

@OptIn(ExperimentalContracts::class)
inline fun <T : Any> requireNotNull(value: T?, lazyError: () -> Throwable): T {
    contract {
        returns() implies (value != null)
    }

    return value ?: throw lazyError()
}
