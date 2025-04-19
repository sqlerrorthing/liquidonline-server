package `fun`.sqlerrorthing.liquidonline.ws.listener

import `fun`.sqlerrorthing.liquidonline.packets.Packet
import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class PacketMessageListener