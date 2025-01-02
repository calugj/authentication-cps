package auth

@kotlin.ExperimentalUnsignedTypes
class Message(val source: UByteArray, val destination: UByteArray, val payload: MutableList<UByteArray>) {
    
    override fun toString(): String {
        var payloads = ""
        var i = 1
        for(message in payload) {
            payloads += "${i++}) ${message.joinToString()}\n"
        }
        return "------------------------------\nSource: ${source.joinToString()}, Destination: ${destination.joinToString()}\nPayload contains ${i-1} field(s):\n${payloads}------------------------------"
    }

}