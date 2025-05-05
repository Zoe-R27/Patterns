# Patterns

#### Zoe Rudd - Software Engineer at Target
#### SENG5802 Final
#### Spring 2025

## About
Within this project, we are generating giftcards and saving them to, in this case, a CSV file. The giftcards are generated
by retrieving a saved sequence from another csv file that keeps track of the last card number generated.

This project also showcases a few of the common design patterns including:
- Adapter
- Singleton
- Prototype

## How To Set Up
This project runs on Java 17 and Gradle 8.10. It is best run in IntelliJ since it is written in Kotlin, a common
language in Enterprise systems at this time.

To run in IntelliJ:
1. Ensure you have Java 17.
3. Download the community version from their [site](https://www.jetbrains.com/idea/).
4. If you don't have Java 17 installed, IntelliJ will prompt you to install Microsoft's OpenJDK 17.
5. Fork this repository and download to where you want it.
6. In IntelliJ go to File -> New -> Open -> Chose Forked Repository.
7. Either let the gradle load in or run `./gradlew build`.
    1. If the Gradle fails, you may be running a different version of Java.
    2. Go to File -> Project Structure -> Make sure your SDK and Language is set to Java 17.
    3. Go to File -> Settings -> Build, Execution Deployment -> Build Tools -> Gradle -> Make sure your Gradle JVM is Java 17.
8. If that doesn't work, you can try to do File -> New -> Project from Existing Sources.

### Structure
- Code can be found in the `app/src/main/kotlin package`.
    - The bulk of the logic is found in `app/src/main/kotlin/patterns/service`.
- Tests are found in `app/src/test/kotlin package`.

Various comment blocks around the project file helps explain what the program does and how some of the patterns
are set up.


### Running the Project
Normally, in a project, generating giftcards would be triggered via an API call or Kafka Listener and would go through a controller class. 
In this case, I have included a `main()` function that can prompt a user for card generation. This makes it easier to view and run.
You can run the program by going to `app/src/main/kotlin/App.kt` and running `main()`.
In IntelliJ, this can be done by either hitting the green arrow button next to the function name on the left or right-clicking on `main` and choosing `Run 'App.kt'`.

This will cause prompts to appear to on the terminal asking for:
- The number of cards you want to generate.
- The amount of money you want the cards to be worth.
- The type of card you want generated.
    - If you choose to generate a Prepaid card, it will also ask for an expiration date.
 
The output CSV files can be found in the .data directory.

To run the tests, you can right-click on the package(directory) and choose `Run Tests in...`.

## Brief Description and Analysis of Patterns

More detailed descriptions of the patterns can be found in the `PatternExplaination` directory.

### Adapter
An example of this can be found in the service package.

In an actual service, we would connect to a database that would have a table to save all the cards generated. We can use this table for tracking card usage, 
card lookups, and deleting cards. We would have an additional table to save, retrieve, and update the sequence to track
how many cards have been generated and where we are with generation numbers.

The adapter pattern is used to allow the program to use the CSV file as if it were a database. The client - in 
this case the `GenerateCardService` - is trying to call the database. The repositories are the original interface that 
we want the client to still feel like they are using. The adapter classes are there to adapt the CSV file to the interface that the client is expecting.
This way the client doesn't need to call all the methods related to file handling, such as readFile, parseFile, writeToFile, etc. 
located in the CSV handlers. It just needs to call save, get, and update functions that would normally be found in a repository.

A more detailed explanation of this can be found in the `PatternExplaination/Adapter.md` file.

### Singleton
The singleton pattern can be found throughout the main package, including an actual implementation in
`GenerateCardService.kt` and a built-in use of it in `AppConfig.kt` using Kotlin object.

The singleton pattern is used to ensure that there is only one instance of a class throughout the application.
This is useful for classes that are expensive to create or that need to be shared across the application, such as the configuration class.
In this case, the `GenerateCardService` is a singleton because having multiple instances
of it could cause issues, such as duplication and data inconsistencies, with the sequence handling and the card generation.

A more detailed explanation of this can be found in the `PatternExplaination/Singleton.md` file.

### Prototype
The example for this can be found in the `TestUtil` file in the test package.

For testing, we often need to create a lot of objects that are similar but not exactly the same. This can be time-consuming and
require large amounts of very similar code to manually type out all the different cards. The prototype pattern allows us to create a base object
and clone it with some small changes to certain fields. This way, we can create a lot of cards quickly and easily.

With the prototype pattern, I can create a base card and then loop and clone it with the changes I want, reducing the amount of redundant code.
The only difference in a traditional prototype compared to the one used in this project is that it uses Kotlin's `.copy()` function, which
does a shallow copy instead of a deep copy.

A more detailed explanation of this can be found in the `PatternExplaination/Prototype.md` file and in the comments of the `TestUtil.kt`.

## More on Gradle

This project uses [Gradle](https://gradle.org/).
To build and run the application, use the *Gradle* tool window by clicking the Gradle icon in the right-hand toolbar,
or run it directly from the terminal:

* Run `./gradlew build` to only build the application.
* Run `./gradlew clean` to clean all build outputs.

Note the usage of the Gradle Wrapper (`./gradlew`).
This is the suggested way to use Gradle in production projects.

[Learn more about the Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html).

[Learn more about Gradle tasks](https://docs.gradle.org/current/userguide/command_line_interface.html#common_tasks).

This project follows the suggested multi-module setup and consists of the `app` project.
The shared build logic was extracted to a convention plugin located in `buildSrc`.

This project uses a version catalog (see `gradle/libs.versions.toml`) to declare and version dependencies
and both a build cache and a configuration cache (see `gradle.properties`).
