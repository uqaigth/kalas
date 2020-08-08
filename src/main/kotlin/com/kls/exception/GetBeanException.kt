package com.kls.exception

class GetBeanException : BeansException {
    constructor(message: String) : super(message) {

    }

    constructor(message: String, cause: Throwable) : super(message, cause) {

    }
}
