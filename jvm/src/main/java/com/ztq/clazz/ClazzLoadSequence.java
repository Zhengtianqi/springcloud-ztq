package com.ztq.clazz;

/**
 * class的加载顺序
 *
 * @author zhengtianqi
 */
public class ClazzLoadSequence {

    static class T1 {
        public static T1 t = new T1(); // step1. count = 0
        static int count = 2; // step3. count = 2

        private T1() {
            count++; // step2. count = 1
        }
    }

    static class T2 {
        static int count = 2; // step1. count = 2
        public static T2 t = new T2(); // step2. count = 2

        private T2() {
            count++; // step3. count = 3
        }
    }


    public static void main(String[] args) {
        // 2
        System.out.println(T1.count);
        // 3
        System.out.println(T2.count);
    }

}
