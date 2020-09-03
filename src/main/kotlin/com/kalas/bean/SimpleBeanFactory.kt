package com.kalas.bean

import com.kalas.exception.BeansException
import com.kalas.exception.CreateBeanException
import com.kalas.exception.GetBeanException

class SimpleBeanFactory : BeanFactory {
    private val container: MutableMap<BeanDefinition, Any> = mutableMapOf()

    override fun beans(): Set<Any> {
        return container.values.toSet()
    }

    @Throws(BeansException::class)
    override fun getBean(name: String, isProxy: Boolean): Any {
        val definition = container.keys.find { it.name == name && it.isProxy == isProxy }
        definition ?: throw GetBeanException("Didn't find bean. Name: $name")
        return container[definition]!!
    }

    @Throws(BeansException::class)
    override fun <T> getBean(name: String, requiredType: Class<T>, isProxy: Boolean): T {
        val definition = container.keys.find { it == BeanDefinition(name, requiredType, isProxy) }
        definition ?: throw GetBeanException("Didn't find bean. Name: $name, Type: ${requiredType.name}")
        return container[definition] as T
    }

    @Throws(BeansException::class)
    override fun <T> getBean(requiredType: Class<T>, isProxy: Boolean): T {
        val definition = container.keys.find { it.type == requiredType && it.isProxy == isProxy }
        definition ?: throw GetBeanException("Didn't find bean. Type: ${requiredType.name}")
        return container[definition] as T
    }

    @Throws(BeansException::class)
    override fun createBean(definition: BeanDefinition, bean: Any) {
        if (container.keys.contains(definition)) {
            throw CreateBeanException("Bean already exists.")
        }
        container.put(definition, bean)
    }

}
