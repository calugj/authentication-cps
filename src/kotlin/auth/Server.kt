package auth
import kotlin.random.Random

@kotlin.ExperimentalUnsignedTypes
class Server(override val ID: UByteArray, override val vault: MutableList<MutableList<UByteArray>>): Device(ID, vault) {

    private var r1 = UByteArray(m)
    private var k1 = UByteArray(m)
    private var t1 = UByteArray(m)
    private var t2 = UByteArray(m)
    private val table: MutableList<MutableList<UByteArray>> = mutableListOf()

    override fun operate() {
        if(step == 0 && getInbound().size >= 1) {
            val received = getInbound()[0]
            if(received.source.contentEquals(received.payload[0])) {

                val list: MutableList<Int> = mutableListOf()
                for(index in 0..n-1)
                    if(Random.nextInt(0, 100) >= 50) list.add(index)
                val C1 = UByteArray(list.size)
                var index = 0
                for(element in list)
                    C1[index++] = element.toUByte()
                
                
                r1 = generateUByteArray(m)
                val M2 = Message(ID, received.source, mutableListOf(C1, r1))
                sendMessage(M2)
                
                step = 2

                // Compute key k1, for future use
                var flag = false
                for(i in C1) {
                    if(!flag) {
                        k1 = vault[received.source[0].toInt()][i.toInt()]
                        flag = true
                    } else k1 = UByteArray(k1.size) { index -> (k1[index].toInt() xor vault[received.source[0].toInt()][i.toInt()][index].toInt()).toUByte() }
                } 
            }
            else{
                var encrypted = received.payload[0]
                val decrypted = decrypt(encrypted, getKey(received.source))
                val string = decrypted.toByteArray().toString(Charsets.UTF_8) + "| this part was added by SERVER: Return back to sender"
                encrypted = encrypt(string.toByteArray().toUByteArray(), getKey(received.source))
                val MESSAGE = Message(ID, received.source, mutableListOf(encrypted))
                sendMessage(MESSAGE)
            }
        }
        else if(step == 2 && getInbound().size >= 1) {
            val received = getInbound()[0]
            
            val decryption = decrypt(received.payload[0], k1)

            val retrieved_r1 = decryption.copyOfRange(0, m)
            if(r1.contentEquals(retrieved_r1)) {
                t1 = decryption.copyOfRange(m, m*2)
                val C2 = decryption.copyOfRange(m*2, decryption.size - m)
                val r2 = decryption.copyOfRange(decryption.size - m, decryption.size)


                var k2 = UByteArray(m)
                var flag = false
                for(i in C2) {
                    if(!flag) {
                        k2 = vault[received.source[0].toInt()][i.toInt()]
                        flag = true
                    } else k2 = UByteArray(k2.size) { index -> (k2[index].toInt() xor vault[received.source[0].toInt()][i.toInt()][index].toInt()).toUByte() }
                }
                
                val k2xort1 = UByteArray(k2.size) { index -> (k2[index].toInt() xor t1[index].toInt()).toUByte() }
                t2 = generateUByteArray(m)

                val payload = UByteArray(r2.size + t2.size)
                r2.copyInto(payload, 0)
                t2.copyInto(payload, r2.size)

                val encryption = encrypt(payload, k2xort1)
                val M4 = Message(ID, received.source, mutableListOf(encryption))
                sendMessage(M4)

                step = 0

                val t = UByteArray(t1.size) { index -> (t1[index].toInt() xor t2[index].toInt()).toUByte() }
                val id = received.source
                deauthenticate(id[0].toInt())
                table.add(mutableListOf(id, t))
            }
        }
        removeInbound()
    }

    fun getKey(deviceID: UByteArray): UByteArray {
        for(element in table)
            if(element[0].contentEquals(deviceID))
                return element[1]
        return UByteArray(m)
    }

    fun deauthenticate(deviceID: Int) {
        for(element in table)
            if(element[0].contentEquals(ubyteArrayOf(deviceID.toUByte()))) {
                table.remove(element)
                break
            }
    }

    fun isAuthenticated(deviceID: Int): Boolean {
        for(element in table)
            if(element[0].contentEquals(ubyteArrayOf(deviceID.toUByte())))
                return true
        return false
    }

    override fun toString(): String{
        var device = super.toString()
        device += "\t\tAuthenticated IoT device(s):\n"
        for(element in table)
            device += "\t\t\t\tIoT ID: ${element[0].joinToString()}\tKey: ${element[1].joinToString()}\n"
        return device
    }

}