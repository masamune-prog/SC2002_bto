# SC2002 BTOMS(BTO Management System)

BTO Management System is a system that allows for Applicants to apply for BTOs and allows for HDB Managers and Officers to perform actions to offer the BTO to applicants, and process the BTO applications

To run this project, import it as a maven project. For best results, run 

```bash
mvn clean 
```

then compile all classes, using either your IDE(Recommended for nicer visuals)

or run

```bash
java -cp target/SC2002_BTO_EXP-1.0-SNAPSHOT.jar Main
```

To Modify the DB, Modify the txt files in the src/main/resources folder.
After modifying the txt files, run the following command to update the

```bash
mvn clean compile
mvn install -DskipTests
```

then run the following command to run the project:

```bash
java -cp target/SC2002_BTO_EXP-1.0-SNAPSHOT.jar Main
```

The UML Diagrams are in the UML Folder with a [draw.io](http://draw.io) file and jpeg for viewing

The Sequence Diagrams are in the Sequence Diagram UML Folder and was made using Visual Paradigm

# Usage

## **Login Credentials**

### Applicant

| Name | NRIC | Age | Marital Status | Password |
| --- | --- | --- | --- | --- |
| John | S1234567A | 35 | Single | password |
| Sarah | T7654321B | 40 | Married | password |
| Grace | S9876543C | 37 | Married | password |
| James | T2345678D | 30 | Married | password |
| Rachel | S3456789E | 25 | Single | password |
| Daniel | T2109876H | 36 | Single | password |
| ZR | S1111111A | 28 | Single | password |
| Tim | T2222222B | 45 | Married | password |
| Test | S3333333C | 50 | Single | password |

### Manager

| Name | NRIC | Age | Marital Status | Password |
| --- | --- | --- | --- | --- |
| Michael | T8765432F | 36 | Single | password |
| Jessica | S5678901G | 26 | Married | password |
| TestManager | S3478901G | 28 | Married | password |

### Officer

| Name | NRIC | Age | Marital Status | Password |
| --- | --- | --- | --- | --- |
| Daniel | T2109876H | 36 | Single | password |
| Emily | S6543210I | 28 | Single | password |
| David | T1234567J | 29 | Married | password |

### Projects

| Project Name | Neighborhood | Type 1 | Number of units for Type 1 | Selling price for Type 1 | Type 2 | Number of units for Type 2 | Selling price for Type 2 | Application opening date | Application closing date | Manager | Officer Slot | Officer | Visibility |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| Acacia Breeze | Yishun | 2-Room | 2 | 350000 | 3-Room | 3 | 450000 | 15/2/2025 | 20/3/2025 | Jessica | 3 | Daniel,Emily | true |