package com.kls.bean


class BeanDefinition(val name: String, val type: Class<*>, val isProxy: Boolean) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BeanDefinition

        if (name != other.name) return false
        if (type != other.type) return false
        if (isProxy != other.isProxy) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + isProxy.hashCode()
        return result
    }
}
