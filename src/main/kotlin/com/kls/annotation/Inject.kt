package com.kls.annotation


@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class Inject(val proxy: Boolean = true)