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
 Developed by the Grid and High Performance Computing Group (GRyCAP) at
 Universitat Politecnica de Valencia (UPV)
 http://www.grycap.upv.es/vmrc


VMRC is client-server system (based on Web Services) to catalog and store Virtual Machine Images (VMI)
along with its metadata (OS, applications, etc.). It supports matchmaking to obtain the appropriate VMIs
that satisfy a given set of hard (must) requirements and soft (should) requirements.

Current version: 2.1.2

This bundle only includes the VMRC client. Additional packages available are:
  - vmrc (The server-side part of the Repository & Catalog of VMIs)
  - vmrc-gui (simple web-based frontend to list VMIs from the VMRC Server with a web-browser)  

1. Features
============
+ Java-based API to interact with the VMRC Server
+ CLI to interact with the VMRC Server


2. Requirements
===============
+ Java JRE 1.7+
+ (Optional) Maven + Java JDK 1.7+ to compile from sources


3. Compilation & Installation
================================================
  1. Unpack the vmrc-client package into a destination folder
  2. Make sure that the VMRC Server is up & running at port 8080
     (During compilation of the client, the service WSDL is dynamically accessed to create some classes)
  3. Compile from sources with the command: mvn package
      - The file $VMRC_CLIENT_LOCATION/target/vmrc-client.jar will be generated
  4. (Optional) Generate the documentation for the client-side Java API
        mvn javadoc:javadoc
      The documentation is generated in the target/site/apidocs folder.

4. Usage
=========
  1. You may use vmrc.sh and vmrc-admin.sh as an entry point to the CLI for user and admin commands respectively
  2. There are sample VMI descriptors in the [samples] directory
  3. The API documentation is available in the [apidocs] folder.
       - The org.grycap.vmrc.client.api.VMRCServerAPI provides a Java API to contact VMRC Server.

5. Additional Configuration
===========================
  1. Create the file $HOME/.vmrc/vmrc_auth with user:password. This information is employed to contact the VMRC server.
  2. Create the file $HOME/.vmrc/vmrc.properties with the following properties:
       vmrc.uri = http://machine:8080/vmrc/vmrc (customize it according to your deployment: machine/port/https)
