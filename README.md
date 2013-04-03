AutoFlighty
==========

This is a simple Android application that turns on/off airplane mode every day according to your configured time.

It includes a simple foreground Activity to configure whether and when to turn on/off airplane mode. The configured task is executed in the background as broadcasts, and resolved by a BroadcastReceiver.

BUILD
==========
To build and run the project, you'll need an Android development environment (usually Eclipse + ADT). Use the project files in this repo to create a new project targeting API level 16.

RUN
==========
An exported copy of the application is provided in the current directory, named AutoFlighty.apk. Install it on any Android device running Android 2.2 ~ 4.1.

