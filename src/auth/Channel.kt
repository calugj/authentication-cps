package auth
import kotlin.random.Random

public val n = 5
public val m = 16

@kotlin.ExperimentalUnsignedTypes
class Channel() {
    private lateinit var server: Server
    private lateinit var device: Device
    private var messages: MutableList<Message> = mutableListOf()
    
    
    init {
        var vault: MutableList<UByteArray> = mutableListOf()
        for(i in 0..n-1) {
            val key = UByteArray(m) { Random.nextInt(0, 256).toUByte() }
            vault.add(key)
        }


        server = Server(ubyteArrayOf(255u), vault)
        device = Device(ubyteArrayOf(0u), vault)
    }


    fun operate() {
        for(message in server.getOutbound()) {
            messages.add(message)
        }
        for(message in device.getOutbound()) {
            messages.add(message)
        }
        removeOutbound()

        for(message in messages) {
            val destination = message.destination
            
            if(destination.contentEquals(server.ID)) {
                server.receiveMessage(message)
            } else if(destination.contentEquals(device.ID)) {
                device.receiveMessage(message)
            }
        }
        removeMessages()

        operateOnDevices()



        println("----------------------\n\n")
    }

    fun addMessage(message: Message) {
        messages.add(message)
    }

    fun removeMessages() {
        messages = mutableListOf()
    }

    fun startDemo() {
        device.initiateAuthentication()
    }

    fun operateOnDevices() {
        server.operate()
        device.operate()
    }

    fun removeOutbound() {
        server.removeOutbound()
        device.removeOutbound()
    }


}