package lesson1;

import java.util.ArrayList;

public class Box<F extends Fruit> {
    private ArrayList<F> list;

    public ArrayList<F> getList() {
        return list;
    }

    public Box() {
        list = new ArrayList<F>();
    }

    void add(F obj) {
        list.add(obj);
    }

    float getWeight() {
        if (list.isEmpty()) {
            return 0;
        } else {
            return list.size() * list.get(0).getWeight();
        }
    }

    boolean compare(Box<? extends Fruit> box) {
        return this.getWeight() == box.getWeight();
    }

    void sprinkle(Box<F> box) {
        for (F obj : list) {
            box.add(obj);
        }
        list.clear();
    }

    void info() {
        if(list.isEmpty()) {
            System.out.println("Коробка пуста");
        } else {
            System.out.println("В коробке " + list.get(0).toString() + " " + list.size() + " штуки");
        }
    }
}
