package lesson5;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class Car implements Runnable {
    private static int CARS_COUNT;
    private Race race;
    private int speed;
    private String name;
    private static CyclicBarrier startBarrier;
    private static CountDownLatch countDownLatchFinish;
    private static CountDownLatch countDownLatchReady;
    private static boolean winner;

    static {
        countDownLatchFinish = MainApp.countDownLatchFinish;
        countDownLatchReady = MainApp.countDownLatchReady;
        startBarrier = MainApp.startBarrier;
    }

    public String getName() {
        return name;
    }
    public int getSpeed() {
        return speed;
    }
    public Car(Race race, int speed) {
        this.race = race;
        this.speed = speed;
        CARS_COUNT++;
        this.name = "Участник #" + CARS_COUNT;
    }
    public static void checkWinner(Car c) {
        if (!winner) {
            System.out.println("========================");
            System.out.println(c.name + " Победитель!");
            System.out.println("========================");
            winner = true;
        }
    }

    @Override
    public void run() {
        try {
            System.out.println(this.name + " готовится");
            Thread.sleep(500 + (int)(Math.random() * 800));

            System.out.println(this.name + " готов");
            countDownLatchReady.countDown();
            startBarrier.await();

        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < race.getStages().size(); i++) {
            race.getStages().get(i).go(this);
        }
        checkWinner(this);
        System.out.println(this.name + " финишировал");
        countDownLatchFinish.countDown();
    }
}
