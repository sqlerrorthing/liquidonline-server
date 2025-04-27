package `fun`.sqlerrorthing.liquidonline.config.jackson.introspectors

import com.fasterxml.jackson.databind.PropertyName
import com.fasterxml.jackson.databind.introspect.Annotated
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector
import `fun`.sqlerrorthing.liquidonline.packets.SerializedName

class AnnotationIntrospector : JacksonAnnotationIntrospector() {
    override fun findNameForDeserialization(a: Annotated?): PropertyName? {
        return _findAnnotation(a, SerializedName::class.java)
            ?.let { PropertyName.construct(it.value) }
            ?: super.findNameForDeserialization(a)
    }

    override fun findNameForSerialization(a: Annotated?): PropertyName? {
        return _findAnnotation(a, SerializedName::class.java)
            ?.let { PropertyName.construct(it.value) }
            ?: super.findNameForDeserialization(a)
    }
}