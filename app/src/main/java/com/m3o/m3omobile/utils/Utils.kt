package com.m3o.m3omobile.utils

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.Color
import android.util.Base64
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

internal const val ACCESS_TOKEN = "access_token"
internal const val API_KEY = "key"
internal const val EMAIL = "email"
internal const val SHARED_PREFERENCE = "Safe"
internal const val USER_ID = "user_id"

internal fun Context.showToast(message: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, length).show()
}

internal fun Fragment.showToast(message: String, length: Int = Toast.LENGTH_SHORT) {
    requireContext().showToast(message, length)
}

internal fun Context.storeToClipboard(label: String, text: String) {
    val clip = ClipData.newPlainText(label, text)
    (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
        .setPrimaryClip(clip)
}

internal fun Fragment.storeToClipboard(label: String, text: String) {
    requireContext().storeToClipboard(label, text)
}

internal fun getServiceIcon(
    context: Context,
    svgPath: String
) = VectorDrawableCreator.getVectorDrawable(
    context,
    100,
    100,
    24f,
    24f,
    true,
    0f,
    0f,
    listOf(VectorDrawableCreator.PathData(
        svgPath,
        Color.TRANSPARENT,
        Color.parseColor("#F687B3") // pink-400
    ))
)

internal fun closeKeyboard(context: Context) {
    val view = (context as Activity).currentFocus
    if (view != null) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

internal object Safe {
    private const val CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding"
    private const val ENCRYPTION_ALGORITHM = "AES"
    private const val HASH_ALGORITHM = "SHA-256"
    private const val IV_LENGTH = 16

    internal fun encryptAndStoreAccessToken(context: Context, accessToken: String) {
        encryptAndStore(context, ACCESS_TOKEN, accessToken)
    }

    internal fun encryptAndStoreUserId(context: Context, userId: String) {
        encryptAndStore(context, USER_ID, userId)
    }

    internal fun encryptAndStoreApiKey(context: Context, apiKey: String) {
        encryptAndStore(context, API_KEY, apiKey)
    }

    private fun encryptAndStore(
        context: Context,
        preference: String,
        value: String
    ) {
        val editor = context.getSharedPreferences(SHARED_PREFERENCE, MODE_PRIVATE).edit()

        if (value != "") {
            try {
                val secretKey = generateKey(Secrets.getCipherPassword())
                val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, IvParameterSpec(ByteArray(IV_LENGTH)))
                val encryptedValue = cipher.doFinal(value.toByteArray(Charsets.UTF_8))
                val encryptedApiKey = Base64.encodeToString(encryptedValue, Base64.DEFAULT)
                editor.putString(preference, encryptedApiKey).commit()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            editor.putString(preference, "").commit()
        }
    }

    internal fun getAndDecryptAccessToken(context: Context) = getAndDecrypt(context, ACCESS_TOKEN)

    internal fun getAndDecryptUserId(context: Context) = getAndDecrypt(context, USER_ID)

    internal fun getAndDecryptApiKey(context: Context) = getAndDecrypt(context, API_KEY)

    private fun getAndDecrypt(context: Context, preference: String): String {
        val spf = context.getSharedPreferences(SHARED_PREFERENCE, MODE_PRIVATE)
        val encryptedValue = spf.getString(preference, "")!!
        if (encryptedValue.isEmpty()) return ""
        val secretKey = generateKey(Secrets.getCipherPassword())
        val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(ByteArray(IV_LENGTH)))
        val decryptedBase64Value = Base64.decode(encryptedValue, Base64.DEFAULT)
        val decryptedValue = cipher.doFinal(decryptedBase64Value)
        return String(decryptedValue, Charsets.UTF_8)
    }

    private fun generateKey(password: String): SecretKeySpec {
        val digest = MessageDigest.getInstance(HASH_ALGORITHM)
        val bytes = password.toByteArray(Charsets.UTF_8)
        digest.update(bytes, 0, bytes.size)
        val key = digest.digest()
        return SecretKeySpec(key, ENCRYPTION_ALGORITHM)
    }
}
