package com.kls.util

import com.kls.exception.ScannerClassException
import java.io.IOException
import java.net.JarURLConnection
import java.util.*
import java.util.function.Predicate


class JarScanner : Scanner {
    override fun scan(packageName: String, predicate: Predicate<Class<*>>?): Set<Class<*>> {
        val classes: MutableSet<Class<*>> = HashSet()
        try {
            //通过当前线程得到类加载器从而得到URL的枚举
            val urlEnumeration = Thread.currentThread().contextClassLoader.getResources(packageName.replace(".", "/"))
            while (urlEnumeration.hasMoreElements()) {
                val url = urlEnumeration.nextElement()
                val protocol = url.protocol
                if ("jar".equals(protocol, ignoreCase = true)) {
                    //转换为JarURLConnection
                    val connection = url.openConnection() as JarURLConnection
                    val jarFile = connection.jarFile
                    //得到该jar文件下面的类实体
                    val jarEntryEnumeration = jarFile.entries()
                    while (jarEntryEnumeration.hasMoreElements()) {
                        val entry = jarEntryEnumeration.nextElement()
                        val jarEntryName = entry.name
                        //这里我们需要过滤不是class文件和不在basePack包名下的类
                        if (jarEntryName.contains(".class") &&
                            jarEntryName.replace("/".toRegex(), ".").startsWith(packageName)
                        ) {
                            val className =
                                jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replace("/", ".")
                            val cls = Class.forName(className)
                            if (predicate == null || predicate.test(cls)) {
                                classes.add(cls)
                            }
                        }
                    }
                } else if ("file".equals(protocol, ignoreCase = true)) {
                    val fileScanner = FileScanner(url.path.replace(packageName.replace(".", "/"), ""))
                    classes.addAll(fileScanner.scan(packageName, predicate))
                }
            }
        } catch (e: ClassNotFoundException) {
            throw ScannerClassException(e.message!!, e)
        } catch (e: IOException) {
            throw ScannerClassException(e.message!!, e)
        }
        return classes
    }
}
