package org.example;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public class JSONMapper {
    public static String toJSONString(Object object) throws NoSuchMethodException {
        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        System.out.println(clazz.getSimpleName());
        StringBuilder jsonString = new StringBuilder();
        jsonString.append("{");
        String json = "";
        for (Field field : fields) {
            if (field.isAnnotationPresent(JSONProperty.class)) {
                jsonString.append(fieldtoJSONString(field, object));
                jsonString.append(",");
            }
            json = jsonString.toString();
            json.substring(0, json.length() - 1);
        }
        return json + "}";
    }

    public static String fieldtoJSONString(Field field, Object object) throws NoSuchMethodException {
        System.out.println(field.getName() +" "+ field.getType());
        Class<?> clazz = ODMUserInterface.findClass(field.getType().getSimpleName());
        if(clazz!=null){
            try {
                System.out.println(clazz.getSimpleName());
                Object fieldObject = clazz.getDeclaredConstructor().newInstance();
                return toJSONString(fieldObject);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        StringBuilder jsonString = new StringBuilder();

                field.setAccessible(true);
                String fieldName = getFieldName(field);
                Object fieldValue = getFieldValue(object, field);

                jsonString.append("\"").append(fieldName).append("\":");

                if (fieldValue == null) {
                    jsonString.append("null");
                } else if (field.getType() == String.class) {
                    jsonString.append("\"").append(fieldValue).append("\"");
                } else if (field.getType() == Double.class) {
                    jsonString.append(fieldValue);
                } else if (field.getType().isPrimitive() || isWrapperType(field.getType())) {
                    jsonString.append(fieldValue);
                } else if (field.getType() == List.class) {
                    jsonString.append(toJSONArray((List<?>) fieldValue));
                } else {
                    jsonString.append(toJSONString(fieldValue));
                }
        return jsonString.toString();
    }

    public static String toJSONString(Map<String, Object> objectMap) throws NoSuchMethodException {
        String json = "";
        for (String key : objectMap.keySet()) {
            json += toJSONString(objectMap.get(key));
        }
        return json;
    }

    private static Object getFieldValue(Object object, Field field) {
        try {
            field.setAccessible(true);
            return field.get(object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean isWrapperType(Class<?> clazz) {
        return clazz == Integer.class || clazz == Long.class || clazz == Float.class ||
                clazz == Double.class || clazz == Boolean.class || clazz == Character.class ||
                clazz == Byte.class || clazz == Short.class;
    }

    private static String toJSONArray(List<?> list) throws NoSuchMethodException {
        StringBuilder jsonArray = new StringBuilder();

        for (int i = 0; i < list.size(); i++) {
            Object item = list.get(i);

            if (item == null) {
                jsonArray.append("null");
            } else if (item instanceof String) {
                jsonArray.append("\"").append(item).append("\"");
            } else if (item.getClass().isPrimitive() || isWrapperType(item.getClass())) {
                jsonArray.append(item);
            } else {
                jsonArray.append(toJSONString(item));
            }

            if (i < list.size() - 1) {
                jsonArray.append(",");
            }
        }
        String json = jsonArray.toString();
        json = json.substring(0, json.length() - 1);
        json = json.substring(1);
        json = json.substring(0, json.length() - 1);
        return "[" + json + "]";
    }

    private static String getFieldName(Field field) {
        JSONProperty annotation = field.getAnnotation(JSONProperty.class);
        if (annotation != null && !annotation.name().isEmpty()) {
            return annotation.name();
        }
        return field.getName();
    }
}
