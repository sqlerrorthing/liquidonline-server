package `fun`.sqlerrorthing.liquidonline.services.user

import `fun`.sqlerrorthing.liquidonline.entities.UserEntity
import org.springframework.stereotype.Service
import java.security.SecureRandom
import java.time.Instant
import java.util.*

@Service
class SimpleTokenServiceImpl : TokenService {
    override fun generateToken(userEntity: UserEntity): String {
        val first = userEntity.id.encodeToBase64WithoutEq()

        val middle = Instant.now().epochSecond.encodeToBase64WithoutEq()

        val last = SecureRandom().let {
            val randomBytes = ByteArray(32)
            it.nextBytes(randomBytes)
            randomBytes.encodeArrayToBase64WithoutEq()
        }

        return "$first.$middle.$last"
    }

    private fun ByteArray.encodeArrayToBase64WithoutEq(): String {
        return Base64.getEncoder().encodeToString(this)
            .replace("=", "")
    }

    private fun <T> T.encodeToBase64WithoutEq(): String {
        val bytes = this.toString().toByteArray()
        return bytes.encodeArrayToBase64WithoutEq()
    }
}
