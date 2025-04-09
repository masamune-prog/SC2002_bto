package model.user;
import model.Model;
public interface User {
    String getNric();
    void setNric(String nric);

    String getPasswordHash();
    void setPasswordHash(String passwordHash);

    String getName();
    void setName(String name);

    int getAge();
    void setAge(int age);

    String getMaritalStatus();
    void setMaritalStatus(String maritalStatus);

    String getProject();
    void setProject(String project);
}