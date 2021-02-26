package com.tess4s.java.concurrency;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Sean
 * Create:   2/26/2021 2:35 PM
 */
public class Bug6260652 {
    public static void main(String[] args) {
        test3();
    }

    /**
     * normal
     */
    public static void test1() {
        System.out.println("test3");
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        Object[] objects = list.toArray();
        System.out.println(objects.getClass().getCanonicalName());
        objects[0] = new Object();
        print(objects);
    }

    /**
     * underlying is String[], not Object[]
     */
    public static void test2() {
        System.out.println("test2");
        List<String> list = Arrays.asList("a", "b");
        Object[] objects = list.toArray();
        System.out.println(objects.getClass().getCanonicalName());
        objects[0] = new Object();
        print(objects);
    }

    /**
     * underlying is String[], not Object[]
     */
    public static void test3() {
        System.out.println("test3");
        Object[] objects = new String[]{"a", "b"};
        System.out.println(objects.getClass().getCanonicalName());
        objects[0] = 7;
        print(objects);
    }

    public static void print(Object[] arr) {
        for (Object o : arr) {
            System.out.println(o + " ");
        }
    }
}
