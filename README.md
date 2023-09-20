# SmartWinemaking
Project repository for the *Internet of Things* course at University of Pisa A.Y. 22-23

## Application description and idea
Alcoholic fermentation is the primary stage of wine production where the sugar in the grape juice 
undergoes transformation into alcohol. When yeast comes into contact with the sugar in the grape 
juice, it metabolizes the sugar through a series of chemical reactions. The yeast breaks down the 
sugar molecules into alcohol (ethanol) and carbon dioxide as byproducts. This conversion process 
is known as fermentation.
In our case the carbon dioxide is released in the air and utilized to mix the must, while in other 
case this gas can be utilized to make the wine sparkling.
The specific method utilized in this example of “smart fermenter” is the one called Ganimede.

The fermenter obviously must have a method to open the bypass, and some sensors to evaluate 
the quality of the must and the process of fermentation itself. The application in order to do so 
has to have the meanings to measure temperature, float level and co2 level in the air, and the 
capacity to open the bypass and activate a cooling system to maintain a good quality of the 
fermentation. 

## Implementation
The system is composed of two different IoT device networks. One network is composed of IoT 
devices that use *MQTT* to report data, while the other network uses *CoAP* as application protocol. 
Both MQTT and CoAP network is deployed using real sensors from the IoT testbed. Both networks 
are connected to a border router that allows them external access.
In this project were used 3 sensors (Co2, Temperature and Float) and 2 actuators (Bypass and
Cooling).
The collector collects data from both MQTT and CoAP sensors and stores them in a MySQL 
database. The collected data can then be visualized through a web-based interface developed 
using Grafana.
A simple control logic is executed on the collector to modify the external environment based on 
the data that has been collected from temperature and float sensors. The collector also exposes a 
simple command line interface through which the user can retrieve the last measured values or 
change the ranges within the actuators will turn on or off.

## Links
[Complete documentation](https://github.com/Plaza99/SmartWinemaking/blob/main/SmartWinemaking%20-%20documentation.pdf)

