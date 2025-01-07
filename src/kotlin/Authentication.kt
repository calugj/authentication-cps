import auth.Channel


@kotlin.ExperimentalUnsignedTypes
fun main() {
    print("\u001b[H\u001b[2J")
    println("IoT devices authentication simulator.\n")
    var number = 0
    var _n = 0
    var _m = 0
    do {
        print("Enter the number of IoT devices [1-255]: ")
        number = readLine()?.toIntOrNull() ?: 0 
        print("\u001b[H\u001b[2J")
    } while(number <= 0 || number > 255)

    do {
        print("Enter the number of keys in a vault [2-100]: ")
        _n = readLine()?.toIntOrNull() ?: 0 
        print("\u001b[H\u001b[2J")
    } while(_n < 2 || _n > 100)

    do {
        print("Enter the number of bytes for the keys [16]-[24]-[32]: ")
        _m = readLine()?.toIntOrNull() ?: 0 
        print("\u001b[H\u001b[2J")
    } while(_m != 16 && _m != 24 && _m != 32)

    val channel = Channel(number, _n, _m)


    val thread = Thread {
        try {
            while (!Thread.currentThread().isInterrupted) {
                channel.operate()
                Thread.sleep(100)
            }
        } catch (e: InterruptedException) {}
    }
    thread.start()

    var choice = '0'
    do {
        do {
            print("1) Print channel info\n2) Start authentication process\n3) Deauthenticate a device\n4) Test communication\n5) Quit\nChoice: ")
            java.lang.Runtime.getRuntime().exec(arrayOf("/bin/sh", "-c", "stty raw </dev/tty")).waitFor()
            choice = System.`in`.read().toChar()
            java.lang.Runtime.getRuntime().exec(arrayOf("/bin/sh", "-c", "stty cooked </dev/tty")).waitFor()
            print("\u001b[H\u001b[2J")
        } while(choice != '1' && choice != '2' && choice != '3' && choice != '4' && choice != '5')
        

        if(choice == '1') {
            println(channel)
        }
        else if(choice == '2') {
            var device = -1
            do {
                print("Select device [0-${number-1}]: ")
                device = readLine()?.toIntOrNull() ?: 0
                print("\u001b[H\u001b[2J")
            } while(device < 0 || device >= number)
            if(channel.isAuthenticated(device))
                    println("Device ${device} is already authenticated.\n")
            else {
                channel.authenticate(device)
                while(!channel.isAuthenticated(device)) Thread.sleep(100)
            }
        }
        else if(choice == '3') {
            var device = -1
            do {
                print("Select device [0-${number-1}]: ")
                device = readLine()?.toIntOrNull() ?: 0
                print("\u001b[H\u001b[2J")
            } while(device < 0 || device >= number)
            if(channel.isAuthenticated(device)) {
                channel.deauthenticate(device)
                while(channel.isAuthenticated(device)) Thread.sleep(100)
            }
            else println("Device ${device} is not authenticated yet.\n")
        }
        else if(choice == '4') {
            var device = -1
            do {
                print("Test communication [0-${number-1}]: ")
                device = readLine()?.toIntOrNull() ?: 0
                print("\u001b[H\u001b[2J")
            } while(device < 0 || device >= number)
            if(channel.isAuthenticated(device)) {
                var message: String
                do {
                    print("Enter a message: ")
                    message = readLine().orEmpty()
                    print("\u001b[H\u001b[2J")
                } while (message.isEmpty())
                
                channel.startCommunication(device, message)
                while(!channel.isCommunicationCompleted(device)) Thread.sleep(100)
            }
            else println("Device ID: ${device} is not authenticated yet.\n")
        }
    } while (choice != '5')

    thread.interrupt()
}
