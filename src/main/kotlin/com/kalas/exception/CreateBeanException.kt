package com.kalas.exception

class CreateBeanException : BeansException {
    constructor(message: String) : super(message) {

    }

    constructor(message: String, cause: Throwable) : super(message, cause) {

    }
}
