Z-Way API with Java: an Example
===============================

An example of how to interact with the Z-Way API in Java, created for teaching purposes.

It employs the [Z-Way library for Java](https://github.com/pathec/ZWay-library-for-Java): its `jar` is available in the `lib` folder of the project. All dependencies are handled with gradle.

The example shows how to:
* authenticate on the available Z-Way server
* get all the `SwitchBinary` devices
* get all the `SensorMultilevel` and `SensorBinary` devices
* turn `SwitchBinary` devices on and off
* get different type of measures from the `SensorMultilevel` and `SensorBinary`` devices
