package com.kls.util

import java.util.function.Predicate

interface Scanner {
    fun scan(packageName: String, predicate: Predicate<Class<*>>?): Set<Class<*>>

    fun scan(packageName: String): Set<Class<*>> {
        return scan(packageName, null)
    }

}
