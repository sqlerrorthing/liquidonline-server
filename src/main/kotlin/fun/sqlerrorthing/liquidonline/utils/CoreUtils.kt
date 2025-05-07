package `fun`.sqlerrorthing.liquidonline.utils

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
fun require(value: Boolean, error: Throwable) {
    contract {
        returns() implies value
    }

    if (!value) {
        throw error
    }
}

@OptIn(ExperimentalContracts::class)
fun <T : Any> requireNotNull(value: T?, error: Throwable): T {
    contract {
        returns() implies (value != null)
    }

    return value ?: throw error
}
