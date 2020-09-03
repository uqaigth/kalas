package com.kalas.annotation


@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class Inject(val value: String = "", val proxy: Boolean = false)
