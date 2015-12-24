# LoggingService
Android background service capable of receiving and logging messages to a file on the device.

The logging service runs after a successful boot and remains running in the background waiting for requests. Any app may bind to it and request a logging action by passing the message that should be logged.

Utilizes a custom BroadcastReceiver to start the service at boot, and log low battery, power disconnect, and configuration change events. A notification can be displayed to the user when an event or message is logged.
