dtbook-preptools
================

This project provides a plugin to the [oXygen XML Editor](http://www.oxygenxml.com/) that allows simple marking up of
dtbook-files as a preparatory step to translation to [Braille](http://en.wikipedia.org/wiki/Braille).


Prerequisite installs
---------------------

* [oXygen XML Editor](http://www.oxygenxml.com/)


Developing with [eclipse](http://eclipse.org/)
-----------------------------------------------

In order to work on this project with [eclipse](http://eclipse.org/) you need to run the ant "compile"
task once after importing the project into eclipse in order for the required "generated_src" 
source folder to be generated.

Debugging with [eclipse](http://eclipse.org/)
-----------------------------------------------

You can choose whether to connect to a running instance of oxygen or to monitor it from its start.
Add the following line to the startup script that comes with oXygen (named '`oxygen`'). This is a
command line option to the '`java`' command:

    -Xrunjdwp:transport=dt_socket,address=104442,server=y,suspend=$SUSPEND
    
`$SUSPEND` can be either '`y`' or '`n`'. When '`y`', oXygen startup will be suspended until a
debugger connects. The port to connect to is given from the oxygen command on stdout as follows:

    Listening for transport dt_socket at address: <number>

Authors
-------

**Bernhard Wagner**

+ http://xmlizer.net
+ http://github.com/bwagner

License
---------------------

Copyright 2011 SBS.

Licensed under GNU Lesser General Public License as published by the Free Software Foundation,
either [version 3](http://www.gnu.org/licenses/gpl-3.0.html) of the License, or (at your option) any later version.
