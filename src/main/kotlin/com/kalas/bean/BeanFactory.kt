package com.kalas.bean

import com.kalas.exception.BeansException
import kotlin.reflect.KClass

interface BeanFactory {
    @Throws(BeansException::class)
    fun beans(): Set<Any>

    @Throws(BeansException::class)
    fun getBean(name: String, isProxy: Boolean = false): Any

    @Throws(BeansException::class)
    fun <T> getBean(name: String, requiredType: Class<T>, isProxy: Boolean = false): T

    @Throws(BeansException::class)
    fun <T> getBean(requiredType: Class<T>, isProxy: Boolean = false): T

    @Throws(BeansException::class)
    fun createBean(definition: BeanDefinition, bean: Any)

    @Throws(BeansException::class)
    fun <T : Any> getBean(name: String, requiredType: KClass<T>, isProxy: Boolean = false): T {
        return getBean(name, requiredType.java, isProxy)
    }

    @Throws(BeansException::class)
    fun <T : Any> getBean(requiredType: KClass<T>, isProxy: Boolean = false): T {
        return getBean(requiredType.java, isProxy)
    }
}
