package com.kls.util

import java.util.function.Predicate

object PackageScanner {
    fun scan(packageName: String, predicate: Predicate<Class<*>>?): Set<Class<*>> {
        val fileScanner: Scanner = FileScanner()
        val fileScan: Set<Class<*>> = fileScanner.scan(packageName, predicate)
        val jarScanner: Scanner = JarScanner()
        val jarScan: Set<Class<*>> = jarScanner.scan(packageName, predicate)
        return fileScan.plus(jarScan)
    }

    fun scan(packageName: String): Set<Class<*>> {
        return scan(packageName, null)
    }

}
