package org.example;

public class Helper {

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

    public static String capitalize(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

}
