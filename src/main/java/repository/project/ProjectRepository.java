//package repository.project;
//
//import model.project.Project;
//import repository.Repository;
//import utils.config.Location;
//import utils.iocontrol.CSVReader;
//
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class ProjectRepository extends Repository<Project> {
//
//    private static final String FILE_PATH = "/data/project/ProjectList.csv";
//
//    ProjectRepository() {
//        super();
//        load();
//    }
//
//    public static ProjectRepository getInstance() {
//        return new ProjectRepository();
//    }
//
//    @Override
//    public String getFilePath() {
//        return Location.RESOURCE_LOCATION + FILE_PATH;
//    }
//
//    @Override
//    public void load() {
//        this.getAll().clear();
//        List<List<String>> csvData = CSVReader.read(getFilePath(), true);
//        List<Map<String, String>> mappedData = convertToMapList(csvData);
//        setAll(mappedData);
//    }
//
//    private List<Map<String, String>> convertToMapList(List<List<String>> csvData) {
//        List<Map<String, String>> result = new ArrayList<>();
//
//        // Define the column headers
//        String[] headers = {
//                "TargetedUserGroup", "Visibility", "ProjectName", "Neighborhood",
//                "TwoRoomUnits", "ThreeRoomUnits", "OpeningDate", "ClosingDate", "ManagerInCharge"
//        };
//
//        // Convert each row to a map
//        for (List<String> row : csvData) {
//            Map<String, String> rowMap = new HashMap<>();
//            for (int i = 0; i < headers.length && i < row.size(); i++) {
//                rowMap.put(headers[i], row.get(i));
//            }
//            result.add(rowMap);
//        }
//
//        return result;
//    }
//
//    @Override
//    public void setAll(List<Map<String, String>> listOfMappableObjects) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        ManagerRepository managerRepository = ManagerRepository.getInstance();
//
//        for (Map<String, String> map : listOfMappableObjects) {
//            try {
//                String targetedUserGroup = map.get("TargetedUserGroup");
//                boolean visibility = Boolean.parseBoolean(map.get("Visibility"));
//                String projectName = map.get("ProjectName");
//                String neighborhood = map.get("Neighborhood");
//                int twoRoomUnits = Integer.parseInt(map.get("TwoRoomUnits"));
//                int threeRoomUnits = Integer.parseInt(map.get("ThreeRoomUnits"));
//                LocalDate openingDate = LocalDate.parse(map.get("OpeningDate"), formatter);
//                LocalDate closingDate = LocalDate.parse(map.get("ClosingDate"), formatter);
//
//                // Get the manager from repository
//                Manager manager = (Manager) managerRepository.getByID(map.get("ManagerInCharge"));
//
//                Project project = new Project(
//                        targetedUserGroup, visibility, projectName, neighborhood,
//                        twoRoomUnits, threeRoomUnits, openingDate, closingDate, manager
//                );
//
//                getAll().add(project);
//            } catch (Exception e) {
//                System.err.println("Error parsing project data: " + e.getMessage());
//            }
//        }
//    }
//}