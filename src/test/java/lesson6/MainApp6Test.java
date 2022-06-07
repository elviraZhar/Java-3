package lesson6;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MainApp6Test {
    private static MainApp6 mainApp6;

    @BeforeEach
    void init() {
        mainApp6 = new MainApp6();
    }

    @Test
    void newArrTest() {
        int[] arr = {2,1,6,8,9,4,6,2,3};
        int[] result = {6,2,3};
        assertAll(
                () -> assertArrayEquals(result, mainApp6.newArr(arr)),
                () -> assertArrayEquals(new int[]{1,2,3}, mainApp6.newArr(new int[]{7,8,4,1,2,3})),
                () -> assertArrayEquals(new int[]{9,8,7,2}, mainApp6.newArr(new int[]{4,9,8,7,2})));
    }

    @Test
    void newArrRuntimeExceptionTest() {
        assertThrows(RuntimeException.class, () -> mainApp6.newArr(new int[]{7,8,1,2,3}));
    }

    @Test
    void checkArrayTest() {
        int[] arr1 = {2,1,6,4,6,2,3};
        assertAll(
                () -> assertTrue(mainApp6.checkArray(arr1)),
                () -> assertFalse(mainApp6.checkArray(new int[]{7,8,5,2,3})),
                () -> assertTrue(mainApp6.checkArray(new int[]{3,7,9,2,3})));
    }
}