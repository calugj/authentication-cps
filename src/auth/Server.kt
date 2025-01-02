package auth
import kotlin.random.Random
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@kotlin.ExperimentalUnsignedTypes
class Server(val ID: UByteArray, val vault: MutableList<UByteArray>) {
    private var inbound: MutableList<Message> = mutableListOf()
    private var outbound: MutableList<Message> = mutableListOf()
    private var step = 0

    private var r1 = UByteArray(m)
    private var k1 = UByteArray(m)

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

    fun operate() {
        println("server.operate()\ninbound:${inbound.size}\noutbound:${outbound.size}\n")
        if(step == 0 && inbound.size >= 1) {
            val message = inbound[0]
            if(message.source.contentEquals(message.payload[0])) {
                step++
                println("step 1 completed")
                
                val C1 = ubyteArrayOf(0u, 2u)
                r1 = UByteArray(m) { Random.nextInt(0, 256).toUByte() }
                val message = Message(ID, message.source, mutableListOf(C1, r1))
                sendMessage(message)
                step++


                var flag = false
                for(i in C1) {
                    if(!flag) {
                        k1 = vault[i.toInt()]
                        flag = true
                    } else {
                        k1 = UByteArray(k1.size) { index ->
                            (k1[index].toInt() xor vault[i.toInt()][index].toInt()).toUByte()
                        }
                    }
                }
                
            }
        }
        else if(step == 2 && inbound.size >= 1) {
            val message = inbound[0]
            
            val decryption = decrypt(message.payload[0], k1)
            println("decryption: ${decryption.joinToString()}")

            val retrieved_r1 = decryption.copyOfRange(0, m)
            if(r1.contentEquals(retrieved_r1)) {
                step++

                val t1 = decryption.copyOfRange(m, m*2)
                
                val C2 = decryption.copyOfRange(m*2, decryption.size - m)
                println("C2: ${C2.joinToString()}")

                val r2 = decryption.copyOfRange(decryption.size - m, decryption.size)



                var k2: UByteArray? = null
                for(i in C2) {
                    if(k2 == null) {
                        k2 = vault[i.toInt()]
                    } else {
                        k2 = UByteArray(k2.size) { index ->
                            (k2[index].toInt() xor vault[i.toInt()][index].toInt()).toUByte()
                        }
                    }
                }
                k2 = UByteArray(k2!!.size) { index ->
                    (k2[index].toInt() xor t1[index].toInt()).toUByte()
                }

                val t2 = UByteArray(m) { Random.nextInt(0, 256).toUByte() }

                val r2conct2 = UByteArray(r2.size + t2.size)
                r2.copyInto(r2conct2, 0)
                t2.copyInto(r2conct2, r2.size)


                val M4 = encrypt(r2conct2, k2)
                val nmessage = Message(ID, message.source, mutableListOf(M4))
                sendMessage(nmessage)

                println("step 3 completed")
                step++

            }

            
        }

        removeInbound()
        println("server.operate() end\ninbound:${inbound.size}\noutbound:${outbound.size}\n")

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