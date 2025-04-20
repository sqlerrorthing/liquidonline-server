package `fun`.sqlerrorthing.liquidonline.ws.listener

import `fun`.sqlerrorthing.liquidonline.extensions.sendMessage
import `fun`.sqlerrorthing.liquidonline.packets.Packet
import `fun`.sqlerrorthing.liquidonline.services.WebSocketSessionStorageService
import `fun`.sqlerrorthing.liquidonline.session.UserSession
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.SmartInitializingSingleton
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketSession
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.isAccessible

@Component
class PacketListenerRegistrar(
    private val applicationContext: ApplicationContext
) : SmartInitializingSingleton {
    private val listeners = mutableMapOf<KClass<out Packet>, ListenerMethod>()

    @Suppress("UNCHECKED_CAST")
    override fun afterSingletonsInstantiated() {
        val beans = applicationContext.getBeansWithAnnotation(WebSocketMessageListener::class.java)

        beans.values.forEach { bean ->
            bean::class.members.forEach { method ->
                method.findAnnotation<PacketMessageListener>()?.let { _ ->
                    method.parameters.find { it.type.classifier is KClass<*> && (it.type.classifier as KClass<*>).isSubclassOf(Packet::class) }?.let { packetArg ->
                        val packetType = packetArg.type.classifier as KClass<out Packet>
                        method.isAccessible = true
                        listeners[packetType] = ListenerMethod(bean, method, packetType)
                    }
                }
            }
        }
    }

    fun dispatchPacket(userSession: UserSession, packet: Packet) {
        val listener = listeners[packet::class] ?: listeners[Packet::class] ?: return
        val result = listener.method.call(listener.bean, userSession, packet)

        if (result is Packet) {
            userSession.sendMessage(result)
        }
    }

    private data class ListenerMethod(
        val bean: Any,
        val method: KCallable<*>,
        val packetType: KClass<out Packet>
    )
}