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
