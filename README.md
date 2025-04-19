To run this project, import it as a maven project. For best results, run mvn clean then compile all classes, using either your IDE

or run 

java -cp target/SC2002_BTO_EXP-1.0-SNAPSHOT.jar Main

To Modify the DB, Modify the txt files in the src/main/resources folder.
After modifying the txt files, run the following command to update the

```bash
mvn clean compile
mvn package -DskipTests
mvn install -DskipTests
```
then run the following command to run the project:
```bash
java -cp target/SC2002_BTO_EXP-1.0-SNAPSHOT.jar Main
```