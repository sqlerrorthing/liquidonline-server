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
