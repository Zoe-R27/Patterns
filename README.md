# Patterns

#### Zoe Rudd - Software Engineer at Target
#### SENG5802 Final
#### Spring 2025

## About
Within this project, we are generating giftcards and saving them to, in this case a CSV file. The giftcards are generated
by retrieving a saved sequence from another csv file that keeps track of the lastest card number generated.

This project also showcases a few of the common design patterns including:
 - Singleton
 - Adapter
 - Prototype

## How To Set Up
This project runs on Java 17 and Gradle 8.11. It is best run in IntelliJ since it is written in Kotlin, a common
language in Enterprise systems at this time. 

To run in IntelliJ: 
1. Ensure you have Java 17
2. Download the community version from their [site](https://www.jetbrains.com/idea/).
3. Fork this repository and download to where you want it
4. In IntelliJ go to File -> New -> Project from Existing Sources -> Chose Forked Repository
5. Either let the gradle load in or run `./gradlew build`
   6. If the Gradle fails, you may be running a different version of Java
   7. Go to File -> Project Structure -> Make sure your SDK and Language is set to Java 17
   8. Go to File -> Settings -> Build, Execution Deployment -> Build Tools -> Gradle -> Make sure your Gradle JVM is Java 17

### Structure
 - Code in found in the `app/src/main/kotlin package`
   - The bulk of the logic is found in `app/src/main/kotlin/patterns/service`
 - Tests are found in `app/src/test/kotlin package`

There are various comment blocks around the project file that helps explain what the program does and how some of the patterns
are set up.


### Running the Project
You can run the program by going to `app/src/main/kotlin/App.kt` and running `main()`. 
This can be done by either hitting the green arrow button next on the left or right-clicking on `main` and choosing `Run 'App.kt'`

This will cause prompts to appear to on the terminal asking for:
 - The number of cards you want to generate
 - The amount of money you want the cards to be worth
 - The type of card you want generated
   - If you chose to generate a Prepaid card, it will also ask for an expiration date

To run the tests, you can right-click on the package(directory) and chose `Run Tests in...`

## Description and Analysis of Patterns
Why we have problem
How we started
Why we chose then instead of something else
Justify why using the adapter
    Usually would go to the database with these specific methods but for demo and not wanting to do containerization
    I sent have the adapter to go to the csv file so we can view it


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