package auth

@kotlin.ExperimentalUnsignedTypes
class Message(val source: UByteArray, val destination: UByteArray, val payload: MutableList<UByteArray>) {
    
}