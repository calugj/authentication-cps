import auth.Channel


@kotlin.ExperimentalUnsignedTypes
fun main() {
    print("\u001b[H\u001b[2J")
    println("IoT devices authentication simulator.")
    var number = 0
    do {
        print("Enter the number of IoT devices [1-255]: ")
        number = readLine()?.toIntOrNull() ?: 0 
        print("\u001b[H\u001b[2J")
    } while(number <= 0 || number > 255)
    val channel = Channel(number)

    var choice = 0
    while(choice != 4) {
        do {
            print("Main Menu\n1) Show info\n2) Start authentication process\n3) Deauthenticate a device\n4) Quit\nChoice: ")
            choice = readLine()?.toIntOrNull() ?: 0 
        } while(choice <= 0 || choice > 4)
        print("\u001b[H\u001b[2J")
        System.out.flush()

        if(choice == 1) {
            print("\u001b[H\u001b[2J")
            System.out.flush()
            println(channel)
        } else if(choice == 2) {
            var device = -1
            do {
                print("Select device [0-${number-1}]: ")
                device = readLine()?.toIntOrNull() ?: 0
                print("\u001b[H\u001b[2J")
                System.out.flush()
            } while(device < 0 || device >= number)
            print("\u001b[H\u001b[2J")
            System.out.flush()
            channel.startDemo(device)
            channel.operate()
            channel.operate()
            channel.operate()
            channel.operate()
            channel.operate()
        } else if(choice == 3) {
            var device = -1
            do {
                print("Select device [0-${number-1}]: ")
                device = readLine()?.toIntOrNull() ?: 0
                print("\u001b[H\u001b[2J")
                System.out.flush()
            } while(device < 0 || device >= number)
            channel.deauthenticate(device)
        }

        println("\n")
    }

}