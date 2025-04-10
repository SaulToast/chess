# My notes

## January 8th

You learn the most when you are struggling or stuck

***Git, what is it?***

version control system. It keeps track of every file modification.


## January 13th
**Java fundamentals**

Differences between Java and C++
- Built in garbage collection
- references instead of pointers
- data types are always the same size in java
- hybrid compiled and interpreted

C++ is an example of compiled code. Source code directly compiles to executable code
and thus needs to be recompiled for different computer hardware. It does run the fastest though

Python is an example of interpreted code. Reads source code and executes it. Slower to execute

Java compiles to Java Byte Code which can be run through any OS that has a
JVM.


Java needs to have a Main class with a main method. 
A lot of errors deal with the vm finding the class files

You can have multiple main methods, which is why you need to specify what main function to use

static method is one that doesn't need an object to use. It also can't modify member variables

Java has wrapper classes for all primitive objects which contain various useful functions.


## January 15th

**writing java classes**

## January 22nd

**Classes and Objects**

static methods and variables

Static methods can't access instance variables and methods.
They can only access other static stuff. Usually if you write a method that doesn't
reference any instance variables, you should make it static

**Organizing Classes**

Put all the static variables first, then the static methods

I like to have all the variables, both static and instance, and then methods


Data classes. They have state but not behavior. They are just useful for moving data
Use records instead. One of the downsides is that they are immutable and don't have setters

**Inheritance**

One of the most powerful features in OOP.

Using a superclass gives polymorphism. One variable can point to many different objects
you can override methods and add new ones. You can call a method without exactly knowing what the object is.

the protected keyword is like static but for a whole family of classes

abstract methods have to be implemented by a child class. A class with abstract methods must also be abstract

classes are either concrete or abstract

*final* keyword 

## January 27th

*more notes on classes*


**Interfaces**

when a class inherits from an interface, it doesn't inherit any code. 
A class can only have one superclass, but you can implement as many interfaces as you want

creating an interface

```Java
public interface Moveable {
    void go();
}
```
To Implement an interface, use the "interface" method
```Java
public class Person implements Moveable {

    @override
    public void go() {
        // code to make it go
    }
    
}
```

If classes share code, take that and make an abstract class. Abstract classes
can implement interfaces.

Determining an object's class
```
o.getClass();

if (o instanceof ChessPiece) {...}
```


**Copying Objects**

two ways to copy an object

1. shallow copy
2. deep copy

usually you want a deep copy
immutable objects don't need to be deep copied because it can't be changed

2 ways to implement copying a class

1. "Clone" method on each class
2. Copy constructors

Every java object has a clone method, but only some implement it.

**Exceptions and Error Handling**

You need to write code so it handles all the possible things that can happen
* bad input
* bugs
* runs longer than expected
* run out of memory
* open a file and it fails
* etc.

Crashing is always unacceptable. Gracefully exit if things do go wrong

Low level code has the error. Create an exception object. Throws it up the runtime stack until it
is caught. Code higher up deals with the errors.

"Swallowing" an exception is bad practice. 

When writing a method, you can handle the exception or declare your function to throw it higher up.

Different types of Throwable

* *Errors* are thrown by the jvm. You can't do much about that. Write the program in a way
that doesn't create errors.
* *RuntimeException* can happen anywhere in the code. Assumed that anything can throw these. Fix by
fixing your bugs.
* *Checked Exceptions* are enforced by the compiler. Write code to handle the exceptions

put cleanup code in the finally block

## January 29th

**Inner Classes**

What is an iterator? An object that lets you iterate over the values in a data structure

```java
public class DataStructure{
    private final static int SIZE = 15;
    private int[] arrayOfInts = new int[SIZE];
    // more class stuff
    
    private static class DataStructureIterator{
        
        private int[] array = null;
        private int increment = 0;
        // start stepping through the array from the beginning
        private int next = 0;
        
        // more iterator stuff
    }
}
```

If the inner class isn't static, it has access to the data of the outer class.
In the example above, the iterator would be able to access the array from DataStructure

A *Local Inner Class* is defined within a method. Probably don't use this too often, but it does exist.

An *Anonymous Inner Class* is a local inner class without a name

**Collections**

many different data structures to be used when regular arrays do not suffice.

* List
* Set
* Queue
* SortedSet
* etc.

They only store objects and not primitives
Some data sets need an object to be comparable. If you use these, you must implement the Comparable Interface

***Maps***: a collection of key value pairs.

Two types of equals
1. identity - only the same object. Uses address
2. value - another object with the same values

You can't change information in objects that are used as keys in data structures (Must remove and re-insert)


## February 3rd

### Generic Types:

This is a basic example of using generic types
```java
public class Pair<T, U> {
    private T val1;
    private U val2;
    public Pair(T val1, U val2){
        this.val1 = val1;
        this.val2 = val2;
    }
}

var pair3 = new Pair<String, Integer>("Hello", 2);
```

***Wildcards***: the syntax is "?"

```java
public class GenericClassExample<T>{
    public void method1 (List<? super T> param) {}
    public T method2() { return null;}
}
```

### Lambdas

*An anonymous function*

A part of functional programming is treating a function like a piece of data. You can pass it around other functions.
Lambdas are about deferred execution.

```java
// syntax for lambdas
// (parameters) -> { expression/body } 
```

Lambdas can only be used with functional interfaces i.e. interfaces with only one abstract method

example
```java
import java.util.function.Predicate;

public class Main {
    public static void main(String[] args) {
        Predicate<Integer> isEven = n -> n % 2 == 0;
        
        System.out.println(isEven.test(4)); // Output: true
        System.out.println(isEven.test(5)); // Output: false
    }
}

```

Creating APIs with Lambdas: (APIs just means a bunch of functions)

Write your function so they use functional interfaces

Using lambdas reduces the amount of functions you need to use. It also makes your code more concise. 

### Method References 

if you already have a function that you want your lambda to do, you can just pass in that function references instead

```java
// static method: ClassName::methodName
// Instance method: objectReference::methodName
// constructor invocation: ClassName::new
```

### Input/Output


## February 5th

***JSON and Design Principles***

How to store data so it persists? In files.
How do you represent that data so it can be shared?

Two options are
1. xml
2. json

Am I going to represent my data as text or bytes?
These two are textual

### xml example

```xml
<?xml version="1.0" encoding="ISO-8859-1" ?>
<Catalog>
    <CD>
        <Title> Hide your heart </Title>
        <Artist> Bonnie Tyler </Artist>
    </CD>
</Catalog>
```

You can also use attributes and nested elements.
It's good to know about because a lot of data is stored like this. But mostly use json now

### JSON

| Type    | Example               |
|---------|-----------------------|
| string  | "crockford"           |
| number  | 42                    |
| boolean | true                  |
| array   | [null,42,"crockford"] |
| object  | {"a":1}               |


example:

```json

{
  "CATALOG": [
    {
      "CD": {
        "TITLE": "Hide your heart",
        "ARTIST": "Bonnie Tyler"
      }
    }
  ]
}
```

Can this be made more concise?

```json
{
  "catalog" : [
    {
      "title": "Hide your heart",
      "artist": "Bonnie Tyler"
    }
  ]
}
```

### Parsing

1. Streaming parsers
   1. tokenizers that return one token at a time from the json data file
2. DOM Parsers
   1. convert json test to an in-memory tree data structure
3. Serializers/Deserializers
   1. use a library to convert from json to java objects
   2. **gson** is a popular library


This string parser is useful for when the file is too big to use a DOM parser feasibly.
Another case is when you only care about one piece of data.

```java
import com.google.gson.GsonBuilder;
import java.io.IOException;

private void generate(Catalog catalog, File file) throws IOException {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    String jsonString = gson.toJson(catalog);
    
    // then you can write string to file
}

```

How do I now deserialize this object?

### Design Principles

The bigger a program is, the more important it is to organize it well.
This will help it be more maintainable and understandable.

Focus on these goals
1. It does what the customer wants it to do
2. It is easy to understand, debug, and maintain as possible
3. It is extensible to requirement changes i.e. easily add new features

**Design is Inherently Iterative**

It's not always possible to design something on paper without starting to code
Design some stuff, start to code, fix the design, code it up, repeat

**Abstraction**

helps us deal with complexity. easier to read. don't have to understand all the code ever written

**Naming**

Keep naming clear. Think about whether using verb or noun


## February 12th

Handlers do communication with the client. They take the json objects and passes the data on

### Network Programming

**Client Connection to Server**

client and server can be written in different languages, it doesn't matter which.
When connected, the client and server can send bytes to each other

In order to connect, both the client and server machines need an IP address (e.g., 128.187.80.20)

We often use domain names instead of IP addresses directly, but the domain name gets converted (domain name service DNS)

A server often is running multiple programs at once. Because of this, a port number is needed
to provide more specificity when a client tries to connect. 

### HTTP Get Requests

URL:
- https://www.google.com:443/images/branding/googlelogo etc.

parts:
- protocol
- domain name
- port number
- path

**Get Request**

used when trying to get information from an url

GET <\path>
Header: stuff"

**Post Request**

often used on forms, when a client needs to send information to a server.
Has a body

POST <\stuff>
Headers:
\n
Response Body: keywords=byu&cs&240

### Using HTTP for a web API

### cURL

command line tool

curl -X GET https://www.byu.edu/ downloads the specified webpage

can include a -v before the -X to see what message is getting sent

chess example:

curl -X POST http://localhost:8000/user -d '{username: "bob", password: "bob", email: "bob"}'

curl -X GET http://localhost:8000/game -H 'Authorization: <\ put auth token here>'

more information on the slides

**Postman**

another GUI tool similar to curl

## February 18th

### Using Spark

```java
import spark.Spark;

public class SimpleHelloBYUServer {
    public static void main (String[] args){
        Spark.get("/hello", (req, res) -> "Hello BYU!");
    }
}
```

Defaults to port 4567, but use Spark.port() to specify.
You can use lambda functions for the handler, but if they get too long use a separate method.
You can reference that using a method reference

***Named Parameters***

look at the slides

### Filters

an http handler you can put in front of the other handlers. It was a way of avoiding duplicate code.


### Chess Server Implementation

What do Web API Handlers do?
- if an auth token is required, validate the auth token
  - go to DAO to check database
- Deserialize JSON request body to Java request object
- call service class
- receive Java response object from service
- serialize Java response object to JSON
- send http response back to client

```java
import dataaccess.DataAccessException;
import java.util.HashMap;

interface UserDao {
   void createUser(UserData user) throws DataAccessException;

   UserDAta getUser(String username) throws DataAccessException;
}

class MemoryUserDao implements UserDao {
   private Map<String, UserData> users;

   public MemoryUserDao() {
      users = new HashMap<String, UserData>();
   }
   
   public void createUser(UserData user) throws DataAccessException {
       users.put(user.getUsername()); 
   }
}
```

## February 19th

### Testing
Automated testing is useful, but it can be pretty tough
- load testing
- stress testing
- etc.

Test Driven Development: when you have the test cases first so you can know
when you are finished writing the code.

It's easy to write something that works some of the time, but it's much harder to write something that works 100% of the time

***Unit Testing***

Whenever you write a function or a class, you should write tests that verify the pieces of code you have created. This way the pieces are more likely to work as you integrate them together.

Grow the software organically.

When following this pattern, if the system breaks when adding a new part, you essentially know where the bug is already.

- The tests are run by a "test driver", which is a program that just runs all of the unit test cases.
- It must be easy to add new tests to the test driver
- can't require manual work

Good unit tests must be:
- Fast
- Cohesive: test one specific thing and have a good name
- Independent: they don't have any side effects. Order doesn't matter
- Unique: don't repeat checks other tests already do

***JUnit Testing Framework***

A library that makes it easy to write test cases

1. Create a test folder which is a sibling to the src folder
2. For every class, have a corresponding test class

you need to create a dependency for JUnit and then you can import things

```java
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class WordExtractorTest{

  private WordExtractor extractor;
  private ArrayList<> expected;

  @BeforeEach
  public void setUp(){
    // initialize the shared stuff
    var expected;
    var actual;
  }

  @Test
  public void testExample(){
    // put test code here
    Assertions.assertEquals();
  }
}
```

BeforeEach decorator is used to reduce duplicated code.

Useful testing decorators:
- @Test
- @BeforeEach
- @AfterEach
- @BeforeAll
- @AfterAll
- @Timeout

***Code Coverage***

How do you know when you have enough tests?

1. Line Coverage: Does every line in my program get tested? Or at least most of them
2. Statement Coverage: same as line coverage but for each statement
3. Branch Coverage: how many unique code branches were covered

Testing is valuable when you don't have enough stuff to actually run the full program. 


## February 24th

### Writing Quality Code

Why is it important to write quality code?
- more time is spent maintaining code
- multiple people will work on a project over time
- creating software is a combination of engineering and art


ideas for nice code
- use whitespace well
- consistency
- naming conventions
- make useful comments
- documentation comments



Avoid these things
- magic numbers: unexplained numbers in equations

Good Naming Principles:
- strong cohesion
  - method does only one thing and the name describes what it does.
- don't hide processes in functions. Reflect them in the name
- method names should usually be either verb phrases or what the function returns
- names should reflect the purpose

comments are good, but not needing to comment is even better. If you think something
should be commented, first think how you could decompose the function/improve the code.

The smaller number of parameters possible is best. Order like this:
1. in
2. in-out
3. out

always initialize data

***Code Layout***
- organize methods into "paragraphs"
- when we nest, we indent
- 


## February 26th

### Relational Databases

They manage massive amounts of data efficiently. They allow multiple people to access data simultaneously.

Every language has a DB API which is not specific to a database product. Your program needs to have a db driver that connects the db api to the specific database that you are working with.

***Embeded vs. Client/Server***

An embeded db runs locally and just the local machine has access to it. A client/server db runs on its own machine and multiple machines can access it

In a relational data model, data is stored in tables consisting of columns and rows. Each row has a *primary key* which is a unique identifier for that row.


| id | name | email_address |
| -- | -- | -- |
| 1 | 'ann' | 'ann@cs.byu.edu' |
| 2 | 
| 3 | 

columns can store *foreign keys* that are the *primary keys* of other tables.

Types of Object Relationships
1. one-to-one: each row in T1 is connected to one row in T2
2. one-to-many: each row in T1 is connected to many rows.
    - put the foriegn key in the many table
3. many-to-many: one to many but it goes in both directions
    - create a relationship table whose rows contrain foreign keys of related objects


***Diagraming Tables***

- Entity Realationship Diagram (ERD)
- Database Model (UML)

### SQL

Designed for working with a database. It is one of the most used languages.

Three parts:
1. Data Definition Lanugage (DDL)
2. Data Manipulation Language (DML)
3. Data Queury Language (DQL)

Data Types:
- character strings
  - CHARACTER(n) fixed-width n-charactr string, padded with spaces
  - VARCHAR(n) variable-width string with a maxiumum size of n characters
- integers
  - INTEGER AND SMALLINT
  - FLOAT, REAL, and DOUBLE PRECISION
  - NUMERIC(precision, scale) or DECIMAL(precision, scale)
- large objects
  - BLOB binary large object (images, sound, video, etc.)
  - CLOB character large object (text documents)
- dates
  - DATE
  - TIME
  - TIME WITH TIME ZONE
  - TIMESTAMP: date and time together. Mostly use this
  - TIMESTAMP WITH TIME ZONE

  ```sql
  create table book if not exists
  (
    id integer not null primary key auto_increment,
    title varchar(255) not null,
    author varchar(255) not null,
    genre varchar(32) not null,
    category_id integer not null,
    foreign key(genre) references genre(genre),
    foreign key(category_id) references category(id)
  );

  drop table book;
  drop table if exists book;

  insert into book
  (title, author, genre, category_id) values ('The Work and the Glory', 'Gerald Lund', 'HistoricalFiction', 3)
  ```

## March 3rd - More SQL stuff

### Queries

```sql
SELECT column, column
FROM Table, Table
WHERE Condition
```

cartesian product is all the combinations of the two sets

```sql
SELECT member.name, book.title
FROM member, books_read, book
WHERE member.id = books_read.member_id AND
    book.id = books_read.book_id
```

This is old syntax

This is newer syntax

```sql
SELECT member.name, book.title
FROM member
INNER JOIN books_read ON member.id = books_read.member_id
INNER JOIN books ON books_read.book_id = book.id
WHERE genre = "NonFiction"
```


### Transactions

you want all or nothing. You don't want half of your sql statements to run.

```sql
BEGIN TRANSACTION
# statement one
# statement two
# etc.
COMMIT TRANSACTION; or ROLLBACK TRANSACTION
```

If a machine dies, when the database is rebooted and the rollback occurs so nothing actually happens.

Transactions also keep execution sequential.

each statement commits automatically, so you need to specify when you want to group a transaction.

### Using SQL in Java

- load database driver
- open a database connection
- start a transaction
- execute queries and/pr updates
- commit or rollback the transaction
- close the db connection


use a try-with-resources statement to automatically close the connection

you should have string constants that contain the sql statements

***Close the objects you create***
 
you can use placeholders/parameters "?" and you update them later

you have to specify that the db should return auto-generated keys

## March 5th - mySQL

hashing passwords in the database. You want to store the hash of the passwords so the raw passwords aren't getting saved in the db.

## March 10th - Logging

Java has a built in logger with different methods for different levels of logs.

useful to set up a logging.properties file

```
handlers=java.util.logging.FileHandler, java.util.logging.ConsoleHandler
java.util.logging.ConsoleHandler.level=FINE
...

```

### Defensive Programming

As we program, we sometimes make assumptions about the state of our code at that point.
If the assumption is correct things often work out, but if our assumption are wrong we tend to get bugs.

Assertions are used to take assumptions we make and actually put that into code.
Most languages have an assert statement.

Why?
- helps us find and confront bugs
- we can catch problems closer to the source
- use them

In java, assertions are ***disabled*** by default.

***Parameter Checking***
two ways
1. asserts
2. if/throw

When to use asserts?
- you control the caller so you can go fix it

***Debugging***
ways to debug
- logging / trace debugging
- asserts / exceptions
- using the debugger
- binary search debugging
- code review
- take a break
- 

Code Review
- Have others review your code 

Steps to debug:
1. have a way to reproduce the bug. Can't fix what you can't break
2. simplify that test case


## March 12th - Phase 5 overview

For phase 5 we are building the first two client UI pieces. This will be done in the console.

Think of the different UIs as a stack. When you quit one, just go back to the previous one.

Read, Eval, Print

```java
class ServerFacade {

  private final String serverUrl;

  public RegisterResult register(RegisterRequest req) {...}
  public LoginResult login(LoginRequest req) {...}
 
}
```

Take the java objects, serialize it to json, send it to the server, deserialize the result into a new java object.

### Console UIs

In the beginning there were consoles. 

Terminal control codes
- clear the terminal
- set the cursor location
- set the background color and text color
- set text attributes such as bold, faint, italic, underline, blinking

## March 17th - Computer Security

If you are building a piece of software for production, security is a big factor. Hackers try to compromise systems in many ways.
- Gain unauthorized access to data
- Gain unauthorized access to computers
- Disable a system so it can't be used

Security Goals:
- Data Confidentiality
- Authentication
- Data Integrity
- Non-Repudiation
    - verifying the origin or authorship of data
    - Can the data be trusted

### Cryptographic Hash Functions

A way to map data to a repeatable output that can't be restored to the original data

input -> digest

Features of the digest
- one-way
- deterministic: the same input gives the same digest
- fixed-size
- pseudo-random: small changes in the input makes the digest completely different

Hash collisions are possible but are highly unlikely.

Historical Algorithms:
- MD-5 128-bit digest
- SHA-1 160-bit digest

Both of these algorithms have be cracked. They are occasionally used for less secure applications.

Modern Algorithms:
- SHA-2 supersedes both the historical ones
- check the slides for the rest

Many languages have built in libraries

### Password Hashing

problems with storing password hashes
- Users that have the same password would have the same hash values
- attackers could still crack passwords using a dictionary attack

Add **Salt** to passwords to mitigate attacks. The salt is just a random string appened to the password

It's a good idea to use a costly algorithm when storing passwords for an added layer of protection.

### Data Encryption

has encryption and decryption keys and without them you can't read the data.

***Encryption***: is the process of encoding data so that only authorized parties can read it.
***Decryption***: is the process of decoding data back to its original form.

- Plaintext: unencrypted data
- Ciphertext: encrypted data
- Key: used to encrypt or decrypt data
- Key Size: the number of bits in the key

The longer the key, the more secure it is.

Two Categories:
1. Symmetric Key: only one key and it is used for both encryption and decryption
2. Asymmetric Key: two different keys are used for encryption and decryption

Modern Symmetric Algorithms:
- Advanced Enryption Standard(AES)
- Blowfish
- TwoFish

## March 24th - Websocket

websocket is more designed for messaging, games, etc. than http

http isn't great for peer-to-peer. Short-polling and long-polling

how does websocket work? It opens a connection to a server which stays open. It allows the server to notify all clients
whenever something interesting happens.

the connection starts out as http, the client sends a message to the server and upgrades it to the websocket connection.

Server has to keep track of all of the connections it is servicing.

Ping pong, the server pings the client and if the client doesn't respond it is dropped.

```java
@WebSocket
public class WSServer {
  public static void main(String[] args) {
    Spark.port(8080);
    Spark.websocket("/ws", WSServer.class);
    Spark.get("/echo/:msg", (req, res) -> "HTTP response: " + req.params(":msg"));
  }

  @OnWebSocketMessage
  public void onMessage(Session session, String message) throws Exception {
    session.getRemote().sendString("WebSocket response: " + message);
  }
}
```

```java
public class WSClient extends Endpoint {

  public static void main(String[] args) throws Exception {
    var ws = new WSClient();
    Scanner scanner = new Scanner(System.in);

    System.out.println("Enter a mesasge you want to echo");
    while (true) {
      ws.send(scanner.nextLine());
    }
  }

  public Session session;

  public WSClient() throws Exception {
    URI uri = new URI("ws://localhost:8080/ws");
    WebSocketContainer container = ContainerProvider.getWebSocketContainer();
    this.session = container.connectToServer(this, uri);

    this.session.addMessageHandler(new MessageHandler.Whoel<String>() {
      public void onMessage(String message) { System.out.println(message); }
    });

  }

  public void send(String msg) throws Exception {
    this.session.getBasicREmote().sendText(msg);
  }
}
```

## March 26th - computer security

Foundational concepts:
- cryptographic hash functions
- data encryption
- secure key exchange
- public key certificates
- digital signatures

### Asymmetric (Public) Key Encryption

popular algorithms
- RSA
- ECC (Elliptic Curve Cryptography)

The downside of pulbic key encryption is that it is slow for sending large amounts of data

Use public key encryption to send the key for the fast, symmetric AES key. This is known as *Secure Symmetric Key Exchange*.

Applications of Encryptions
- protect data as it traverses a computer network to prevent attackers form reading the data
- protect data that is stored in a database or file (data at rest)

How do we know we can trust a given certificate? They are created by trusted organizations called "Certificate Authorities."


## March 31st - Concurrency and Multi-threaded Programming

Concurrency is when you run multiple tasks (functions) at the same time.

App
 | 
OS
 | 
Hardware

The hardware is capable of running many things at once, the os manages threads and is the interface between hardware and software.

For anything that takes a relatively long time you should consider putting that on a new thread.
Every thread has its own runtime stack. 

When using threads, they don't wait on each other. Instead they run concurrently. This means that the program doesn't completely terminate until all the threads are done.

This can create a problem where the interactions between threads are nondeterministic

**Concurrent**: each task has a turn to run on the core. This is controlled by the OS
**Parallel**: the tasks are acutally running at the same time. Needs multiple cores or CPUs
**Sequentially**: One task after another

When using threads, another important concept is **Synchronization**. This allows some threads to wait for others

Why does concurrency make programming harder?
- shared resources
- race conditions: when two threads are racing to perform a task. Leads to differing result on subsequent runs.

### Writing Thread-safe Code

basic concepts
- database transactions
- synchronized methods

**Critical section**: a section of code that access a shared resource

How can we make sure that only one thread executes a critical section at a time? Use a ***synchronized method***


## April 7th - More on multi-threading

Synchronized method - the method can only be executed one at a time on one object

Sometimes this won't work so you need to use a synchronized code block with a custom lockObject

### Atomic Variables

this is another way to protect critical sections. Java has several built in atomic data types

sometimes critical sections are just simple operations. And synchronized methods are slow
- x++
- x += 10
- etc.

```java
public class AtomicIncrement {
  private AtomicIntger x = new AtomicInteger(0);

  public void increment() {
    x.incrementAndGet();
  }

  public int getX() {
    return x.get();
  }
}
```

### Command-line builds 

What does "building" software entail

1. retrieve source code (i.e. from github)
2. download dependencies
3. compile code
4. compile and run automated tests
5. measuring test coverage
6. package compiled code into a distributable formate
7. install/deploy the software 

different build tools based on languages:
- java: maven, gradle
- c++: Make
- javascript: npm
- Python: Poetry, PyBuilder
- Universal: Shell scripts
