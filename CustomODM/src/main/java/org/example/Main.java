package org.example;

import static org.example.MyODM.toJSON;

public class Main {
    public static void main(String[] args) throws IllegalAccessException {
        MyObject.MyInnerObject innerObject = new MyObject.MyInnerObject("Ania", "Ola", 12);
        MyObject obj = new MyObject("Hello", 123, innerObject);
        String json = toJSON(obj);
        System.out.println(json);
    }

    static class MyObject {
        @JSONProperty(name = "field_1")
        private String field1;
        @JSONProperty(name = "field_2")
        private int field2;

        @JSONProperty(name= "object1")
        private MyInnerObject innerObject;

        public MyObject(String field1, int field2, MyInnerObject innerObject) {
            this.field1 = field1;
            this.field2 = field2;
            this.innerObject = innerObject;
        }

        static class MyInnerObject {
            private String field1;

            private String field2;

            private int field3;

            public MyInnerObject(String field1, String field2, int field3) {
                this.field1 = field1;
                this.field2 = field2;
                this.field3 = field3;
            }
        }
    }
}