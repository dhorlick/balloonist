*Balloonist* is a software application to help layout comics text balloons and panels.

## Installation

*Balloonist* in implemented in [Java](http://java.com) and built with [Apache Maven](https://maven.apache.org) version 3.0.

*Balloonist* runs under versions 1.4, 5.0, 6.0, 7.0 and 8.0 of the Java Runtime Environment. Although in principle *Balloonist* could be built on any of the Java Software Development Kits that correspond to these runtime versions, in practice you'll need the Java 5 one or better, since Maven v3 requires it.

Once Java and Maven are installed, you can build and install *Balloonist* by typing

```mvn install```

After a successful installation, you should be able to start the application by opening `app/target/balloonist-…-SNAPSHOT.jar` file from your desktop manager.

Alternately, to manually start Balloonist type `java -jar app/target/balloonist-…-SNAPSHOT.jar` from your command shell.

The installation will also generate user documentation. It can be browsed at `app/target/classes/manual/index.html` or from the pull-down menus of the running application.

## Implementation

*Balloonist* is subdivided into "app" and "balloon-engine" Maven modules.

The *balloon-engine* module is a library that uses the Java 2D API and the [iText PDF library](http://itextpdf.com) to provide circumscribed text layout. It was built for comics, but might also benefit illustration, computer-assisted design and diagramming software.

The *app* module uses Java Swing to supply *Ballonist's* graphics user interface.

## Contributors

*Balloonist* was written by Dave Horlick and used to be a commercial product sold by Smith & Tinker's Technology, LLC.

## License

*Balloonst* is released under the GNU GPLv3 license. See LICENSE.txt for details.
