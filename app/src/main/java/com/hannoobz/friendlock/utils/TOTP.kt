package com.hannoobz.friendlock.utils

import java.nio.ByteBuffer
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object TOTP {
    private const val TIME_STEP_SECONDS = 30L
    private const val OTP_DIGITS = 6
    private const val HMAC_ALGORITHM = "HmacSHA1"

    fun generate(secret: String, time: Long = System.currentTimeMillis()): String {
        val key = base32Decode(secret)
        val steps = time / 1000 / TIME_STEP_SECONDS
        val data = ByteBuffer.allocate(8).putLong(steps).array()

        val mac = Mac.getInstance(HMAC_ALGORITHM)
        mac.init(SecretKeySpec(key, HMAC_ALGORITHM))
        val hmac = mac.doFinal(data)

        val offset = hmac.last().toInt() and 0x0F
        val binary =
            ((hmac[offset].toInt() and 0x7F) shl 24) or
                    ((hmac[offset + 1].toInt() and 0xFF) shl 16) or
                    ((hmac[offset + 2].toInt() and 0xFF) shl 8) or
                    (hmac[offset + 3].toInt() and 0xFF)

        val otp = binary % 10.0.pow(OTP_DIGITS).toInt()
        return otp.toString().padStart(OTP_DIGITS, '0')
    }

    private fun base32Decode(base32: String): ByteArray {
        val base32Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"
        val cleaned = base32.trim().replace("=", "").uppercase()
        val bits = StringBuilder()

        for (c in cleaned) {
            val value = base32Chars.indexOf(c)
            require(value != -1) { "Invalid Base32 character: $c" }
            bits.append(value.toString(2).padStart(5, '0'))
        }

        val bytes = mutableListOf<Byte>()
        for (i in bits.indices step 8) {
            if (i + 8 <= bits.length) {
                val byte = bits.substring(i, i + 8).toInt(2)
                bytes.add(byte.toByte())
            }
        }
        return bytes.toByteArray()
    }

    private fun Double.pow(exp: Int): Double = Math.pow(this, exp.toDouble())
}

