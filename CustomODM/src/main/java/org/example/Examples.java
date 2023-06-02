package org.example;

import java.util.List;
public class Examples {
    public static class Car {

        private String company;

        private String model;

        private int year;

        private List<String> features;

        private Engine engine;

        public Car(String company, String model, int year, List<String> features, Engine engine) {
            this.company = company;
            this.model = model;
            this.year = year;
            this.features = features;
            this.engine = engine;
        }

        public Car() {
            // Default constructor
        }

        // Getters and setters
        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
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
            private String type;
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

        private String name;

        private int age;

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

            private String street;

            private String city;

            private String state;

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
