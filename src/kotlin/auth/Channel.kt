package auth
import kotlin.random.Random

public val n = 25
public val m = 32

@kotlin.ExperimentalUnsignedTypes
class Channel(val number: Int) {
    private var devices: MutableList<Device> = mutableListOf()
    private var messages: MutableList<Message> = mutableListOf()
    
    init {
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
        println("\n##########################################################\n")
    }

    fun addMessage(message: Message) {
        messages.add(message)
    }

    fun removeMessages() {
        messages = mutableListOf()
    }

    fun startDemo(deviceID: Int) {
        println("\n##########################################################\n")
        println("Authentication process started for device ID: ${deviceID}.")
        println("\n##########################################################\n")
        for(device in devices)
            if(device.ID.contentEquals(ubyteArrayOf(deviceID.toUByte())))
                device.initiateAuthentication()
    }

    fun startCommunication(deviceID: Int, message: String) {
        for(device in devices)
            if(device.ID.contentEquals(ubyteArrayOf(deviceID.toUByte())) && device is IoTDevice)
                device.startCommunication(deviceID, message)
    }

    fun deauthenticate(deviceID: Int) {
        for(device in devices)
            if(device.ID.contentEquals(ubyteArrayOf(deviceID.toUByte())))
                device.deauthenticate()
            else if(device.ID.contentEquals(ubyteArrayOf(255.toUByte())) && device is Server)
                device.deauthenticate(deviceID)
        println("Device ${deviceID} successfully deauthenticated.")
    }

    fun isAuthenticated(deviceID: Int): Boolean {
        for(device in devices)
            if(device.ID.contentEquals(ubyteArrayOf(255.toUByte())) && device is Server)
                return device.isAuthenticated(deviceID)
        return false
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