vmrc-client: A CLI & API for  VMRC
===========

<!-- language: lang-none -->
    ____   ____  ____    ____  _______      ______  
    |_  _| |_  _||_   \  /   _||_   __ \   .' ___  |
     \ \   / /    |   \/   |    | |__) | / .'   \_|
      \ \ / /     | |\  /| |    |  __ /  | |
       \ ' /     _| |_\/_| |_  _| |  \ \_\ `.___.'\
        \_/     |_____||_____||____| |___|`.____ .'


 A Virtual Machine Image Repository & Catalog
Developed by the [Grid and High Performance Computing Group (GRyCAP)](http://www.grycap.upv.es) at the
[Universitat Politècnica de València (UPV)](http://www.upv.es).

Web page: http://www.grycap.upv.es/vmrc

0. Introduction
=================
VMRC is client-server system (based on Web Services) to index Virtual Machine Images (VMI)
along with its metadata (OS, applications, etc.). It supports matchmaking to obtain the appropriate VMIs
that satisfy a given set of hard (must) requirements and soft (should) requirements.

Current version: 2.1.2

This repository only includes the VMRC client. Additional packages available are:
  - [vmrc](http://github.com/grycap/vmrc) (The server-side part of the Repository & Catalog of VMIs)

You will require a running instance of the VMRC service to compile this code and to connect from this client.
If you are looking for a pre-compiled ready-to-use version of the VMRC client check the [webpage](http://www.grycap.upv.es/vmrc).

1. Features
============
+ Java-based API to interact with [VMRC](https://www.github.com/grycap/vmrc)
+ CLI (based on shell script) to interact with [VMRC](https://www.github.com/grycap/vmrc)


2. Requirements
===============
+ Java JDK 1.7+
+ Maven (to compile from sources)


3. Compilation & Installation
================================================
1. Clone the repository from GitHub
```
git clone https://github.com/grycap/vmrc-client.git
```
2. Make sure that the VMRC Server is up & running and listening at port 8080
  * This is the default configuration if you followed the instructions when installing VMRC.
  * During compilation of the client, the service WSDL is dynamically accessed to create some classes.
3. Compile from sources with the command:
```
 mvn package
 ```
The file $VMRC_CLIENT_LOCATION/target/vmrc-client.jar will be generated
4. (Optional) Generate the documentation for the client-side Java API
        mvn javadoc:javadoc
The documentation is generated in the target/site/apidocs folder.

The CLI tools are available in the tools folder
  * vmrc.sh: To register, query, list, deregister and change permissions of VMIs.
  * vmrc-admin.sh: To create, delete, list or update users, etc.

4. Usage
===========
1. You should use vmrc.sh and vmrc-admin.sh as the CLI tools for user and admin commands respectively.
2. There are sample VMI descriptors in the [src/test/vmis] directory
3. The API documentation is available in the [target/site/apidocs] folder.
   In particular, the org.grycap.vmrc.client.api.VMRCServerAPI provides a Java API to contact VMRC.

You have further information in the VMRC client manual available both in the [webpage](http://www.grycap.upv.es/vmrc) and in the [manual] folder at the repo. 
5. Additional Configuration
===========================
1. Create the file $HOME/.vmrc/vmrc_auth with user:password. This information is employed to contact the VMRC server.
2. Create the file $HOME/.vmrc/vmrc.properties with the following properties:
       vmrc.uri = http://machine:8080/vmrc/vmrc
  Customize it according to your deployment: machine/port/https.
