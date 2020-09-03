package com.kalas

import com.kalas.annotation.Bean
import com.kalas.annotation.Component
import com.kalas.annotation.Inject
import com.kalas.bean.BeanDefinition
import com.kalas.bean.BeanFactory
import com.kalas.bean.SimpleBeanFactory
import com.kalas.exception.CreateBeanException
import com.kalas.util.PackageScanner
import java.lang.reflect.Field
import kotlin.reflect.KClass

object KalasApplication {

    lateinit var beanFactory: BeanFactory
    lateinit var startClass: Class<*>
    private val preInjectBeans: MutableSet<Any> = hashSetOf()

    fun run(startClass: Class<*>) {
        prepareBeanFactory(startClass)
    }

    fun run(startClass: KClass<*>) {
        prepareBeanFactory(startClass.java)
    }

    private fun prepareBeanFactory(startClass: Class<*>) {
        this.startClass = startClass
        this.beanFactory = SimpleBeanFactory()
        createBeansFromPackageScan()
        createBeansFromMethodScan()
        inject()
    }

    private fun createBeansFromPackageScan() {
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

    private fun createBeansFromMethodScan() {
        val beans = beanFactory.beans()
        beans.asSequence().forEach { bean ->
            preInject(bean)
            bean.javaClass.methods
                .filter { it.getAnnotation(Bean::class.java) != null }
                .forEach {
                    val beanAnnotation = it.getAnnotation(Bean::class.java)
                    val definition = BeanDefinition(beanAnnotation.value, it.returnType, beanAnnotation.proxy)
                    beanFactory.createBean(definition, it.invoke(bean))
                }
        }
    }

    private fun preInject(bean: Any) {
        bean.javaClass.fields
            .filter { it.getAnnotation(Inject::class.java) != null }
            .forEach {
                var fieldBean = it.get(bean)
                if (fieldBean == null) {
                    fieldBean = injectField(it, bean)
                }
                if (!preInjectBeans.contains(fieldBean)) {
                    preInject(fieldBean)
                }
            }
        preInjectBeans.add(bean)
    }

    private fun inject() {
        val beans = beanFactory.beans()
        beans.asSequence()
            .filter { !preInjectBeans.contains(it) }
            .forEach { bean ->
                bean.javaClass.fields
                    .filter { it.getAnnotation(Inject::class.java) != null }
                    .forEach { injectField(it, bean) }
            }
    }

    private fun injectField(field: Field, obj: Any): Any {
        val inject = field.getAnnotation(Inject::class.java)
        val bean: Any = if (inject.value != "") {
            beanFactory.getBean(inject.value, field.type, inject.proxy)
        } else {
            beanFactory.getBean(field.type, inject.proxy)
        }
        field.set(obj, bean)
        return bean
    }
}
