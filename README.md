# Smartify
Automatically open the garage door as you approach. Works based on your geolocation.

Features:
- Open and close the garage remotely.
- Automatically open the garage when approaching it.
- Monitor if the garage is open or closed. 
- Safer than traditional remotes. (Can't be sniffed and replayed)
- Works on up to 4 garage doors.

## Required Hardware
1. [Particle Photon](https://store.particle.io/products/photon)
2. [Particle Relay Shield](https://store.particle.io/products/relay-shield)
3. [12V Power Supply](https://www.amazon.com/dp/B00KZ2ZQE8)
4. [Magnetic Sensor](https://www.amazon.com/dp/B0009SUF08)
5. Wire

## Installation
You need to install both the hardware and the software in order for it to work.

### Hardware
Connect the magnetic sensor to the garage door. Attach the one (wireless) part to the garage door, and the other part (with the wires) next to it on the wall. Make sure they are close enough.

<p align="center"><img src="/images/magneticsensor.jpg" width="250"></p>

Run the wires back to the relay shield. Connect the NO pin on the magnetic sensor to the A0 pin on the Photon (white wire). Connect the COM pin on the magnetic sensor to the GND pin on the photon (blue wire). Note that this isn't strict, you can just as easily connect NO to GND and COM to A0, this makes no difference.

Run two wires from the garage controller where the mechanical switch is hooked up to the relay shield (wire pair with the red line in image but will most likely be different for you). Alternatively you can also run wires from the mechanical switch, simply tap them off of the two wires already connected.

<p align="center"><img src="/images/garagecontroller.jpg" width="450"></p>

Run these wires back to the NO and COM port of Relay 1 on the relay shield. Again the order doesn't matter.

The relay shield should then be connected as follows.

<p align="center"><img src="/images/relayshield.png" width="450"></p>

Note that when using multiple garage doors, you can use Relay 2 with a magnetic sensor on A1 (and GND), Relay 3 with a magnetic sensor on A2 (and GND), and Relay 3 with a sensor on A3 (and GND).

### Software
In addition to this Android application, you need to put the following code on your Particle Photon. More info can be found in the Photon's [Getting Started](https://docs.particle.io/guide/getting-started/start/photon/) guide.

```C++
#define NB_RELAYS 4

int led = D7; 
int relays[] = {D3, D4, D5, D6}; 
int sensors[] = {A0, A1, A2, A3}; 
int sensorValues[4];

void setup() 
{ 
   int i; 
   pinMode(led, OUTPUT); 
   digitalWrite(led, LOW); 
   for (i=0; i<NB_RELAYS; i++) { 
       pinMode(relays[i], OUTPUT); 
       digitalWrite(relays[i], LOW); 
       pinMode(sensors[i], INPUT_PULLUP); 
       Particle.variable("sensorvalue" + String(i+1) , sensorValues[i]); 
   } 
   Particle.function("led",ledToggle); 
   Particle.function("open",open); 
   Particle.function("close",close); 
}

void loop() 
{ 
   int i; 
   for (i=0; i<NB_RELAYS; i++) { 
       sensorValues[i] = digitalRead(sensors[i]); 
   } 
   delay(1000); 
}

int ledToggle(String command) { 
   if (command=="on") { 
       digitalWrite(led,HIGH); 
       return 1; 
   } 
   else if (command=="off") { 
       digitalWrite(led,LOW); 
       return 0; 
   } 
   else { 
       return -1; 
   } 
} 

int open(String command) { 
   int i = command.toInt(); 
   if (i<=0 || i>NB_RELAYS) { 
       return -1; 
   } 
   i--; 
   if (!isOpen(sensors[i])) { 
       relayToggle(relays[i]); 
       return 0; 
   } 
   return 1; 
} 

int close(String command) { 
   int i = command.toInt(); 
   if (i<=0 || i>NB_RELAYS) { 
       return -1; 
   } 
   i--; 
   if (isOpen(sensors[i])) { 
       relayToggle(relays[i]); 
       return 0; 
   } 
   return 1; 
} 

bool isOpen(int sensor) { 
   return digitalRead(sensor) == HIGH; 
} 

void relayToggle(int relay) { 
   digitalWrite(relay,HIGH); 
   delay(500); 
   digitalWrite(relay,LOW); 
} 
```

## How It Works
After installing the software on both the Photon and the Android phone, you can setup a new device.

<p align="center"><img src="/images/editdevice.png" width="250"></p>

- Device Name: Name for the device e.g. "My Garage"
- Device ID: The Particle device ID which can be found under devices in the build dashboard. e.g. "132000024005011111110343"
- Access Token: Token found under settings in the particle build dashboard. e.g. "abc01dddffffffffffffffff00000010100ffeeeee"
- Relay Number: The number of the relay that the Garage is connected to. (1-4)
- Location latitude/longitude. The location of your garage. This is used to automatically open the garage door if enabled. Stand in front of your garage and hit "set to current".
- Auto Open: Enable if the garage should automatically be open when approaching it. The radius of the region is set to 100m. This means that if you enter the region, the garage will open.

See the list of garages and the status. Red dot means garage closed, green dot means garage opened.

<p align="center"><img src="/images/devices.png" width="250"></p>

You can then use your smartphone also open/close the garage remotely or have it open automatically as you approach it.

## Code Layout
The code tries to follow the principles of [The Clean Architecture](https://8thlight.com/blog/uncle-bob/2012/08/13/the-clean-architecture.html).
