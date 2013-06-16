# Growl4Java

## About

A minimalistic Java wrapper for Growl's notification system.  
Your feedback is always welcome.

![image](http://cl.ly/image/2D0i1U1q1x1v/growl4j.png)

> Example of an Growl4Java notification [(theme)](http://pixelb.in/howl-a-modern-rounded-growl-theme-381/).

## Requirements

- Mac OS X, with Growl (1.3+) installed
- Java version 6/7

## Get started

### Setup Growl

First, `import net.metzweb.growl.Growl;` and setup the Growl class.  
The constructor requires three arguments:

1. The name of your application.
2. An array with all **available** [notification types](#notification-types).
3. An array with the **enabled** [notification types](#notification-types).

```
Growl growl = new Growl("Java Weather",
	new String[]{"General", "Warnings"},
	new String[]{"General"});
```

Next, check whether Growl is available (installed and running). If this is the case, register the application.

```
if (growl.init()) {
	growl.registerApplication();
}
```

### Send notification

Once our application is registered, we can start sending out messages. The first parameter of `notify` is the used [notification type](#notification-types), that has to be specified and enabled in the constructor.

```
growl.notify("General", "Weather", "Foggy");
```

#### Add notification icon

If you want to add a fancy icon to your message, specify it as a third parameter:

```
growl.notify("General", "Weather", "Sunny", "/Users/foo/sun.png");
```

The icon path has to be an **absolute path**, which you can get like this:

```
File iconFile = new File("not/absolute/path/icon.png");
iconFile.getAbsolutePath();
```

---

Please note, that the icon will only appear if **Growl notifications** are used instead of Mountain Lion's **notification center** (it's not a limitation of the class).

---

### Notification types

Notification types allow you to group your messages by their purpose. This allows your users to manage your apps notifications by its type: 

![image](http://cl.ly/image/1I1w2t0f0n14/notification-types.png)

The type can be changed by modifying `notify()` method's first parameter:

```
growl.notify("Warning", "Weather alert", "Thunder-storm");
```

## Available methods ##

- `boolean init()`: Initializes Java's AppleScript engine.
- `void registerApplication()`: Adds application to Growl.
- `void notify(<notification>, <title>, <message>)`: Sends notification.
- `void notify(<notification>, <title>, <message>, <icon>)`: Sends notification with icon.

> [Growl AppleScript developer documentation.](http://growl.info/documentation/applescript-support.php)

## History ##

**Growl4J 2.0 - 03/06/2013**

- `update` Updated AppleScript commands for Growl 2.0+.
- `feature` Implemented support for notification icons.
- `feature` Added documentation.

**Growl4J 1.0 - 15/01/2013**

- `update` Compatible with Growl 1.3

**Growl4J 0.5 - 10/01/2013**

- `release` Beta version.

## Credits ##

Copyright (c) 2013 - Programmed by Christian Metz  
Based on Tobias Södergren *Growlbridge*, Apache License 2.0.  
Released under the [BSD License](http://www.opensource.org/licenses/bsd-license.php).