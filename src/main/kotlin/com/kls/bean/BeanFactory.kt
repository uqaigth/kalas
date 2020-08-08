package com.kls.bean

import com.kls.exception.BeansException

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
}

