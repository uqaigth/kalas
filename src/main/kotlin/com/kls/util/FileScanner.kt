package com.kls.util

import com.kls.exception.ScannerClassException
import java.io.File
import java.util.*
import java.util.function.Predicate


class FileScanner() : Scanner {
    var defaultClassPath: String = FileScanner::class.java.getResource("/").path

    constructor(defaultClassPath: String) {
        this.defaultClassPath = defaultClassPath
    }

    private class ClassSearcher {
        val classSuffix = ".class"
        private val classPaths: MutableSet<Class<*>> = HashSet()

        fun doFile(file: File, packageName: String, predicate: Predicate<Class<*>>?) {
            if (file.name.endsWith(classSuffix)) {
                try {
                    val clazz = Class.forName(
                        packageName + "." + file.name.substring(0, file.name.lastIndexOf("."))
                    )
                    if (predicate == null || predicate.test(clazz)) {
                        classPaths.add(clazz)
                    }
                } catch (e: ClassNotFoundException) {
                    throw ScannerClassException(e.message!!, e)
                }
            }
        }

        fun doDir(file: File, packageName: String, predicate: Predicate<Class<*>>?) {
            val files = file.listFiles()
            files?.forEach {
                if (it.isDirectory)
                    doDir(it, "$packageName,${it.name}", predicate)
                else
                    doFile(file, packageName, predicate)
            }
        }

        fun doPath(file: File, packageName: String, predicate: Predicate<Class<*>>?): Set<Class<*>> {
            if (file.isDirectory)
                doDir(file, packageName, predicate)
            else
                doFile(file, packageName, predicate)
            return classPaths
        }
    }

    override fun scan(packageName: String, predicate: Predicate<Class<*>>?): Set<Class<*>> {
        //然后把我们的包名basPack转换为路径名
        val basePackPath = packageName.replace(".", File.separator)
        val searchPath = defaultClassPath + basePackPath
        return ClassSearcher().doPath(File(searchPath), packageName, predicate)
    }

}
