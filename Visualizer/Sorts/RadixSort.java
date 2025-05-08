package Visualizer.Sorts;

import Visualizer.SortingVisualizer;
import Visualizer.VisualizerFrame;
import java.util.ArrayList;
import java.util.List;

public class RadixSort implements Runnable {

    private static final int NUM_BUCKETS = 10; // Constant for the number of buckets (0-9)
    private Integer[] toBeSorted;
    private VisualizerFrame frame;
    private boolean lsd;

    public RadixSort(Integer[] toBeSorted, VisualizerFrame frame, boolean lsd) {
        this.toBeSorted = toBeSorted;
        this.frame = frame;
        this.lsd = lsd;
    }

    @Override
    public void run() {
        if (lsd) {
            radixLSD(toBeSorted);
        } else {
            radixMSD(toBeSorted, findMaxDigit(toBeSorted));
        }
        SortingVisualizer.isSorting = false;
    }

    // Radix Sort (LSD - Least Significant Digit)
    private void radixLSD(Integer[] array) {
        int maxDigit = findMaxDigit(array);
        Integer[] sortedArray = array;

        for (int digit = 1; digit <= maxDigit; digit++) {
            sortedArray = distributeAndCollect(sortedArray, digit);
            frame.reDrawArray(sortedArray);
            sleep();
        }
    }

    // Radix Sort (MSD - Most Significant Digit)
    private void radixMSD(Integer[] array, int digit) {
        if (digit < 1 || array.length <= 1) {
            return;
        }

        Integer[] sortedArray = distributeAndCollect(array, digit);
        frame.reDrawArray(sortedArray);
        sleep();

        // Recursively sort each bucket
        int start = 0;
        for (int i = 0; i < NUM_BUCKETS; i++) {
            int end = start;
            while (end < sortedArray.length && getDigit(sortedArray[end], digit) == i) {
                end++;
            }
            radixMSD(subArray(sortedArray, start, end), digit - 1);
            start = end;
        }
    }

    // Distribute elements into buckets and collect them back
    private Integer[] distributeAndCollect(Integer[] array, int digit) {
        List<Integer>[] buckets = createBuckets();

        // Distribute elements into buckets based on the current digit
        for (int num : array) {
            int bucketIndex = getDigit(num, digit);
            buckets[bucketIndex].add(num);
        }

        // Collect elements back into a single array
        List<Integer> collected = new ArrayList<>();
        for (List<Integer> bucket : buckets) {
            collected.addAll(bucket);
        }

        return collected.toArray(new Integer[0]);
    }

    // Create empty buckets
    private List<Integer>[] createBuckets() {
        List<Integer>[] buckets = new ArrayList[NUM_BUCKETS];
        for (int i = 0; i < NUM_BUCKETS; i++) {
            buckets[i] = new ArrayList<>();
        }
        return buckets;
    }

    // Extract the digit at the given position (1 = least significant digit)
    private int getDigit(int number, int position) {
        return (number / (int) Math.pow(10, position - 1)) % 10;
    }

    // Find the maximum number of digits in the array
    private int findMaxDigit(Integer[] array) {
        int max = Integer.MIN_VALUE;
        for (int num : array) {
            max = Math.max(max, num);
        }
        return (int) Math.log10(max) + 1;
    }

    // Extract a subarray from the given array
    private Integer[] subArray(Integer[] array, int start, int end) {
        Integer[] subArray = new Integer[end - start];
        System.arraycopy(array, start, subArray, 0, end - start);
        return subArray;
    }

    // Sleep for the specified duration
    private void sleep() {
        try {
            Thread.sleep(SortingVisualizer.sleep);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore the interrupted status
            System.err.println("Thread interrupted during sleep.");
        }
    }
}
