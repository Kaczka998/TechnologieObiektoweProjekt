package org.example;

public class Helper {

    //Method that checks if className is a name of custom class predefined by user. In this specific scenario
    //it checks only Examples.java for classes, but it can be modified or class file name can be parameterized.
    public static Class<?> findClass(String className) {
        Class<?>[] declaredClasses = Examples.class.getDeclaredClasses();
        for (Class<?> declaredClass : declaredClasses) {
            if (declaredClass.getSimpleName().equals(className)) {
                return declaredClass;
            } else {
                Class<?>[] nestedClasses = declaredClass.getDeclaredClasses();
                for (Class<?> nestedClass : nestedClasses) {
                    if (nestedClass.getSimpleName().equals(className)) {
                        return nestedClass;
                    }
                }
            }
        }
        return null;
    }

    //Method for simply modifying string to start with upper case letter.
    public static String capitalize(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

}
