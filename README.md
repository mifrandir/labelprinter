# Label Printer

Allows to parse an XML file to print letter labels containing names and addresses based
on configuration. This is something I needed and at this point it has done its job. It's
a bit hacky and far away from being polished but maybe someone else needs something like
this.

Assuming that your XML files look different than mine, you probably want to change 
`Parser.java` and `Config.java`. Also it's pretty straight forward to adapt to different
label layouts.

If you have any questions or feature requests, let me know. For now I will not be putting
any work into this.

## Usage

```
$ ./gradlew wrapper --gradle-version 7.5
$ ./gradlew build
$ java -jar app/build/libs/app-all.jar "Mitglieder aktuell.xml" "Mitglieder aktuell.pdf"
```

## Dependencies

Other versions may work, but these are tried and tested:

- [Java JDK 17](https://www.oracle.com/java/technologies/downloads/)
- Gradle 7.5
