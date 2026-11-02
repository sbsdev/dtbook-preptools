# dtbook-preptools

This project provides a plugin to the [oXygen XML Editor](http://www.oxygenxml.com/) that allows simple marking up of
dtbook-files as a preparatory step to translation to [Braille](http://en.wikipedia.org/wiki/Braille).


# Installation

The plugin needs [oXygen XML Editor](http://www.oxygenxml.com/) 25 or later.

1. Download `dtbook-preptools-<version>-plugin.zip` from the
   [releases](https://github.com/sbsdev/dtbook-preptools/releases). Versions up to 1.4 are on
   [Maven Central](https://repo1.maven.org/maven2/ch/sbs/preptools/dtbook-preptools/).
2. Quit oXygen.
3. Delete the folder `plugins/dtbook-preptools` in the oXygen installation directory, if there is
   one, and unzip the zip file into a new, empty `plugins/dtbook-preptools` folder. The zip file has
   no top-level folder of its own.
4. Start oXygen. The preptools appear in the PrepTools menu and toolbar.

What's new in each version is described in the [changelog](CHANGELOG.md).

# Debugging

You can choose whether to connect to a running instance of oxygen or to monitor it from its start.
Invoke oxygen startup (a shell script to be found in the oxygen installation directory that is named
`oxygen` with the version number appended, e.g. `25.0`) with the following command line
option. (This will be passed as a command line option to the '`java`' command):

    oxygen25.0 -J-Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=$SUSPEND
    
`$SUSPEND` can be either '`y`' or '`n`'. When '`y`', oXygen startup will be suspended until a
debugger connects. The port to connect to is given from the oxygen command on stdout as follows:

    Listening for transport dt_socket at address: <number>

## Regexp Debugging

For interactive exploration of regular expressions try https://regex101.com

# Build

The build needs Java 17 or later and produces the plugin zip in `target/`.

```
mvn -B package
```
Run the tests
```
mvn -B test
```
Build GTAGS file
```
mvn -B exec:exec
```
Format the source code
```
mvn -B spotless:apply
```

# Release

1. In `CHANGELOG.md`, turn the `[Unreleased]` section into `[<version>] - <date>`, start a new
   empty `[Unreleased]` section, update the links at the bottom, and commit.
2. Set the version, tag the release as `v<version>` and push:
   ```
   mvn release:prepare
   ```
3. Build the plugin zip from the tag, in `target/checkout/target/`:
   ```
   mvn release:perform
   ```
4. Publish it as a GitHub release, with the changelog section as release notes:
   ```
   gh release create v<version> target/checkout/target/dtbook-preptools-<version>-plugin.zip \
     --title <version> --notes-file <notes>
   ```

# License

Copyright 2011 SBS.

Licensed under GNU Lesser General Public License as published by the Free Software Foundation,
either [version 3](http://www.gnu.org/licenses/gpl-3.0.html) of the License, or (at your option) any later version.
