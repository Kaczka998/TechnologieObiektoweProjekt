package org.example;

import java.util.List;

public class MyObject {
    @JSONProperty
    private String field1;
    @JSONProperty
    private int field2;

    @JSONProperty
    private MyInnerObject innerObject;

    @JSONProperty
    private List<String> list1;

    public MyObject(String field1, int field2, MyInnerObject innerObject) {
        this.field1 = field1;
        this.field2 = field2;
        this.innerObject = innerObject;
    }
    public MyObject(String field1, int field2, MyInnerObject innerObject, List<String> list1) {
        this.field1 = field1;
        this.field2 = field2;
        this.innerObject = innerObject;
        this.list1 = list1;
    }

    public List<String> getList() {
        return this.list1;
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
