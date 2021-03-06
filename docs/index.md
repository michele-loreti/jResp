jResp is a Java framework that provides programmers with a set of APIs that simplify design, development and coordination of distributed systems. To cope with size, complexity and dynamism of contemporary software-intensive distributed systems jResp provides suitable programming abstractions to represent behaviors, knowledge and aggregations according to specific policies, and to support programming context-awareness, self-awareness and adaptation.

This project is a fork of the project started in the context of [EU Project ASCENS](http://www.ascens-ist.eu) that is still available [here](http://jresp.sourceforge.net).

# jResp architecture

A jResp system consists of a set of components executed over a distributed infrastructure. Components are implemented via the class ```Node```. The architecture of a node is shown in the following figure:

![alt text](./images/node_structure.png "Node Structure")

Nodes are executed over virtual machines or physical devices providing access to input/output devices and network connections. A node aggregates a knowledge repository, a set of running processes, and a set of policies. Structural and behavioral information about a node are collected into an interface via attribute collectors. Nodes interact via ports supporting both point-to-point and group-oriented communications.

## Knowledge

The interface ```KnowledgeManager``` identifies a generic knowledge repository and indicates the high-level primitives to manage pieces of relevant information coming from different sources. This interface contains the methods for withdrawing/retrieving/adding piece of knowledge from/to a repository. A knowledge manager based on Tuple Space is integrated is currently in jRESP.  Thus, items are defined as tuples, i.e. sequences of  Objects, that can be collected into a knowledge repository. They can be retrieved/withdrawn via pattern-matching through templates, consisting of a sequence of actual and formal fields.

External data can be collected into a knowledge repository via sensors. Each sensor can be associated to a logical or physical device providing data that can be retrieved by processes and that can be the subject of adaptation. Similarly, actuators can be used to send data to an external device or service attached to a node. This approach allows jRESP processes to control exogenous devices that identify logical/physical actuators.

The interface associated to a node is computed by exploiting attribute collectors. Each of this collector is able to inspect the local knowledge and to compute the value of the attributes. This mechanism equips a node with reflective capabilities allowing a component to self-project the image of its state on the interface. Indeed, when the local knowledge is updated the involved collectors are automatically activated and the node interface is modified accordingly.

## Network Infrastructure

Each Node is equipped with a set of ports for interacting with other components. A port is identified by an address that can be used to refer to other jRESP components. Each node can be addressed via a pair composed of the node name and the address of one of its ports. The abstract class ```AbstractPort``` implements the generic behaviour of a port. It implements the communication protocol used by jRESP components to interact with each other. Class ```AbstractPort``` also provides the instruments to dispatch messages to components. However, the methods used for sending messages via a specific communication network/media are abstract. The concrete classes defining specific kinds of ports extend AbstractPort to provide concrete implementations of the above outlined abstract methods, so to use different underlying network infrastructures (e.g., Internet, Ad-hoc networks,...).

Currently, four kinds of port are available: ```SocketPort```, ```ScribePort```, ```ServerPort``` and ```VirtualPort```. The first one implements point-to-point and group-oriented interactions via TCP and UDP, respectively. In particular, ```SocketPort``` implements group-oriented interactions in terms of a UDP broadcast. Unfortunately, this approach does not scale when the size of involved components increases. To provide a more efficient and reliable support to group-oriented interactions, jRESP provides the class ```ScribePort```. This class realises interactions in terms of the P2P and multicast protocols provided by [Scribe](http://www.freepastry.org/SCRIBE/default.htm) and [FreePastry](http://www.freepastry.org). A more centralized implementation is provided by ```ServerPort```. All messages sent along this kind of port pass through a centralize server that dispatches all the received messages to each of the managed ports. Finally, ```VirtualPort``` implements a port where interactions are performed via a buffer stored in memory. A VirtualPort is used to simulate nodes in a single application without relying on a specific network infrastructure.

## Behaviors

Behaviour of a component is defined via the abstract class ```Agent```, which provides the methods implementing the actions that can be used to interact with local knowledge and remote components. In fact, they can be used for generating fresh names, for instantiating new components and for withdrawing/retrieving/adding information items from/to shared knowledge repositories. The latter methods extend the ones considered in knowledge with another parameter identifying either the (possibly remote) node where the target repository is located or the group of nodes whose repositories have to be accessed. As previously mentioned, group-oriented interactions are supported by the communication protocols defined in the node ports and by attribute collectors.

## Policies

In jRESP policies can be used to regulate the interaction between the different internal parts of components and their mutual interactions. When a method of an instance of class ```Agent``` is invoked, its execution is controlled by the policy associated to the node where the agent is running. The policy can then allow or forbid the execution of the action (for instance, by generating an exception when some access right has been violated) and, possibly, dynamically add additional actions to the agent. The authorisation approach is based on the ```attribute-based access control``` model [ABAC](https://en.wikipedia.org/wiki/Attribute-Based_Access_Control). Each action to authorise and its context (given by the interfaces of the involved components) are represented by an attribute-based request that is then evaluated by the policy currently in force. 

By default, each node is instantiated with the policy allowing any operation. Different kinds of policies can be easily integrated in jRESP by implementing the interface ```INodePolicy```. Currently, this interface is implemented to support policies of different structures; namely we have

1. ```SinglePolicy```, i.e. a single authorisation policy in force in a node.

2. ```PolicyAutomaton```, i.e. an automaton whose states define the policies possibly in force in the node; the policy of the current state corresponds to the current policy. 

For both the policy type, there is available an implementation based on [FACPL](http://facpl.sf.net), a Java framework to design and implement attributed-based access control policies. Indeed, a ```SinglePolicy``` can be instantiated with a FACPL policy, while each state of a ```PolicyAutomaton``` corresponds to a FACPL policy; the automaton transitions are defined by conditions on the attributes forming the authorisation request to evaluate. 

Intuitively, a FACPL policy is formed by a sequence of basic access control rules, either positive (```permit```) or negative (```deny```), whose authorisations are combined together by a combining algorithm: ```permit-unless-deny``` or ```deny-unless-permit```. When both ```permit``` and ```deny``` authorisations are obtained, the former gives precendence to ```deny```, the latter vice versa. Each rule defines upon the attibutes of authorisation requests the access controls for actions and possibly additional actions to enforce in the Agent as result of the rule evaluation. Thus, rules use pre-defined attribute names to specify controls on action paramenters (e.g, ```action/id``` to check the action type: PUT, GET, ...) or on the context (e.g, ```subjcet/id``` to check the name of the component executing the action). 

# Getting Started

1. Clone the git repository in your computer with

```
git clone https://github.com/michele-loreti/jResp.git
```

2. Follow the build instructions at https://github.com/michele-loreti/jResp/blob/master/README.md which include also instructions to build meta-data for Eclipse.

If needed, you can update your jRESP installation you have to execute:

```
git pull 
```

## Create a new jResp project in Eclipse

1. Create a Java Project 

2. Open the MANIFEST.MF file under META-INF folder and add ```org.cmg.jresp.core```, ```org.cmg.jresp.pastry```, ```org.cmg.jresp.simulation``` among the “Required Plug-ins"


## Export Executable Code

If you have an executable Java you can create an executable jar. 

1. Select your project and click the menu “File -> Export”

2. Select “Java -> Runnable JAR File"

3. Select your “Lunch configuration” and choose a ```<myjarfile>.jar``` name 

4. Tick the “Extract required libraries into generated JAR”

5. Press “Finish” button

After that you can execute you program by running

```
java -jar <myjarfile>.jar
```
