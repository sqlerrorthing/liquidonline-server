package `fun`.sqlerrorthing.liquidonline.utils

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component


@Component
class SpringContextHolder : ApplicationContextAware {
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        context = applicationContext
    }

    companion object {
        private var context: ApplicationContext? = null

        fun <T> getBean(requiredType: Class<T>): T? {
            return context?.getBean(requiredType)
        }
    }
}
