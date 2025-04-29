package be.unamur.snail.register.fixtures;

public class TestModels {
    public static class Person {
        private String name;
        private int age;
        private Person friend;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }

    public static class A {
        private String a1;
        private boolean a2;
    }

    public static class B extends A {
        private String b1;
        private int b2;
        private static boolean staticB3;
    }

    public static class NoFields {}

    public class A1 {
        private String a1;

        static class B1 {}
    }
}
