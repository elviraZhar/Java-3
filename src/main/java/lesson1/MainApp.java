package lesson1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainApp {
    public static void main (String [] args){
     String [] color = {"red", "blue", "green", "black", "white", "gray", "orange"};

     int n = color.length;//1. Метод который меняет элементы массива местами
     String temp;

     for (int i = 0; i < n/2; i++) {
         temp = color[n-i-1];
         color[n-i-1] = color[i];
         color[i] = temp;
     }
     for (int i = 0; i < color.length; i++) {
         System.out.println(color[i]);
     }

        System.out.println();

        List<String> colorList = new ArrayList<>();//2. Метод преобразования массива в ArrayList
        Collections.addAll(colorList, color);
        for (String str : colorList)
            System.out.println(str);

        System.out.println();

        Orange orange1 = new Orange();//3. Задача (коробка с фруктами)
        Orange orange2 = new Orange();
        Apple apple1 = new Apple();
        Apple apple2 = new Apple();
        Apple apple3 = new Apple();

        Box<Orange> orangeBox = new Box<>();
        Box<Apple> appleBox1 = new Box<>();
        Box<Apple> appleBox2 = new Box<>();

        orangeBox.add(orange1);
        orangeBox.add(orange2);
        orangeBox.info();

        appleBox1.add(apple1);
        appleBox1.add(apple2);
        appleBox1.add(apple3);
        appleBox1.info();

        appleBox1.sprinkle(appleBox2);
        appleBox2.info();

        System.out.println("Коробки с фруктами равны: " + (orangeBox.compare(appleBox2)));
     }
}
