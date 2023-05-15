package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.example.MyODM.toJSON;

public class Main {
    public static void main(String[] args) throws IllegalAccessException {
        MyObject.MyInnerObject innerObject = new MyObject.MyInnerObject("Ania", "Ola", 12);
        MyObject obj = new MyObject("Hello", 123, innerObject);
        MyObject obj2 = obj;
        MyObject obj3 = obj;
        List<MyObject> objList = Arrays.asList(obj, obj2, obj3);

        try {
            // Convert objects to JSON
            String json = MyODM.toJSON(objList);

            // Print JSON
            System.out.println("JSON: " + json);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        String json = toJSON(obj);
        System.out.println("obj " + json);

        List<String> list = Arrays.asList("Mleko", "Ala!", "Kot", "24313fdsd1", "Pies");
        MyObject obj1 = new MyObject("Hello", 123, innerObject, list);
        String json1 = toJSON(obj1);
        System.out.println("obj1 " + json1);

        // Create Car object
        List<String> carFeatures = Arrays.asList("GPS", "Bluetooth", "Backup camera");
        Examples.Car.Engine carEngine = new Examples.Car.Engine("V6", 3.5);
        Examples.Car car = new Examples.Car("Toyota", "Camry", 2022, carFeatures, carEngine);

        // Create Person object
        List<Examples.Person.Address> addresses = Arrays.asList(
                new Examples.Person.Address("123 Main St", "City1", "State1", "12345"),
                new Examples.Person.Address("456 Elm St", "City2", "State2", "67890")
        );
        Examples.Person person = new Examples.Person("John Doe", 30, addresses);

        try {
            // Convert objects to JSON
            String carJson = MyODM.toJSON(car);
            String personJson = MyODM.toJSON(person);

            // Print JSON output
            System.out.println("Car JSON: " + carJson);
            System.out.println("Person JSON: " + personJson);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}