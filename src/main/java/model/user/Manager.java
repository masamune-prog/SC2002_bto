package model.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Manager implements User {
    private String managerNRIC;
    private String hashedPassword;
    private String managerName;
    private List<String> projectIDsInCharge;

    public Manager(String managerNRIC, String managerName) {
        this.managerNRIC = managerNRIC;
        this.managerName = managerName;
    }

    public Manager(String managerNRIC, String hashedPassword, String managerName) {
        this.managerNRIC = managerNRIC;
        this.hashedPassword = hashedPassword;
        this.managerName = managerName;
    }
    public Manager(String managerNRIC, String hashedPassword, String managerName, List<String> projectIDsInCharge) {
        this.managerNRIC = managerNRIC;
        this.hashedPassword = hashedPassword;
        this.managerName = managerName;
        this.projectIDsInCharge = projectIDsInCharge;
    }
    public Manager(Map<String, String> map) {this.fromMap(map);}
    public Manager(){
        this.managerNRIC = "";
        this.hashedPassword = "";
        this.managerName = "";
        this.projectIDsInCharge =  new ArrayList<String>() {
        };
    }

    public String getManagerNRIC() {
        return managerNRIC;
    }

    public void setManagerNRIC(String managerNRIC) {
        this.managerNRIC = managerNRIC;
    }

    @Override
    public String getID() {
        return managerNRIC;
    }

    @Override
    public void setID(String id) {
        this.managerNRIC = id;
    }

    @Override
    public String getNRIC() {
        return managerNRIC;
    }

    @Override
    public void setNRIC(String nric) {
        this.managerNRIC = nric;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    @Override
    public String getName() {
        return managerName;
    }

    @Override
    public void setName(String name) {
            this.managerName = name;
    }

    @Override
    public Object getUserType() {
        return UserType.MANAGER;
    }

    public List<String> getProjectIDsInCharge() {
        return projectIDsInCharge;
    }

    public void setProjectIDsInCharge(List<String> projectIDsInCharge) {
        this.projectIDsInCharge = projectIDsInCharge;
    }
}
