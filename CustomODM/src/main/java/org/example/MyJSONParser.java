package org.example;

import java.lang.reflect.*;
import java.util.*;

import org.json.simple.*;
public class MyJSONParser {
    public static <T> T fromJSON(String json, Class<T> clazz) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        JSONObject jsonObject = (JSONObject) JSONValue.parse(json);
        return fromJSON(jsonObject, clazz);
    }

    private static <T> T fromJSON(JSONObject jsonObject, Class<?> clazz) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        T instance = (T) clazz.getDeclaredConstructor().newInstance();

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            if (jsonObject.containsKey(fieldName)) {
                Object fieldValue = jsonObject.get(fieldName);
                field.setAccessible(true);
                Type fieldType = field.getGenericType();

                if (field.getType().equals(int.class)) {  // Handle int fields specifically
                    if (fieldValue instanceof Number) {
                        int intValue = ((Number) fieldValue).intValue();
                        field.set(instance, intValue);
                    }
                }else if (fieldType instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) fieldType;
                    Type[] typeArguments = parameterizedType.getActualTypeArguments();
                    if (typeArguments.length > 0 && typeArguments[0] instanceof Class) {
                        // Handle List of Objects
                        if (field.getType().equals(List.class) && isListOfObjects(field)) {
                            List<?> list = fromJSONList((JSONArray) fieldValue, (Class<?>) typeArguments[0]);
                            field.set(instance, list);
                        }
                    }
                } else if (field.getType().isArray()) {
                    // Handle Array
                    if (fieldValue instanceof JSONArray) {
                        Object[] array = fromJSONArray((JSONArray) fieldValue, field.getType().getComponentType());
                        field.set(instance, array);
                    }
                } else if (field.getType().isPrimitive() || field.getType().equals(String.class)) {
                    // Handle Primitive types and Strings
                    field.set(instance, fieldValue);
                } else {
                    // Handle Object
                    if (fieldValue instanceof JSONObject) {
                        Object nestedObject = fromJSON((JSONObject) fieldValue, field.getType());
                        field.set(instance, nestedObject);
                    }
                }
            }
        }
        return instance;
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

    private static Object[] fromJSONArray(JSONArray jsonArray, Class<?> clazz) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Object[] array = (Object[]) Array.newInstance(clazz, jsonArray.size());
        for (int i = 0; i < jsonArray.size(); i++) {
            Object item = jsonArray.get(i);
            if (item instanceof JSONObject) {
                Object instance = fromJSON((JSONObject) item, clazz);
                array[i] = instance;
            }
        }
        return array;
    }

    private static <T> List<T> fromJSONList(JSONArray jsonArray, Class<T> clazz) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        List<T> list = new ArrayList<>();
        for (Object item : jsonArray) {
            if (item instanceof JSONObject) {
                T instance = fromJSON((JSONObject) item, clazz);
                list.add(instance);
            }
        }
        return list;
    }
}
