package com.test.facade;

public class Facade {
    private Class1 one = new Class1();
    private Class2 two = new Class2();
    private Class3 three = new Class3();
    public void op1() {
        System.out.println("Facade op1()");
        one.op1();
    }
    public void op2() {
        System.out.println("Facade op2()");
        two.op2();
    }
    public void op3() {
        System.out.println("Facade op3()");
        three.op3();
    }

    public void Method() {
        System.out.println("Facade Method()");
        three.op3();
        two.op2();
        one.op1();
    }

    public static void main(String[] args) {
        Facade facade = new Facade();
        facade.Method();
    }
}

class Class1 {
    public void op1() {
        System.out.println("方法1");
    }
}

class Class2 {
    public void op2() {
        System.out.println("方法2");
    }
}

class Class3 {
    public void op3() {
        System.out.println("方法3");
    }
}