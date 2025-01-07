package auth
import kotlin.random.Random

public var n = 25
public var m = 32
public val AUTH = ubyteArrayOf(0u)
public val DEAUTH = ubyteArrayOf(1u)
public val MESG = ubyteArrayOf(2u)

@kotlin.ExperimentalUnsignedTypes
class Channel(val number: Int, val _n: Int, val _m: Int) {
    private var devices: MutableList<Device> = mutableListOf()
    private var messages: MutableList<Message> = mutableListOf()
    
    init {
        n = _n
        m = _m

        var vaultArray: MutableList<MutableList<UByteArray>> = mutableListOf()
        var vault: MutableList<UByteArray> = mutableListOf()
        for(i in 0..number - 1) {
            for(j in 0..n-1)
                vault.add(UByteArray(m) { Random.nextInt(0, 256).toUByte() })
            vaultArray.add(vault)
            devices.add(IoTDevice(ubyteArrayOf(i.toUByte()), mutableListOf(vault)))
            vault = mutableListOf()
        }
            
        devices.add(Server(ubyteArrayOf(255u), vaultArray))
    }


    fun operate() {
        // Operate actions on the devices. This includes the submission of new devices
        // Sender -> Message ------------------------> Destination
        operateOnDevices()

        // deliver messages from devices to the channel
        // Sender ------------> Message ------------ -> Destination
        for(device in devices) 
            for(message in device.getOutbound())
                messages.add(message)
        removeOutbound()

        // deliver messages from the channel to the destination
        // Sender -------------------------> Message -> Destination
        for(message in messages) {
            val destination = message.destination
            
            for(device in devices)
                if(destination.contentEquals(device.ID)) 
                    device.receiveMessage(message)
        }
        removeMessages()
    }

    fun addMessage(message: Message) {
        messages.add(message)
    }

    fun removeMessages() {
        messages = mutableListOf()
    }

    fun getMessages(): MutableList<Message> {
        return messages
    }

    fun authenticate(deviceID: Int) {
        for(device in devices)
            if(device.ID.contentEquals(ubyteArrayOf(deviceID.toUByte())) && device is IoTDevice)
                device.authenticate()
    }

    fun startCommunication(deviceID: Int, message: String) {
        for(device in devices)
            if(device.ID.contentEquals(ubyteArrayOf(deviceID.toUByte())) && device is IoTDevice) {
                println("\n##########################################################\n")
                println("Communication initiated!")
                println("\n##########################################################\n")
                device.startCommunication(deviceID, message)
            }
                
    }

    fun isCommunicationCompleted(deviceID: Int): Boolean {
        for(device in devices)
            if(device.ID.contentEquals(ubyteArrayOf(deviceID.toUByte())) && device is IoTDevice)
                return device.completed
        return true
    }

    fun deauthenticate(deviceID: Int) {
        for(device in devices)
            if(device.ID.contentEquals(ubyteArrayOf(deviceID.toUByte())))
                device.deauthenticate()
    }

    fun isAuthenticated(deviceID: Int): Boolean {
        var IoT = false
        var server = false
        for(device in devices) {
            if(device.ID.contentEquals(ubyteArrayOf(deviceID.toUByte())) && device is IoTDevice)
                IoT = device.isAuthenticated()
            if(device.ID.contentEquals(ubyteArrayOf(255.toUByte())) && device is Server)
                server = device.isAuthenticated(deviceID)
        }
            
        return IoT && server
    }

    fun operateOnDevices() {
        for(device in devices)
            device.operate()
    }

    fun removeOutbound() {
        for(device in devices)
            device.removeOutbound()
    }

    override fun toString(): String {
        var devicez = ""
        var i = 1
        for(device in devices) {
            devicez += "${i++}) "
            devicez += device.toString()
            devicez += "\n"
        }
        return "Channel info:\n------------------------------\n${devicez}------------------------------"
    }

}