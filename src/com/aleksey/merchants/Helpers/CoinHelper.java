package com.aleksey.merchants.Helpers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

public class CoinHelper
{
    public static final String TagName_Key = "Key";
    public static final String TagName_Name = "Name";
    public static final String TagName_Weight = "Weight";
    public static final String TagName_Die = "Die";

    public static final int DieStride = 12;
    public static final int MaxFlanWeightInHundreds = 50 * 100;//100 = 1oz;
    
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
    
    public static String getCoinName(ItemStack itemStack)
    {
        return itemStack.getTagCompound().getString(TagName_Name);
    }
    
    public static int getCoinWeight(ItemStack itemStack)
    {
        return itemStack.getTagCompound().getInteger(TagName_Weight);
    }

    public static byte[] getCoinDie(ItemStack itemStack)
    {
        return itemStack.getTagCompound().getByteArray(TagName_Die);
    }

    public static void copyDie(ItemStack srcStack, ItemStack dstStack)
    {
        if(!dstStack.hasTagCompound())
            dstStack.setTagCompound(new NBTTagCompound());
        
        NBTTagCompound srcTag = srcStack.getTagCompound();
        NBTTagCompound dstTag = dstStack.getTagCompound();
        
        dstTag.setString(TagName_Key, srcTag.getString(TagName_Key));
        dstTag.setString(TagName_Name, srcTag.getString(TagName_Name));
        dstTag.setInteger(TagName_Weight, srcTag.getInteger(TagName_Weight));
        dstTag.setByteArray(TagName_Die, srcTag.getByteArray(TagName_Die));
    }
}