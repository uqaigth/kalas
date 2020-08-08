package com.kls.exception

open class BeansException : RuntimeException {

    constructor(message: String) : super(message) {

    }

    constructor(message: String, cause: Throwable) : super(message, cause) {

    }
}
