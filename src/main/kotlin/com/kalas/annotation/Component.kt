package com.kalas.annotation

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Component(val value: String = "", val proxy: Boolean = false)
