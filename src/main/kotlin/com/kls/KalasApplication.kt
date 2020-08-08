package com.kls

import com.kls.annotation.Bean
import com.kls.annotation.Component
import com.kls.annotation.Inject
import com.kls.bean.BeanDefinition
import com.kls.bean.BeanFactory
import com.kls.bean.SimpleBeanFactory
import com.kls.exception.CreateBeanException
import com.kls.util.PackageScanner
import java.lang.reflect.Field

object KalasApplication {

    lateinit var beanFactory: BeanFactory
    lateinit var startClass: Class<*>
    val preInjectBeans: MutableSet<Any> = hashSetOf()

    fun run(startClass: Class<*>) {
        prepareBeanFactory(startClass)
    }

    fun prepareBeanFactory(startClass: Class<*>) {
        this.startClass = startClass
        this.beanFactory = SimpleBeanFactory()
        createBeansFromPackageScan()
        createBeansFromMethodScan()
        inject()
    }

    fun createBeansFromPackageScan() {
        // 包扫描，管理有Component注解的
        val classes = PackageScanner.scan(startClass.`package`.name)
        classes.asSequence()
            .filter { it.getAnnotation(Component::class.java) != null }
            .forEach {
                val component = it.getAnnotation(Component::class.java)
                val definition = BeanDefinition(component.value, it, component.proxy)
                try {
                    val bean = it.getConstructor().newInstance()
                    beanFactory.createBean(definition, bean)
                } catch (e: NoSuchMethodException) {
                    throw CreateBeanException("Bean must have a no arg open constructor, Name: ${component.value}, Type: ${it.name}")
                } catch (e: SecurityException) {
                    throw CreateBeanException("Bean must have a no arg open constructor, Name: ${component.value}, Type: ${it.name}")
                }
            }
    }

    fun createBeansFromMethodScan() {
        val beans = beanFactory.beans()
        beans.asSequence().forEach { bean ->
            val chain = findDependencyChain(bean)
            preInject(chain)
            bean.javaClass.methods
                .filter { it.getAnnotation(Bean::class.java) != null }
                .forEach {
                    val beanAnnotation = it.getAnnotation(Bean::class.java)
                    val definition = BeanDefinition(beanAnnotation.value, it.returnType, beanAnnotation.proxy)
                    beanFactory.createBean(definition, it.invoke(bean))
                }
        }
    }

    fun findDependencyChain(bean: Any): Set<Any> {
        // todo
        bean.javaClass.fields
        return setOf()
    }

    fun preInject(chain: Set<Any>) {
        chain.forEach { bean ->
            bean.javaClass.fields
                .filter { it.getAnnotation(Inject::class.java) != null }
                .forEach { injectField(it, bean) }
            preInjectBeans.add(bean)
        }
    }

    fun inject() {
        val beans = beanFactory.beans()
        beans.asSequence()
            .filter { !preInjectBeans.contains(it) }
            .forEach { bean ->
                bean.javaClass.fields
                    .filter { it.getAnnotation(Inject::class.java) != null }
                    .forEach { injectField(it, bean) }
            }
    }

    fun injectField(field: Field, obj: Any) {
        val inject = field.getAnnotation(Inject::class.java)
        if (inject.value != "") {
            field.set(obj, beanFactory.getBean(inject.value, field.declaringClass, inject.proxy))
        } else {
            field.set(obj, beanFactory.getBean(field.declaringClass, inject.proxy))
        }
    }
}
