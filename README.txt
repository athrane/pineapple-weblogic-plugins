------------------------------------------------------------
NOTES ON SETTING UP IDE + CHECKING OUT THE PROJECT
------------------------------------------------------------

1) IDE: Eclipse
-----------

1.1) Download Eclipse and install it.

2) Maven 
---------

2.1) Download Maven 3.5.3 and install it.

2.2 ) Add $M2_HOME/bin to your $PATH.

2.3) Create a MAVEN_OPTS environment variable and set its value to: 

-Xms512M -Xmx512M

2.4) in the settings.xml file add the java.net repository to get access to J2EE 1.5 jars:

Add this stanza to repositories:

<repository>
  <id>java.net</id>
  <url>http://download.java.net/maven/1</url>
  <layout>legacy</layout>
</repository>				

For more info: https://maven-repository.dev.java.net/

3) Add third party jars to local repository
----------------------------------------------

Since the Pineapple uses some third party jars that are not hosted on the Maven 
repositories you will need to manually install the jars into your local 
repository yourself. 

3.1) Add WebLogic 12.1.2 jar to local Maven 3.0 repository

Use the WebLogic jar builder tool to build a JDK 1.8 client jar from a WebLogic 12.1.2 
installation: http://docs.oracle.com/cd/E24329_01/web.1211/e24378/jarbuilder.htm

Then run the following commands from the directory where the wlfullclient.jar was created 
to install the jar into your local repository.

mvn install:install-file -DgroupId=oracle -DartifactId=weblogic-full-client -Dversion=12.1.2 -Dpackaging=jar -Dfile=wlfullclient.jar -DgeneratePom=true

4) Create Maven based workspace in Eclipse
-----------

4.1) Open Eclipse

4.2) Create a new workspace named: c:\projects\pineapple-weblogic-plugins-ws

5) Configure Maven in Eclipse 
-----------

5.1) Open the Eclipse workspace: c:\projects\pineapple-weblogic-plugins-ws

5.2) Configure the Maven settings file to use by selecting Windows -> Preferences 
-> Maven -> User Settings and browse to the location of your Maven settings file.
Select the file settings.xml.
Click Apply.

6) Import Maven projects into Eclipse from GitHub
-----------

6.1) Open the Eclipse workspace: c:\projects\pineapple-weblogic-plugins-ws

6.2) Import projects by going to File -> Import ->  Git -> Projects from Git -> Clone URI 

URI: https://github.com/athrane/pineapple-weblogic-plugins.git
Host: github.com
Repository Path: /athrane/pineapple-weblogic-plugins.git
Protocol: HTTPS
User: your-GitHub-user
Password: your-GitHub-PWD
and click next.

6.3) Branch Selection

Select "master" and click next.

6.4) Local Destination

Directory: C:\Users\myuser\git\pineapple-weblogic-plugins
Initial branch: "master"
Configuration -> Remote name: origin
and click next.

6.5) Select a wizard to use for importing projects
Click cancel.

6.6) Import projects by going to File -> Import ->  Maven -> Existing Maven Projects 
and browse to the Git working directory at: C:\Users\myuser\git\pineapple-weblogic-plugins
Select all sub projects and click Finish.

7) Configure code template in Eclipse pineapple-weblogic-plugins-ws workspace
-----------

7.1) Open the Eclipse workspace: c:\projects\pineapple-weblogic-plugins-ws

7.2) Open Eclipse -> Windows -> Preferences -> Java -> Code Style -> Code Templates
-> Import -> C:\Users\myuser\git\pineapple\src\checkstyle\maven-eclipse-codestyle.xml
and click Apply.

------------------------------------------------------------
NOTES ON BUILDING AND RUNNING PINEAPPLE
------------------------------------------------------------

9) Build pineapple
-------------------

9.1) From command-prompt , cd into the pineapple directory and run:

mvn -cpu clean install
