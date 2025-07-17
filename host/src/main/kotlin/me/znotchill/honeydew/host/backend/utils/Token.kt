package me.znotchill.honeydew.host.backend.utils

import java.security.SecureRandom
import java.util.Base64

fun generateSecureToken(length: Int = 64): String {
    val bytes = ByteArray(length)
    SecureRandom().nextBytes(bytes)
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
}