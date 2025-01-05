#import "@preview/chronos:0.2.0"

#set par(justify: true)
#set heading(numbering: "1.")
#set math.vec(delim: "[")
#set math.mat(delim: "[")
#set page(columns: 2)
#show raw: set text(size: 7pt)
#show figure.caption: emph
#show figure.caption: set text(size: 9pt)


#show ". ": ".   "
#show "i.e.": "i.e."
#show "e.g.": "e.g."
#show "etc.": "etc."




#place(
  top + center,
  scope: "parent",
  float: true,
  align(center)[
    #block(width:85%)[
      #text(size:22pt, weight:"semibold", "Report for the Course on Cyber-Physical Systems and IoT Security")

      #text(size:17pt, style:"italic", "Authentication of IoT Device and IoT Server Using Secure Vaults")

      #text(size:14pt, "Boscolo Meneguolo Luca  -  2113488")
      
      #linebreak()
    ]
  ]
)





= Objective



= System Setup

This project aims to implement the authentication protocol.
The decision on the programming language to use to implement the simulator was based on some observations.
We don't need the efficiency of C in such simulation.
IoT devices offer low computational power, and although a real implementation would require a fast and versatile programming language (with direct access to the memory), for the sake of the demonstration we can use a modern language, although with limited memory management.
The choice was then to adopt Kotlin, because it offers modern commodities and features, and it is 100% compatible with the well established Java Runtime Environment.


= Experiments

== Overview of the System

An IoT System is composed by three classes of devices:
- IoT device: its purpose is to collect data from the sensors, and send them to the server;
- server: collects data from all the devices;
- user interface: provides the user a configurable environment to interact with the system.
Every device rely on a communication channel to send and receive messages, although the server usually is in a remote location#footnote[Cloud services.], 

*IoT devices*

IoT devices need to perform lightweight operations, and usually are battery-powered.
For these reasons, they offer very low computational capabilities, and cost constraints limit the memory available.
To every IoT device, it is assigned a unique identification number.

*Server*

The server has a well protected database.


*Vault*

The devices have access to a vault.
The vault is substantially a secure storage containing $n$ keys, each one of them being $m$ bits long.
The vault needs to be shared between one IoT device and the server, and must be consistent at all times.
Obviously the server must have different instances of the vault, where one corresponds to a specific IoT device.
Parameters $m$ and $n$ are tuned by the developers, to balance security and computational power.



The simulator is developed as follows. 

== Authentication mechanism





= Results and Discussion


= Conclusions






#show ".": "."
#linebreak()
#bibliography("biblio.yml")