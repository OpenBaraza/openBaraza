Baraza is a Swahili word that means a deliberation meeting held by a collective group of a people of wisdom. The name baraza is used because the system came around by many heads with big minds coming together to develop it so as to come up with a working reliable system.

The Baraza Framework is a development Java Development initiative that makes application development easy. The framework takes advantage of XML to define its components. Using the framework, desktop applications, applets can be easily deployed using the same XML document with only slight modification.

Baraza is divided in three different blocks there is the Java code block, XML block and the Database block. The Java code block is again divided into four different blocks, that is; applet, servlet, server and IDE.

Baraza is a system that incorporates different independent projects that work independently using the same Java code block that uses the individual project XML block to produce a desired result. Feature of the baraza frame work:
- High development speed
- Low development cost
- Web enabled applications
- Very stable
- Well defined (well ordered design)
- Ease of operation (user friendly)
- Compatible with pc
- Reports, mailing lists, and forums are available.

Setup

1. install Postgresql http://www.postgresql.org/ above version 12
	Linux Installation
	- yum install postgresql-server
	- service postgresql initdb - (Optional - will clear all databases)
	- service postgresql start

	Windows Installation
	- Download the setup file and install

	{ To make the installation easier you can set the password for user postgres durting the installation to Baraza2011 }
	{ Remember to change it for a live deployment }

2. Install Java https://openjdk.org/ version 17 and above

3. Install apache ant from https://ant.apache.org/

4. Build the applications by running
cd ./openBaraza/
ant

5. Run the Ide
	cd ./openBaraza/
	Linux : ./ide.sh
	Windows : double click on setup.bat in app folder

6. Click on 
	Test Connection - {To test connection to the database}
	Save Configutations - {In case you change user name or password connecting to the database }
	Create New - {Create a new blank database}
	Create Demo - {Create a demo database}

7. Running the appplication
	Web Application
	Linux : sh server.sh
	Windows : server.bar
	got to browser http://localhost:9090/hr

	Running other web projects
	Linux : sh server.sh banking
	Windows : tomcat.bar
	got to browser http://localhost:9090/banking

	Application
	Linux : sh baraza.sh
	Windows : baraza.bar

	IDE
	Application
	Linux : sh ide.sh
	Windows : ide.bar

8. If you have many application to run in tomcat downlaod the 
	No DB configuration will be needed. There is a README file for the .war in the war folder

USER PASSWORDS
username root
password baraza



