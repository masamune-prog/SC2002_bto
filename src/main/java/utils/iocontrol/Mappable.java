package utils.iocontrol;

import utils.parameters.EmptyID;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Interface for objects that can be mapped to/from a Map.
 */
public interface Mappable {

    /**
     * Converts the object to a map
     *
     * @return the map
     */
    default Map<String, String> toMap() {
//        System.err.println("in");
        Map<String, String> map = new HashMap<>();
        Field[] fields = getClass().getDeclaredFields();
        for (Field field : fields) {
//            System.err.println(field.getName() + " in");
            try {
                field.setAccessible(true);
                try {
                    map.put(field.getName(), field.get(this).toString());
                } catch (NullPointerException e) {
                    map.put(field.getName(), EmptyID.EMPTY_ID);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
//            System.err.println(field.getName() + " out");
        }
//        System.err.println("out");
        return map;
    }

    /**
     * Converts the map to an object
     *
     * @param map the map
     */
    default void fromMap(Map<String, String> map) {
        Field[] fields = getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                if (field.getType().isEnum()) {
                    @SuppressWarnings("unchecked")
                    Enum<?> enumValue = Enum.valueOf((Class<Enum>) field.getType(), map.get(field.getName()));
                    field.set(this, enumValue);
                } else if (field.getType().equals(Integer.TYPE) || field.getType().equals(Integer.class)) {
//                    System.err.println("==============================");
                    if (EmptyID.isEmptyID(map.get(field.getName()))) {
                        field.set(this, 0);
                    } else {
                        int intValue = Integer.parseInt(map.get(field.getName()));
                        field.set(this, intValue);
                    }
                } else {
                    field.set(this, map.get(field.getName()));
                }
            } catch (IllegalAccessException | NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }
}
