package org.example;

import java.lang.reflect.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSONParser {
    public static <T> T fromJSON(String json, String className) {
        try {
            Class<?> clazz = Helper.findClass(className);
            Object object = clazz.getDeclaredConstructor().newInstance();
            Field[] fields = clazz.getDeclaredFields();
            for(Field field : fields){
                if (field.getType() == List.class && field.getGenericType() instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                    Type[] typeArguments = parameterizedType.getActualTypeArguments();
                    if (typeArguments.length == 1 && typeArguments[0] == String.class) {
                        System.out.println("Jest listą: " + field.getName());
                        try {
                            Method setter = object.getClass().getMethod("set" + Helper.capitalize(field.getName()), List.class);
                            setter.invoke(object, listStringFromJson(json, field));
                        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    } else if (typeArguments.length == 1 && typeArguments[0] instanceof Class) {
                        try {
                            Class<?> elementType = (Class<?>) typeArguments[0];
                            List<?> list = listFromJson(json, field);
                            Method setter = object.getClass().getMethod("set" + Helper.capitalize(field.getName()), List.class);
                            setter.invoke(object, list);
                        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (field.getType() == int.class || field.getType() == Integer.class || field.getType() == double.class || field.getType() == Double.class || field.getType() == float.class || field.getType() == Float.class || field.getType() == long.class || field.getType() == Long.class || field.getType() == short.class || field.getType() == Short.class || field.getType() == byte.class || field.getType() == Byte.class) {
                    field.setAccessible(true);
                    Object value = numberFromJson(json, field, (Class<? extends Number>) field.getType());
                    if (value != null) {
                        field.set(object, value);
                    }
                } else if (field.getType().isPrimitive() || field.getType().equals(String.class)) {
                        try {
                            Method setter = object.getClass().getMethod("set" + Helper.capitalize(field.getName()), String.class);
                            setter.invoke(object, stringFromJson(json, field));
                        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                   // field.set(object, stringFromJson(json, field));
                } else {
                    // Handle custom class type
                    try {
                        Class<?> customClass = Helper.findClass(field.getType().getSimpleName());
                        Object customObject = fromJSON(json, customClass.getSimpleName()); // Recursively map custom object
                        Method setter = object.getClass().getMethod("set" + Helper.capitalize(field.getName()), customClass);
                        setter.invoke(object, customObject);
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }

            }
            return (T) object;
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> List<T> listFromJson(String json, Field field) {
        String fieldName = field.getName();
        String pattern = "\"" + fieldName + "\":\\[([^\\]]+)\\]";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(json);
        if (matcher.find()) {
            String matchedValue = matcher.group(1);
            String[] objectStrings = matchedValue.split("\\},\\{");

            List<T> valuesList = new ArrayList<>();

            for (String objectString : objectStrings) {
                if (objectString.startsWith("{") || objectString.endsWith("}")) {
                    String genericTypeName = field.getGenericType().getTypeName();
                    String simpleClassName = genericTypeName.substring(genericTypeName.lastIndexOf('.') + 1)
                            .replaceAll("[<>]", "");
                    if (simpleClassName.contains("$")) {
                        simpleClassName = simpleClassName.substring(simpleClassName.lastIndexOf('$') + 1);
                    }
                    if (Helper.findClass(simpleClassName) != null) {
                        System.out.println("address: " + objectString);
                        T mappedObject = fromJSON(objectString, simpleClassName);
                        valuesList.add(mappedObject);
                    }
                } else {
                    String cleanedValue = objectString.replaceAll("\"", "");
                    valuesList.add((T) cleanedValue);
                }
            }

            return valuesList;
        }

        return null;
    }

    private static List<String> listStringFromJson (String json, Field field){
        String fieldName = field.getName();
        String pattern = "\""+fieldName+"\":\\[([^\\]]+)\\]";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(json);
        if (matcher.find()) {
            String matchedValue = matcher.group(1);
            String[] valuesArray = matchedValue.replaceAll("\""+fieldName+"\":", "").split("\",\"");
            List<String> valuesList = new ArrayList<>();

            for (String value : valuesArray) {
                String cleanedValue = value.replaceAll("\"", "");
                valuesList.add(cleanedValue);
            }

            return valuesList;
        }
        return null;
    }

    private static Number numberFromJson(String json, Field field, Class<? extends Number> type) {
        field.setAccessible(true);
        String fieldName = field.getName();
        String pattern = "\"" + fieldName + "\":(\\d+(\\.\\d+)?)";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(json);
        if (matcher.find()) {
            String matchedValue = matcher.group(1);
            if (type == Integer.class || type == int.class) {
                return Integer.valueOf(matchedValue);
            } else if (type == Double.class || type == double.class) {
                return Double.valueOf(matchedValue);
            } else if (type == Float.class || type == float.class) {
                return Float.valueOf(matchedValue);
            } else if (type == Long.class || type == long.class) {
                return Long.valueOf(matchedValue);
            } else if (type == Short.class || type == short.class) {
                return Short.valueOf(matchedValue);
            } else if (type == Byte.class || type == byte.class) {
                return Byte.valueOf(matchedValue);
            }
        }
        return null;
    }

    private static String stringFromJson (String json, Field field){
        String fieldName = field.getName();

        String pattern = "\""+fieldName+"\":\"([^\"]+)\"";

        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(json);
        if (matcher.find()) {
            String matchedValue = matcher.group(1);
            String value = matchedValue.replaceAll("\""+fieldName+"\":", "");

            return value;
        }
        return null;
    }
}
