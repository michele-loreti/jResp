## jResp: a Java Environment for diStributed Programming

jResp is a Java framework that aims at providing programmers with a set of API that simplify design, development and coordintion of distributed systems. To cope with size, complexity and dynamism of contemporary software-intensive distributed systems jResp provides appropriate programming abstractions that permit to represent behaviors, knowledge and aggregations according to specific policies, and to support programming context-awareness, self-awareness and adaptation.
        

## Building

jResp is using the Gradle build automation tool for dependency managment and building. One can import the project directly in IDE that supports Gradle, or generate meta-data for an IDE. Currently supported IDE's for this project is Eclipse.

In order to build the project, open a terminal, and execute in the `CORE/` directory: `gradle build`

To build meta-data for Eclipse, open a terminal and execute in the `CORE/` directory: `gradle eclipse`

To build a standalone JAR library, opeen a terminal and execute in the `CORE/` directory: `gradle standaloneJar`

