package com.hannoobz.friendlock.utils

import java.security.SecureRandom

fun generateRandomSecret(length: Int = 6): String {
    val base32Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"
    val random = SecureRandom()
    return (1..length)
        .map { base32Chars[random.nextInt(base32Chars.length)] }
        .joinToString("")
}