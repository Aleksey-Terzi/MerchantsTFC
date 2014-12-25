package com.aleksey.merchants.Helpers;

import net.minecraft.util.StatCollector;

public class CoinHelper
{
    public static final int DieStride = 12;
    public static final int MaxFlanWeight = 50 * 100;//100 = 1oz;
    
    public static double getWeightOz(int weightIndex)
    {
        switch(weightIndex)
        {
            case 1:
                return 50;
            case 20:
                return 2.5;
            case 50:
                return 1;
            case 100:
                return 0.5;
            case 200:
                return 0.25;
            default:
                return 0;
        }
    }
    
    public static String getWeightText(int weightIndex)
    {
        String weightInOz = String.valueOf(getWeightOz(weightIndex));
        
        return weightInOz + StatCollector.translateToLocal("Oz") + " (" + String.valueOf(weightIndex) + ")";
    }
    
    public static byte[] packDie(boolean[] bits)
    {
        int dataLen = bits.length / 8;
        
        if((bits.length % 8) != 0)
            dataLen++;
        
        byte[] data = new byte[dataLen];
        int mask = 1;
        
        for(int i = 0; i < bits.length; i++)
        {
            int dataIndex = i / 8;
            byte dataByte = data[dataIndex];
            int bitIndex = i % 8;
            
            if(bitIndex == 0)
                dataByte = 0;
            
            if(bits[i])
                dataByte |= (byte)(mask << bitIndex);
            
            data[dataIndex] = dataByte;
        }
        
        return data;
    }
    
    public static boolean[] unpackDie(byte[] bytes)
    {
        boolean[] bits = new boolean[bytes.length * 8];
        
        int mask = 1;
        
        for(int i = 0; i < bits.length; i++)
        {
            int dataIndex = i / 8;
            byte dataByte = bytes[dataIndex];
            int bitIndex = i % 8;
            
            bits[i] = (dataByte & ((byte)(mask << bitIndex))) != 0;
        }
        
        return bits;
    }
}