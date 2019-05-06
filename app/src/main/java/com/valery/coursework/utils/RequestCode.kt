package com.valery.coursework.utils

enum class RequestCode(val code: Int) {

    DEFAULT;

    companion object {
        private var index = 0
    }

    constructor() : this(++index)
}