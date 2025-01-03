package auth
import kotlin.random.Random

@kotlin.ExperimentalUnsignedTypes
class IoTDevice(override val ID: UByteArray, override val vault: MutableList<MutableList<UByteArray>>): Device(ID, vault) {
    
    private var t1 = UByteArray(m)
    private var t2 = UByteArray(m)
    private var k2 = UByteArray(m)
    private var r2 = UByteArray(m)
    
    private var autht = UByteArray(m)
    private var authServer: UByteArray? = null

    override fun initiateAuthentication() {
        val sessionID = generateUByteArray(m)
        val M1 = Message(ID, ubyteArrayOf(255u), mutableListOf(ID, sessionID))
        sendMessage(M1)

        step = 1;
    }

    override fun deauthenticate() {
        step = 0
    
        autht = UByteArray(m)
        authServer = null
    }


    override fun operate() {
        if(step == 1 && getInbound().size >= 1) {
            val received = getInbound()[0]
            // Compute key k1
            var k1 = UByteArray(m)
            var flag = false
            for(i in received.payload[0]) {
                if(!flag) {
                    k1 = vault[0][i.toInt()]
                    flag = true
                }
                else k1 = UByteArray(k1.size) { index -> (k1[index].toInt() xor vault[0][i.toInt()][index].toInt()).toUByte() }
            }

            val r1 = received.payload[1]
            t1 = generateUByteArray(m)
            val list: MutableList<Int> = mutableListOf()
            for(index in 0..n-1)
                if(Random.nextInt(0, 100) >= 50) list.add(index)
            val C2 = UByteArray(list.size)
            var index = 0
            for(element in list)
                C2[index++] = element.toUByte()
            r2 = generateUByteArray(m)

            
            val payload = UByteArray(r1.size + t1.size + C2.size + r2.size)
            r1.copyInto(payload, 0)
            t1.copyInto(payload, r1.size)
            C2.copyInto(payload, r1.size + t1.size)
            r2.copyInto(payload, r1.size + t1.size + C2.size)

            val encryption = encrypt(payload, k1)

            val M3 = Message(ID, received.source, mutableListOf(encryption))
            sendMessage(M3)

            step = 3

            // Compute key k2, for future use
            flag = false
            for(i in C2) {
                if(!flag) {
                    k2 = vault[0][i.toInt()]
                    flag = true
                } else k2 = UByteArray(k2.size) { index -> (k2[index].toInt() xor vault[0][i.toInt()][index].toInt()).toUByte() }
            } 
        }
        else if(step == 3 && getInbound().size >= 1) {
            val received = getInbound()[0].payload[0]
            
            val k2xort1 = UByteArray(k2.size) { index -> (k2[index].toInt() xor t1[index].toInt()).toUByte() }

            val decrypted = decrypt(received, k2xort1)

            val obtained_r2 = decrypted.copyOfRange(0, m)
            t2 = decrypted.copyOfRange(m, decrypted.size)

            if(obtained_r2.contentEquals(r2)) {
                step = 4
                autht = UByteArray(t1.size) { index -> (t1[index].toInt() xor t2[index].toInt()).toUByte() }
                authServer = getInbound()[0].source
                println("Authentication complete!")
            }
        } else if(step == 4 && getInbound().size >= 1) { 
            val received = getInbound()[0]
            val decrypted = decrypt(received.payload[0], autht).toByteArray().toString(Charsets.UTF_8)
        
            println("Relay message:\n${decrypted}")
        }

        removeInbound()
    }

    override fun toString(): String {
        var device = super.toString()
        if(authServer == null)
            return "${device}\t\tAuth: false"
        return "${device}\t\tAuth: true\tKey: ${autht.joinToString()}"
    }

    fun startCommunication(deviceID: Int, message: String) {
        val string = "This part was sent by IoT: ${message} "
        val encrypted = encrypt(string.toByteArray().toUByteArray(), autht)
        val MESSAGE = Message(ID, ubyteArrayOf(255.toUByte()), mutableListOf(encrypted))
        sendMessage(MESSAGE)
    }

}