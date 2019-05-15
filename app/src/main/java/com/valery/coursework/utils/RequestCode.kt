package com.valery.coursework.utils

enum class RequestCode(val code: Int) {

    DEFAULT,
    PICK_IMAGE,
    RUN_CAMERA;

    companion object {
        private var index = 0

        fun toRequestCode(code: Int): RequestCode? {
            return values().firstOrNull { it.code == code }
        }
    }

    constructor() : this(++index)
}