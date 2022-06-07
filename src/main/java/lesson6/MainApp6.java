package lesson6;

import java.util.Arrays;

public class MainApp6 {

    public static int[] newArr (int[] arr) {
        int x = 0;
        for (int i = arr.length - 1; i > 0; i--) {
            if (arr[i] == 4) {
                x = i;
            }
        }
        int [] result = new int[arr.length - x - 1];
        for (int n = 0, j = x + 1; n < result.length; n++, j++) {
                result[n] = arr[j];
        }
        if (x == 0){
            throw new RuntimeException();
        }
        return result;
    }

    public static boolean checkArray(int[] arr){

        for (int i = 0; i < arr.length; i++) {
            if(arr[i] == 1 || arr[i] == 4) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args){
        int[] arr = {1,6,7,8,4,2,1,5,3};
        int[] arr1 = {1,6,7,8,9,2,1,5,3};
        int[] arr2 = {3,3,5,6,7,2};

        System.out.println(checkArray(arr2));
        System.out.println(Arrays.toString(newArr(arr)));
        System.out.println(Arrays.toString(newArr(arr1)));
    }
}
