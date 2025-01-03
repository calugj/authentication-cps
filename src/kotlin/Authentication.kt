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
    while(choice != 5) {
        do {
            print("Main Menu\n1) Show info\n2) Start authentication process\n3) Deauthenticate a device\n4) Test communication\n5) Quit\nChoice: ")
            choice = readLine()?.toIntOrNull() ?: 0 
        } while(choice <= 0 || choice > 5)
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
            for(i in 0..4) {
                channel.operate()
                Thread.sleep(500)
            }
        } else if(choice == 3) {
            var device = -1
            do {
                print("Select device [0-${number-1}]: ")
                device = readLine()?.toIntOrNull() ?: 0
                print("\u001b[H\u001b[2J")
                System.out.flush()
            } while(device < 0 || device >= number)
            channel.deauthenticate(device)
        } else if(choice == 4) {
            var device = -1
            do {
                print("Test communication [0-${number-1}]: ")
                device = readLine()?.toIntOrNull() ?: 0
                print("\u001b[H\u001b[2J")
                System.out.flush()
                if(channel.isAuthenticated(device)) {
                    var message: String
                    do {
                        print("\u001b[H\u001b[2J")
                        System.out.flush()
                        print("Enter a non-empty string: ")
                        message = readLine().orEmpty()
                    } while (message.isEmpty())
                    print("\u001b[H\u001b[2J")
                    System.out.flush()
                    channel.startCommunication(device, message)
                    for(i in 0..2) {
                        channel.operate()
                        Thread.sleep(500)
                    }

                } else println("Device ID: ${device} is not authenticated.")
            } while(device < 0 || device >= number)
            
        }

        println("\n")
    }

}