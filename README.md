# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
## Server Design Document

https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=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-xw-cG7K2pM+SuJpSSX07AGMYh5DNDUPX3J3fGA2GnA3pgJyaAOh471zQEjrTrLyOfRmdRzDgP6MZcYy1baUuZdQDlzaLmXKlMWZRo2r0zbef9RPpzlaomIWSRO3RoHpj4XybQ-9DDH7xfMdY4RjjWJmfU7xb3NM2U8re4kgZgwoeWe68hi-PDBH2MYi4xTczn9LP8dtIJ4PmmxPO-g1GrWBO15e9M2bnjync+qfRupnF4yzU6cj+r-TDP48mJVyZpFNed5TD4w33+TfmV2eZg5jcXhYtdi9L97AYrCDxB+jgwWwtAvi0ltLWWrRjBq0y6h01T7gm3ZANwPAjI9AGHsdwi2IPFIX6gAoZUdjPWlwXTxYYEAaAwB8UYbQH2bwYYddPbcuEnLULHWuR-efYSebeaeNZgc-WAhDQXZXNMDHFkFvE-CPK4G5clIBFqdnXoJcZ3SgH3CAZQPiPHCvT3YncgiSSgkAaghDGnJVOnGPAGLve+QDe5FqMBRTWvC3WARAefNAQOdNa-McPlFcOBIAA
