package auth
import kotlin.random.Random
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@kotlin.ExperimentalUnsignedTypes
open class Device(open val ID: UByteArray, open val vault: MutableList<MutableList<UByteArray>>) {
    protected var inboundMessages: MutableList<Message> = mutableListOf()
    protected var outboundMessages: MutableList<Message> = mutableListOf()
    protected var step = 0

    fun sendMessage(message: Message) {
        outboundMessages.add(message)
        println("Message sent. Description:\n${message}")
    }

    fun receiveMessage(message: Message) {
        inboundMessages.add(message)
    }

    fun removeInbound() {
        inboundMessages = mutableListOf()
    }

    fun removeOutbound() {
        outboundMessages = mutableListOf()
    }

    fun getInbound(): MutableList<Message> {
        return inboundMessages;
    }

    fun getOutbound(): MutableList<Message> {
        return outboundMessages;
    }




    open fun initiateAuthentication() {}

    open fun operate() {}
    
    open fun deauthenticate() {}

    protected fun encrypt(message: UByteArray, key: UByteArray): UByteArray {
        val secretKey = SecretKeySpec(key.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedBytes = cipher.doFinal(message.toByteArray())
        return encryptedBytes.toUByteArray()
    }

    protected fun decrypt(encryptedMessage: UByteArray, key: UByteArray): UByteArray {
        val secretKey = SecretKeySpec(key.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val decryptedBytes = cipher.doFinal(encryptedMessage.toByteArray())
        return decryptedBytes.toUByteArray()
    }

    protected fun generateUByteArray(size: Int): UByteArray {
        return UByteArray(size) { Random.nextInt(0, 256).toUByte() }
    }

    override fun toString(): String {
        var device = ""
        if(ID.contentEquals(ubyteArrayOf(255.toUByte()))) device += "[SERVER]"
        else device += "[IoT]"
        device += "\tID: ${ID.joinToString()}"
        return device
    }
}