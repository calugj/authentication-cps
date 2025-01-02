import auth.Message
import auth.Channel




@kotlin.ExperimentalUnsignedTypes
fun main() {
    val array = ubyteArrayOf(0u,1u,2u,3u,4u,5u,6u,7u,8u,9u)
    val message = Message(ubyteArrayOf(0u), ubyteArrayOf(100u), mutableListOf(array))

    val channel = Channel()
    channel.startDemo()
    channel.operate()
    channel.operate()
    channel.operate()
    channel.operate()
    channel.operate()
    channel.operate()
    channel.operate()
    channel.operate()
}