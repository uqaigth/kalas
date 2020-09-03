package com.kalas.annotation

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Bean(val value: String = "", val proxy: Boolean = false)
