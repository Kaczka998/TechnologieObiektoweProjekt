package org.example;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;

public class JSONMapper {
    //This method adds "{" and "}" and the ends of json and between calls in a loop over object's fields mapping for all
    // fields on object to json Strings. It gets field list from object parsed through parameter with reflection.
    public static String toJSONString(Object object) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        StringBuilder jsonString = new StringBuilder();
        jsonString.append("{"); //starts json with "{".
        String json = "";
        for (Field field : fields) {
            if (field.isAnnotationPresent(JSONProperty.class)) { //checking if field is annotated, if not it is ignored.
                jsonString.append(fieldtoJSONString(field, object)); //calling method that identifies field type and parses it to json.
                jsonString.append(","); //separating fields
            }
            json = jsonString.toString();
            json.substring(0, json.length() - 1);
        }
        return json.replaceAll(",$", "") + "}";//cut last "," and closes json with "}".
    }

    //This method identifies type of field and handles it based on it's type.
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

    //Method for retrieving field value from object.
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


    //Method for handling arrays with adding "[" and "]" to them.
    private static String toJSONArray(List<?> list) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class listItemClass = list.get(0).getClass();
        Class customClass = Helper.findClass(listItemClass.getSimpleName());
        if(customClass!=null){
            String jsonArrayOfObjects = "";
            for(Object item : list){
                jsonArrayOfObjects += toJSONString(item);
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

    //Method for retrieving name of the field. It also checks if there is custom name put in annotation to use instead of field name.
    private static String getFieldName(Field field) {
        JSONProperty annotation = field.getAnnotation(JSONProperty.class);
        if (annotation != null && !annotation.name().isEmpty()) {
            return annotation.name();
        }
        return field.getName();
    }
}
