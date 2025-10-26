package ge.nikka.gtutables

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.inputmethod.InputMethodManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.net.Socket
import java.nio.charset.StandardCharsets
import java.util.*


class Utils {
    companion object {

        fun isInternetAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }

        fun dismissKeyboard(activity: Activity) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (null != activity.getCurrentFocus()) imm.hideSoftInputFromWindow(
                activity.getCurrentFocus()!!.getApplicationWindowToken(), 0
            )
        }

        fun writeTextToFile(context: Context, fileName: String, text: String) {
            val file = File(context.filesDir, fileName)
            file.writeText(text)
        }
        fun stringToHex(text: String?): String {
            val bytes = text?.toByteArray(StandardCharsets.UTF_8)
            val hexString = java.lang.StringBuilder()
            if (bytes != null) {
                for (b in bytes) {
                    hexString.append(String.format("%02x", b))
                }
            }
            return hexString.toString()
        }

        fun hexToString(hex: String?): String {
            val bytes = ByteArray(hex?.length?.div(2)!!)
            var i = 0
            while (i < hex.length) {
                bytes[i / 2] = hex.substring(i, i + 2).toInt(16).toByte()
                i += 2
            }
            return String(bytes, StandardCharsets.UTF_8)
        }

        fun encrypt(input: String, key: String): String {
            if (key.isEmpty()) return input
            val sb = StringBuilder(input.length)
            for (i in input.indices) {
                val shift = key[i % key.length].code and 0xFFFF
                val encryptedChar = (input[i].code + shift) and 0xFFFF
                sb.append(encryptedChar.toChar())
            }
            return sb.toString()
        }

        fun decrypt(input: String, key: String): String {
            if (key.isEmpty()) return input
            val sb = StringBuilder(input.length)
            for (i in input.indices) {
                val shift = key[i % key.length].code and 0xFFFF
                val decryptedChar = (input[i].code - shift) and 0xFFFF
                sb.append(decryptedChar.toChar())
            }
            return sb.toString()
        }

        suspend fun sendAndReceive(messageToSend: String): String? = withContext(Dispatchers.IO) {
            var tempSocket: Socket? = null
            try {
                tempSocket = Socket("gtu.localto.net", 5000)

                val outStream = DataOutputStream(tempSocket.getOutputStream())
                val inStream = DataInputStream(tempSocket.getInputStream())

                val messageBytes = encrypt(messageToSend, "table").toByteArray(Charsets.UTF_8)
                val sizeBytes = messageBytes.size.toString().toByteArray(Charsets.UTF_8)
                outStream.write(sizeBytes)
                outStream.write("L".toByteArray(Charsets.UTF_8))
                outStream.write(messageBytes)
                outStream.flush()

                var dsize = ""
                val buffer = ByteArray(1)
                var bytesRead: Int
                while (inStream.read(buffer).also { bytesRead = it } != -1) {
                    val charRead = String(buffer, 0, 1)
                    if (charRead == "L") break
                    dsize += charRead
                }

                val messageSize = dsize.toIntOrNull() ?: return@withContext null
                val messageBytesResponse = ByteArray(messageSize)
                inStream.readFully(messageBytesResponse)
                decrypt(String(messageBytesResponse, Charsets.UTF_8), "table")
            } catch (e: Exception) {
                e.toString()
            } finally {
                tempSocket?.close()
            }
        }

    }
}
