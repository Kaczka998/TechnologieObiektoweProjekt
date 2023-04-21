package org.example;

import java.lang.reflect.Field;
import java.util.*;

import org.json.simple.*;

public class MyODM {

//    public static String toJSON(Object object) throws IllegalAccessException {
//        Map<String, Object> jsonMap = new HashMap<>();
//        Field[] fields = object.getClass().getDeclaredFields();
//        for (Field field : fields) {
//            field.setAccessible(true);
//            String fieldName = field.getName();
//            Object fieldValue = field.get(object);
//            JSONProperty jsonProperty = field.getAnnotation(JSONProperty.class);
//            if (jsonProperty != null) {
//                fieldName = jsonProperty.name();
//            }
//            jsonMap.put(fieldName, fieldValue);
//        }
//        return JSONValue.toJSONString(jsonMap);
//    }
public static String toJSON(Object object) throws IllegalAccessException {
    Map<String, Object> jsonMap = new HashMap<>();
    Field[] fields = object.getClass().getDeclaredFields();
    for (Field field : fields) {
        field.setAccessible(true);
        String fieldName = field.getName();
        Object fieldValue = field.get(object);
        JSONProperty jsonProperty = field.getAnnotation(JSONProperty.class);
        if (jsonProperty != null) {
            fieldName = jsonProperty.name();
        }
        if (field.getType().isArray()) {
            // handle arrays
            Object[] array = (Object[]) fieldValue;
            List<Object> list = new ArrayList<>(Arrays.asList(array));
            jsonMap.put(fieldName, toJSON(list));
        } else if (Collection.class.isAssignableFrom(field.getType())) {
            // handle collections
            Collection<?> collection = (Collection<?>) fieldValue;
            jsonMap.put(fieldName, toJSON(collection));
        } else if (field.getType().isPrimitive() || field.getType().equals(String.class)) {
            // handle primitives and Strings
            jsonMap.put(fieldName, fieldValue);
        } else {
            // handle objects
            jsonMap.put(fieldName, toJSON(fieldValue));
        }
    }
    return JSONValue.toJSONString(jsonMap);
}

    private static String toJSON(Collection<?> collection) throws IllegalAccessException {
        List<Object> list = new ArrayList<>();
        for (Object obj : collection) {
            list.add(toJSON(obj));
        }
        return JSONValue.toJSONString(list);
    }

}