package com.example.parthpachchigar.hardwaredevice;

/**
 * Created by parth.pachchigar on 10/30/17.
 */

public class HeadingBuffer {
    static int[] buffer={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    static int index=0;

    public static boolean bufferEmpty(){
        for(int k=0;k<buffer.length;k++){
            if(buffer[k]==0){
                return true;
            }
        }
        return false;
    }

    public static void addVal(int val){
        buffer[index]=val;
        index++;
        if(index==buffer.length){
            index=0;
        }
    }

    public static double getAvg(){
        int sum=0;
        for(int k=0;k<buffer.length;k++){
            sum+=buffer[k];
        }
        return sum/40;
    }
}
