package org.example;

import java.util.List;
import org.example.JSONProperty;
//Class holding example custom classes used for testing Mapper and Parser.
public class Examples {

    public static class Car {
        @JSONProperty
        private String make;
        @JSONProperty
        private String model;
        @JSONProperty
        private int year;
        @JSONProperty
        private List<String> features;
        @JSONProperty
        private Engine engine;

        public Car(String make, String model, int year, List<String> features, Engine engine) {
            this.make = make;
            this.model = model;
            this.year = year;
            this.features = features;
            this.engine = engine;
        }

        public Car() {
            // Default constructor
        }

        // Getters and setters
        public String getMake() {
            return make;
        }

        public void setMake(String make) {
            this.make = make;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public List<String> getFeatures() {
            return features;
        }
        public void setFeatures(List<String> features) {
            this.features = features;
        }

        public Engine getEngine() {
            return engine;
        }

        public void setEngine(Engine engine) {
            this.engine = engine;
        }

        public static class Engine {
            @JSONProperty
            private String type;
            @JSONProperty
            private double displacement;

            public Engine(String type, double displacement) {
                this.type = type;
                this.displacement = displacement;
            }

            public Engine(){
                // Default constructor
            }
            // Getters and setters
            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public double getDisplacement() {
                return displacement;
            }

            public void setDisplacement(double displacement) {
                this.displacement = displacement;
            }
        }
    }

    public static class Person {
        @JSONProperty
        private String name;
        @JSONProperty
        private int age;
        @JSONProperty
        private List<Address> addresses;

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return this.age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public List<Address> getAddresses() {
            return this.addresses;
        }

        public void setAddresses(List<Address> addresses) {
            this.addresses = addresses;
        }

        public Person() {
        }

        public Person(String name, int age, List<Address> addresses) {
            this.name = name;
            this.age = age;
            this.addresses = addresses;
        }

        public static class Address {
            @JSONProperty
            private String street;
            @JSONProperty
            private String city;
            @JSONProperty
            private String state;
            @JSONProperty
            private String postalCode;

            public String getStreet() {
                return this.street;
            }

            public void setStreet(String street) {
                this.street = street;
            }

            public String getCity() {
                return this.city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getState() {
                return this.state;
            }

            public void setState(String state) {
                this.state = state;
            }

            public String getPostalCode() {
                return this.postalCode;
            }

            public void setPostalCode(String postalCode) {
                this.postalCode = postalCode;
            }

            public Address(){

            }

            public Address(String street, String city, String state, String postalCode) {
                this.street = street;
                this.city = city;
                this.state = state;
                this.postalCode = postalCode;
            }

            // Getters and setters
        }
    }
}
