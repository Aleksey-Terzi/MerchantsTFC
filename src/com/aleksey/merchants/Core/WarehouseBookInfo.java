package com.aleksey.merchants.Core;

import net.minecraft.nbt.NBTTagCompound;

public class WarehouseBookInfo
{
    public int X;
    public int Y;
    public int Z;
    public int Key;
    
    public static WarehouseBookInfo readFromNBT(NBTTagCompound nbt)
    {
        if(nbt == null)
            return null;
        
        WarehouseBookInfo info = new WarehouseBookInfo();
        info.X = nbt.getInteger("X");
        info.Y = nbt.getInteger("Y");
        info.Z = nbt.getInteger("Z");
        info.Key = nbt.getInteger("Key");
        
        return info;
    }
    
    public void writeToNBT(NBTTagCompound nbt)
    {
        nbt.setInteger("X", X);
        nbt.setInteger("Y", Y);
        nbt.setInteger("Z", Z);
        nbt.setInteger("Key", Key);
    }
}
