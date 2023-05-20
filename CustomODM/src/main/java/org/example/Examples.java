package org.example;

import java.util.List;
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

        public List<String> getFeatures() {
            return this.features;
        }
        public Car(String make, String model, int year, List<String> features, Engine engine) {
            this.make = make;
            this.model = model;
            this.year = year;
            this.features = features;
            this.engine = engine;
        }

        // Getters and setters

        public static class Engine {
            private String type;
            private double displacement;

            public Engine(String type, double displacement) {
                this.type = type;
                this.displacement = displacement;
            }

            // Getters and setters
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

        public int getAge() {
            return this.age;
        }

        public List<Address> getAddresses() {
            return this.addresses;
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

            public String getCity() {
                return this.city;
            }

            public String getState() {
                return this.state;
            }

            public String getPostalCode() {
                return this.postalCode;
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
