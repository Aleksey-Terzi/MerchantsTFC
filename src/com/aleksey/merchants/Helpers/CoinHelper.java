package com.aleksey.merchants.Helpers;

public class CoinHelper
{
    public static final int DieStride = 12;
    
    public static String getOz(int weightIndex)
    {
        switch(weightIndex)
        {
            case 1:
                return "50";
            case 20:
                return "2.5";
            case 50:
                return "1";
            case 100:
                return "0.5";
            case 200:
                return "0.25";
            default:
                return "";
        }
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