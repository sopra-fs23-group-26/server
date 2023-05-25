<div align="center">
    <h2>SoPra FS23 - Group 26 Server</h2>
</div>

# WeGame-Game Platform

## Introduction
The idea of our project is to provide a fun and engaging platform for users to enjoy playing games and socializing with their friends. Our game platformn currently contain 1 single-player game Sudoku and 1 multi-player game Who is undercover. On our platform, the user could add friends, view game history, upload their image and also see the ranking among all users.

## Technologies
- Java 
- SpringBoot
- JPA (with H2 database)
- Github Actions
- REST API
- Datamuse API(external API)
- Dosuku API(external API)

## High-Level Components
The most important components are:
- [User](src/main/java/ch/uzh/ifi/hase/soprafs23/entity/User.java)
- [Room](src/main/java/ch/uzh/ifi/hase/soprafs23/entity/Room.java)
- [GameUndercover](src/main/java/ch/uzh/ifi/hase/soprafs23/entity/GameUndercover.java)

### User
The user is one of the most important component in our project. A user could edit their profile, upload their image and add new friends. A user could also see his game history which is linked to username and also rankings among all users.

### Room
The room component is a base for multi-players game and it has the information of all players. After the room is created and ready, all players would be directed to the game start page. The room component is thus reusable for all multi-players game.

### GameUndercover
This component is an example that we write for illustrate the multi-players game idea. It is responsible for the logic of the game, for example the turn, time to vote and when the game ends.

## Launch & Deployment
Download your IDE of choice (e.g., [IntelliJ](https://www.jetbrains.com/idea/download/), [Visual Studio Code](https://code.visualstudio.com/), or [Eclipse](http://www.eclipse.org/downloads/)). Make sure Java 17 is installed on your system (for Windows, please make sure your `JAVA_HOME` environment variable is set to the correct version of Java).

### IntelliJ
1. File -> Open... -> SoPra server template
2. Accept to import the project as a `gradle project`
3. To build right click the `build.gradle` file and choose `Run Build`

### VS Code
The following extensions can help you get started more easily:
-   `vmware.vscode-spring-boot`
-   `vscjava.vscode-spring-initializr`
-   `vscjava.vscode-spring-boot-dashboard`
-   `vscjava.vscode-java-pack`

**Note:** You'll need to build the project first with Gradle, just click on the `build` command in the _Gradle Tasks_ extension. Then check the _Spring Boot Dashboard_ extension if it already shows `soprafs23` and hit the play button to start the server. If it doesn't show up, restart VS Code and check again.

## Building with Gradle
You can use the local Gradle Wrapper to build the application.
-   macOS: `./gradlew`
-   Linux: `./gradlew`
-   Windows: `./gradlew.bat`

More Information about [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) and [Gradle](https://gradle.org/docs/).

### Build

```bash
./gradlew build
```

### Run

```bash
./gradlew bootRun
```

You can verify that the server is running by visiting `localhost:8080` in your browser.

### Test

```bash
./gradlew test
```

### Development Mode
You can start the backend in development mode, this will automatically trigger a new build and reload the application
once the content of a file has been changed.

Start two terminal windows and run:

`./gradlew build --continuous`

and in the other one:

`./gradlew bootRun`

If you want to avoid running all tests with every change, use the following command instead:

`./gradlew build --continuous -xtest`


## Roadmap
Potential improvements or extensions in the future may include:

- create chat room for the platform
- introduce more game based on our game platform
- change the external API for generate the word set in the game undercover

## Authors & Acknowledement
>Jiewen Luo, Ruirui Wang, Heqing Ren & Jiajian Zhu

>SoPra Team for the template and our TA Valentin Hollenstein

## License

Licensed under GNU General Public License v3.0
- See [License](LICENSE)
