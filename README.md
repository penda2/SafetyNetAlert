
# SafetyNet Alerts

Application for sending information to emergency service systems.


## Run locally

Prerequisites :
- Install JDK 17 :
https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html

-1 - Clone the project

```bash
  git clone https://github.com/penda2/SafetyNetAlert.git
```

- 2 - Go to the project directory, click on the address bar at the top of the window (where the folder path is displayed). Type "cmd" and press Enter. This will open Command Prompt directly to the location of this folder.

- 3- To verify java version, run :
```bash
  java -version
```
- 4 - To generate jar file in new taget folder, run :

```bash
mvn clean package
```
- 5 - To deploy application, run : 
```bash
java -jar api-0.0.1-SNAPSHOT.jar
```
- 5 - Go to the browser and test the application with given endpoints:
  
http://localhost:8080/person

http://localhost:8080/medicalrecord

http://localhost:8080/firestation

http://localhost:8080/firestation/3 

http://localhost:8080/childAlert/1509%20Culver%20St

http://localhost:8080/phoneAlert/3

http://localhost:8080/fire/947%20E.%20Rose%20Dr

http://localhost:8080/flood/stations/1

http://localhost:8080/personInfo/Felicia/Boyd

http://localhost:8080/communityEmail/Culver




## Authors

- [penda2](https://github.com/penda2)

