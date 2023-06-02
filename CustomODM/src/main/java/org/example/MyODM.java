package org.example;

import java.lang.reflect.*;
import java.util.*;
import org.json.simple.*;

public class MyODM {

    public static Map<String, Object> toJsonMap(Object object) throws IllegalAccessException {
        Map<String, Object> jsonMap = new HashMap<>();
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            Type fieldType = field.getGenericType();
            if (fieldType instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) fieldType;
                if (parameterizedType.getRawType() == List.class && parameterizedType.getActualTypeArguments()[0] == String.class) {
                    // handle List<String> separately
                    field.setAccessible(true);
                    List<?> list = (List<?>) field.get(object);
                    jsonMap.put(field.getName(), list);
                    continue;
                }
            }

            field.setAccessible(true);
            String fieldName = field.getName();
            Object fieldValue = field.get(object);
            if (field.getType().isArray()) {
                // handle arrays
                Object[] array = (Object[]) fieldValue;
                List<Object> list = new ArrayList<>(Arrays.asList(array));
                jsonMap.put(fieldName, toJSON(list));
            } else if (field.getType().equals(List.class) && isListOfObjects(field)) {
                List<?> list = (List<?>) fieldValue;
                if (list != null) {
                    jsonMap.put(fieldName, toJSON(list));
                }
            } else if (Collection.class.isAssignableFrom(field.getType())) {
                // handle collections
                Collection<?> collection = (Collection<?>) fieldValue;
                if (collection != null) {
                    jsonMap.put(fieldName, toJSON(collection));
                }
            } else if (field.getType().isPrimitive() || field.getType().equals(String.class)) {
                // handle primitives and Strings
                jsonMap.put(fieldName, fieldValue);
            } else {
                // handle objects
                String nestedJson = toJSON(fieldValue);
                jsonMap.put(fieldName, JSONValue.parse(nestedJson));
            }
        }
        return jsonMap;
    }
    public static String toJSON(Object object) throws IllegalAccessException {
        String result = JSONValue.toJSONString(toJsonMap(object));
        return filterJSONString(result);
    }

    public static String toJSON(List<?> objects) throws IllegalAccessException {
        List<Map<String, Object>> jsonList = new ArrayList<>();
        for (Object object : objects) {
            jsonList.add(toJsonMap(object));
        }
        return filterJSONString(JSONValue.toJSONString(jsonList));
    }

    private static String toJSON(Collection<?> collection) throws IllegalAccessException {
        List<Object> list = new ArrayList<>();
        for (Object obj : collection) {
            list.add(toJSON(obj));
        }
        return filterJSONString(JSONValue.toJSONString(list));
    }

    private static boolean isListOfObjects(Field field) {
        Type fieldType = field.getGenericType();
        if (fieldType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) fieldType;
            Type[] typeArguments = parameterizedType.getActualTypeArguments();
            if (typeArguments.length > 0) {
                Type typeArgument = typeArguments[0];
                return typeArgument instanceof Class;
            }
        }
        return false;
    }

    private static String filterJSONString (String rawJsonString){
        String result = rawJsonString.replace("\\", "");
        result = result.replace("\"[", "[");
        result = result.replace("]\"", "]");
        return result;
    }
}
