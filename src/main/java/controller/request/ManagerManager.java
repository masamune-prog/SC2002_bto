package controller.request;

import model.user.Manager;
import repository.user.ManagerRepository;
import utils.exception.ModelNotFoundException;

import java.util.List;

public class ManagerManager {
    public static Manager getManagerByID(String managerID) throws ModelNotFoundException {
        return ManagerRepository.getInstance().getByID(managerID);
    }
    public static String getIDByManagerName(String managerName) throws ModelNotFoundException {
        List<Manager> managers = ManagerRepository.getInstance().getAll();
        for (Manager manager : managers) {
            if (manager.getName().equals(managerName)) {
                return manager.getID();
            }
        }
        return null;
    }

}
