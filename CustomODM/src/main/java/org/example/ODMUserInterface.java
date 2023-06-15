package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

public class ODMUserInterface {
    private static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private static final JSONParser jsonParser = new JSONParser();
    private static final JSONMapper jsonMapper = new JSONMapper();

    public static void run() {
        boolean quit = false;

        while (!quit) {
            printMenu();
            String choice = readUserInput();

            switch (choice) {
                case "1":
                    mapJSONToObject();
                    break;
                case "2":
                    mapObjectToJSON();
                    break;
                case "3":
                    quit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("=== Object Mapper Menu ===");
        System.out.println("1. Map JSON to Object");
        System.out.println("2. Map Object to JSON");
        System.out.println("3. Quit");
        System.out.print("Enter your choice: ");
    }

    private static String readUserInput() {
        try {
            return reader.readLine().trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    // This method comunicates with user and takes required input, then calls method from JSONParser class.
    private static void mapJSONToObject() {
        System.out.print("Enter the JSON string: ");
        String jsonString = readUserInput();

        System.out.print("Enter the class name: ");
        String className = readUserInput();

        Class<?> clazz = Helper.findClass(className);
        if (clazz != null) {
            Object object = null;
            try {
                object = jsonParser.fromJSON(jsonString, className);
            } catch (Exception e) {
                System.out.println("Error mapping JSON to object: " + e.getMessage());
            }
            if (object != null) {
                System.out.println("Object mapped successfully:\n");
                displayObjectFields(object);
            }
        } else {
            System.out.println("Class not found: " + className);
        }
    }

    // This method display objects mapped from JSON showing all fields with names and values.
    private static void displayObjectFields(Object object) {
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(object);
                if (value instanceof List<?>) {
                    // Handle lists
                    System.out.println(field.getName() + " : ");
                    List<?> list = (List<?>) value;
                    for (Object listItem : list) {
                        if (listItem instanceof String) {
                            System.out.println(listItem);
                        } else {
                            displayObjectFields(listItem);
                        }
                    }
                } else if (value != null && value.getClass().getName().startsWith("org.example")) {
                    // Handle nested objects
                    System.out.println(field.getName() + " : ");
                    displayObjectFields(value);
                } else {
                    System.out.println(field.getName() + " : " + value);
                }
            } catch (IllegalAccessException e) {
                System.out.println("Error accessing field: " + e.getMessage());
            }
        }
    }

    // This method comunicates with user and takes required input, then calls method from JSONMapper class.
    // It takes Class name from user and then reads what fields it need. It takes values for those fields from user,
    // creates object and send it as parameter to jsonMapper.toJSONString() method.
    private static void mapObjectToJSON() {
        System.out.print("Enter the class name: ");
        String className = readUserInput();
        List<String> innerJSONs = new ArrayList<>();

        Class<?> clazz = Helper.findClass(className);
        try {
            Object object = clazz.getDeclaredConstructor().newInstance();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true); // Make the field accessible

                if (field.getType() == List.class) {
                    ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                    Class<?> listType = (Class<?>) parameterizedType.getActualTypeArguments()[0];

                    List<Object> list = new ArrayList<>();
                    boolean hasMoreValues = true;

                    while (hasMoreValues) {
                        Object valueObject = null;

                        if (listType == String.class) {
                            System.out.print("Provide value for " + field.getName() + " field: (separately, expected data type: " + listType.getSimpleName() + "): ");
                            String userInput = readUserInput();
                            valueObject = userInput;
                        } else {
                            System.out.println(field.getName() + ": ");
                            valueObject = listType.getDeclaredConstructor().newInstance();

                            Field[] valueFields = valueObject.getClass().getDeclaredFields();
                            for (Field valueField : valueFields) {
                                valueField.setAccessible(true); // Make the field accessible
                                System.out.print("Provide values for " + valueField.getName() + " field (separately, expected data type: " + valueField.getType().getSimpleName() + "): ");
                                String userInput = readUserInput();

                                // Set the value of the field in the value object
                                setFieldValue(valueObject, valueField, userInput);
                            }
                        }

                        list.add(valueObject);

                        System.out.print("Are there any more values? (yes/no): ");
                        String moreValuesInput = readUserInput();
                        hasMoreValues = moreValuesInput.equalsIgnoreCase("yes");
                    }

                    field.set(object, list);
                } else if (field.getType().isPrimitive() || field.getType().equals(String.class)) {
                    System.out.print("Provide value for " + field.getName() + " field (expected data type: " + field.getType().getSimpleName() + "): ");
                    String userInput = readUserInput();

                    // Set the value of the field in the object
                    setFieldValue(object, field, userInput);
                } else {
                    // Handle object fields
                    Class<?> fieldType = field.getType();
                    Object fieldObject = fieldType.getDeclaredConstructor().newInstance();
                    System.out.println(field.getName() + ": ");
                    // Recursively call mapObjectToJSON for the nested object
                    if (fieldObject != null) {
                        innerJSONs.add(mapObjectToJSON(fieldObject));
                    }
                    // Set the value of the field in the object
                    field.set(object, fieldObject);
                }
            }
            String jsonString = jsonMapper.toJSONString(object);
            System.out.println("JSON mapped successfully:\n" + jsonString);
        } catch (Exception e) {
            System.out.println("Error mapping object to JSON: " + e.getMessage());
        }
    }

    //method similar to one above but it takes an object as parameter and handles inner objects reading required values
    //from user. F.e. Engine object in a Car object.
    private static String mapObjectToJSON(Object object) {
        try {
            Class<?> clazz = object.getClass();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true); // Make the field accessible

                if (field.getType() == List.class) {
                    ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                    Class<?> listType = (Class<?>) parameterizedType.getActualTypeArguments()[0];

                    List<Object> list = new ArrayList<>();
                    boolean hasMoreValues = true;

                    while (hasMoreValues) {
                        Object valueObject;

                        if (listType == String.class) {
                            System.out.print("Provide value for " + field.getName() + " field (expected data type: " + field.getType().getSimpleName() + ": ");
                            String userInput = readUserInput();
                            valueObject = userInput;
                        } else {
                            valueObject = listType.getDeclaredConstructor().newInstance();

                            Field[] valueFields = listType.getDeclaredFields();
                            for (Field valueField : valueFields) {
                                valueField.setAccessible(true); // Make the field accessible
                                System.out.print("Provide values for " + valueField.getName() + " field (separately, expected data type: " + field.getType().getSimpleName() + "): ");
                                String userInput = readUserInput();

                                // Set the value of the field in the value object
                                setFieldValue(valueObject, valueField, userInput);
                            }
                        }

                        list.add(valueObject);

                        System.out.print("Are there any more values? (yes/no): ");
                        String moreValuesInput = readUserInput();
                        hasMoreValues = moreValuesInput.equalsIgnoreCase("yes");
                    }

                    field.set(object, list);
                } else if (field.getType().isArray()) {
                    // Handle array fields
                    // ...
                } else if (field.getType().isPrimitive() || field.getType().equals(String.class)) {
                    System.out.print("Provide value for " + field.getName() + " field: (expected data type: " + field.getType().getSimpleName() + "): ");
                    String userInput = readUserInput();

                    // Set the value of the field in the object
                    setFieldValue(object, field, userInput);
                } else {
                    // Handle object fields
                    Class<?> fieldType = field.getType();
                    Object fieldObject = fieldType.getDeclaredConstructor().newInstance();

                    // Recursively call mapObjectToJSON for the nested object
                    mapObjectToJSON(fieldObject);

                    // Set the value of the field in the object
                    field.set(object, fieldObject);
                }
            }

            String jsonString = jsonMapper.toJSONString(object);
            return jsonString;
        } catch (Exception e) {
            System.out.println("Error mapping inner object to JSON: " + e.getMessage());
        }
        return "";
    }

    private static void setFieldValue(Object object, Field field, String value) throws IllegalAccessException {
        Class<?> fieldType = field.getType();

        if (fieldType == String.class) {
            field.set(object, value);
        } else if (fieldType == int.class || fieldType == Integer.class) {
            field.set(object, Integer.parseInt(value));
        } else if (fieldType == double.class || fieldType == Double.class) {
            field.set(object, Double.parseDouble(value));
        } else if (fieldType == boolean.class || fieldType == Boolean.class) {
            field.set(object, Boolean.parseBoolean(value));
        } else {
            // Handle other types as needed
        }
    }
}
