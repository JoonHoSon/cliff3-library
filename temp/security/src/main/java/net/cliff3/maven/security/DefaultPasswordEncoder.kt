package net.cliff3.maven.security

import org.springframework.security.crypto.password.PasswordEncoder
import net.cliff3.maven.common.util.crypto.CryptoUtil
import java.util.*

/**
 * DefaultPasswordEncoder
 *
 * @author JoonHo Son
 * @since 1.0.0
 */
class DefaultPasswordEncoder: PasswordEncoder {
    override fun encode(p0: CharSequence?): String {
        return super.en
    }

    override fun matches(p0: CharSequence?, p1: String?): Boolean {
//        TODO("Not yet implemented")
        return false
    }

    private fun encodePassword(plainPassword: String): String? {
        val result: Optional<ByteArray> = CryptoUtil.makeSHA256Hash(plainPassword)

        return result.get
    }
}