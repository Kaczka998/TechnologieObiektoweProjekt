package org.example;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;

public class JSONMapper {
    public static String toJSONString(Object object) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
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
        return json.replaceAll(",$", "") + "}";
    }

    public static String fieldtoJSONString(Field field, Object object) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> fieldType = field.getType();

        if (fieldType.isMemberClass() && Modifier.isStatic(fieldType.getModifiers())) {
            field.setAccessible(true);
            // Handle static inner classes separately
            Object fieldObject = field.get(object);
            return "\""+field.getName()+"\":" + toJSONString(fieldObject);
        }

        if (fieldType.isMemberClass() && !Modifier.isStatic(fieldType.getModifiers())) {
            // Handle non-static inner classes
            Object fieldObject;
            try {
                field.setAccessible(true);
                fieldObject = field.get(object);
                if (fieldObject != null) {
                    return "\""+field.getName()+"\":" + toJSONString(fieldObject);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        // Handle regular fields
        StringBuilder jsonString = new StringBuilder();
        field.setAccessible(true);
        String fieldName = getFieldName(field);
        Object fieldValue = getFieldValue(object, field);

        jsonString.append("\"").append(fieldName).append("\":");

        if (fieldValue == null) {
            jsonString.append("null");
        } else if (field.getType() == String.class) {
            jsonString.append("\"").append(fieldValue).append("\"");
        } else if (field.getType().isPrimitive() || isWrapperType(field.getType())) {
            jsonString.append(fieldValue);
        } else if (field.getType() == List.class) {
            jsonString.append(toJSONArray((List<?>) fieldValue));
        } else {
            jsonString.append(toJSONString(fieldValue));
        }

        return jsonString.toString();
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
    private static String toJSONArray(List<?> list) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException { //źle obsługuje listy obiektów (adresy)
        Class listItemClass = list.get(0).getClass(); // if listItemClass.getSimpleName() wywołane w findClass znajdzie klasę to jest to lista obiektów tej klasy, inaczj sprawdź jakit to typ, string czy numeryczny czy jak
        Class customClass = Helper.findClass(listItemClass.getSimpleName());
        if(customClass!=null){
            String jsonArrayOfObjects = "";
            for(Object item : list){
                jsonArrayOfObjects += toJSONString(item); // próba ogarnięcia obsługi listy obiektów
                jsonArrayOfObjects += ",";
                jsonArrayOfObjects = jsonArrayOfObjects.substring(0, jsonArrayOfObjects.length() - 1);
            }
            return "[" + jsonArrayOfObjects + "]";
        }
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
        return "[" + jsonArray + "]";
    }

    private static String getFieldName(Field field) {
        JSONProperty annotation = field.getAnnotation(JSONProperty.class);
        if (annotation != null && !annotation.name().isEmpty()) {
            return annotation.name();
        }
        return field.getName();
    }
}
