package org.example;

/**
 * @ClassName BubbleSort
 * @Description 冒泡排序  时间复杂度 o(n^2)
 * @Author yzchao
 * @Date 2022/11/29 21:34
 * @Version V1.0
 */
public class BubbleSort {

    public static void bubbleSort(int[] data){
        int arrayLength = data.length;

        for(int i = 1; i < arrayLength; i++) { //第i次排序
            for(int j = 0;  j < arrayLength-i; j++) { //从索引为 j 的数开始

                if(data[j] > data[j+1]){ //相邻元素两两对比
                    int temp = data[j+1];
                    data[j+1] = data[j];
                    data[j] = temp;
                }
            }
            System.out.println("第" + i + "次排序：\n" + java.util.Arrays.toString(data));
            }
        }

    public static void main(String[] args) {
        int[] data = {3, 44, 38, 5, 47, 15, 36, 26, 27, 2, 46, 4};
        System.out.println("排序之前：\n" + java.util.Arrays.toString(data));

        bubbleSort(data);
        System.out.println("排序之后：\n" + java.util.Arrays.toString(data));
        }
    }





