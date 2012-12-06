heizung-tomcat
==============

Heating control running in Tomcat as webapp using OWFS and Quartz scheduler

For running it, you need to mount the OneWire filesystem an the location
specified in the properties file, so the temperature sensors can be read and the output can control
the heating valve as needed.
I have tested it with two DS18B20 sensors and one DS2408 i/o chip on the bus.

Then you can deploy the war file in tomcat 7 as a webapp.
In tomcat, you have to add a user with the role 'heizung', so you can login to the heizung-tomcat webapp.