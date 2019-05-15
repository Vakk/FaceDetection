package com.valery.coursework.utils.extensions

inline fun <reified T> Any?.cast(): T {
    if (this is T) {
        return this
    } else {
        throw IllegalStateException("${this?.javaClass?.name ?: "null"} can't be cast to ${T::class.java.name}")
    }
}