//package test;
//
//import utils.iocontrol.UserSaver;
//import model.user.User;
//
//public class UserSaverTest {
//    public static void main(String[] args) {
//        final String FILE_PATH = "data/users.txt";
//
//        // Create users
//        UserSaver userSaver = new UserSaver();
//        userSaver.addUser(new User("Alice", "S1234567A"));
//        userSaver.addUser(new User("Bob", "S7654321B"));
//
//        // Save to file
//        userSaver.save(FILE_PATH);
//        System.out.println("Users saved to " + FILE_PATH);
//
//        // Load from file to verify
//        UserSaver userLoader = new UserSaver();
//        userLoader.load(FILE_PATH);
//        System.out.println("Loaded users:");
//        for (User user : userLoader.getUsers()) {
//            System.out.println(user);
//        }
//    }
//}
