package auth
import kotlin.random.Random

import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@kotlin.ExperimentalUnsignedTypes
class Device(val ID: UByteArray, val vault: MutableList<UByteArray>) {
    private var inbound: MutableList<Message> = mutableListOf()
    private var outbound: MutableList<Message> = mutableListOf()
    private var step = 0


    private var t1 = UByteArray(m)
    private var k2 = UByteArray(m)
    private var r2 = UByteArray(m)


    init {
        
    }

    fun sendMessage(message: Message) {
        outbound.add(message)
    }

    fun receiveMessage(message: Message) {
        inbound.add(message)
    }

    fun removeInbound() {
        inbound = mutableListOf()
    }

    fun removeOutbound() {
        outbound = mutableListOf()
    }

    fun getInbound(): MutableList<Message> {
        return inbound;
    }

    fun getOutbound(): MutableList<Message> {
        return outbound;
    }

    fun initiateAuthentication() {
        val sessionID = ubyteArrayOf(0u)
        val message = Message(ID, ubyteArrayOf(255u), mutableListOf(ID, sessionID))

        sendMessage(message)

        step = 1;
    }

    fun operate() {
        println("device.operate()\ninbound:${inbound.size}\noutbound:${outbound.size}\n")

        if(step == 1 && inbound.size >= 1) {
            step++
            val message = inbound[0]

            var k1: UByteArray? = null
            for(i in message.payload[0]) {
                if(k1 == null) {
                    k1 = vault[i.toInt()]
                } else {
                    k1 = UByteArray(k1.size) { index ->
                        (k1[index].toInt() xor vault[i.toInt()][index].toInt()).toUByte()
                    }
                }
            }
            if(k1 == null) {
                k1 = UByteArray(m)
            }

            println("step 2 completed")


            val r1 = message.payload[1]
            //println(r1.joinToString())
            t1 = UByteArray(m) { Random.nextInt(0, 256).toUByte() }
            //println(t1.joinToString())

            val r1conct1 = UByteArray(r1.size + t1.size)
            r1.copyInto(r1conct1, 0)
            t1.copyInto(r1conct1, r1.size)
            //println("r1conct1: ${r1conct1.joinToString()}")


            val C2 = ubyteArrayOf(1u, 3u)

            for(i in C2) {
                if(k2 == null) {
                    k2 = vault[i.toInt()]
                } else {
                    k2 = UByteArray(k2.size) { index ->
                        (k2[index].toInt() xor vault[i.toInt()][index].toInt()).toUByte()
                    }
                }
            }
            
            


            r2 = UByteArray(m) { Random.nextInt(0, 256).toUByte() }
            //println("r2: ${r2.joinToString()}")

            val payload = UByteArray(r1conct1.size + C2.size + r2.size)
            r1conct1.copyInto(payload, 0)
            C2.copyInto(payload, r1conct1.size)
            r2.copyInto(payload, r1conct1.size + C2.size)
            println("payload: ${payload.joinToString()}")


            val encryption = encrypt(payload, k1)
            //println("encryption: ${encryption.joinToString()}")

            val decryption = decrypt(encryption, k1)
            //println("decryption: ${decryption.joinToString()}")

            val nmessage = Message(ID, message.source, mutableListOf(encryption))
            //println(nmessage.destination.joinToString())
            sendMessage(nmessage)
            step++
        }
        else if(step == 3 && inbound.size >= 1) {
            val M4 = inbound[0].payload[0]
            val k2xort1 = UByteArray(k2.size) { index ->
                (k2[index].toInt() xor t1[index].toInt()).toUByte()
            }
            val decrypted = decrypt(M4, k2xort1)

            val obtained_r2 = decrypted.copyOfRange(0, m)

            if(obtained_r2.contentEquals(r2)) {
                print("SIIIIIIIIIIIIIIIIIIII")
            }


            
        }


        removeInbound()
        println("device.operate() end\ninbound:${inbound.size}\noutbound:${outbound.size}\n")

    }

    private fun encrypt(message: UByteArray, key: UByteArray): UByteArray {
        val secretKey = SecretKeySpec(key.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedBytes = cipher.doFinal(message.toByteArray())
        return encryptedBytes.toUByteArray()
    }

    // Decrypt a UByteArray message using a UByteArray key
    private fun decrypt(encryptedMessage: UByteArray, key: UByteArray): UByteArray {
        val secretKey = SecretKeySpec(key.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val decryptedBytes = cipher.doFinal(encryptedMessage.toByteArray())
        return decryptedBytes.toUByteArray()
    }
}