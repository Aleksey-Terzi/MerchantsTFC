package com.aleksey.merchants.Render.Blocks;

public class Bound
{
    public double MinX;
    public double MinY;
    public double MinZ;
    public double MaxX;
    public double MaxY;
    public double MaxZ;
    public int ShiftX;
    public int ShiftY;
    public int ShiftZ;
    
    public Bound(double minX, double minY, double minZ, double maxX, double maxY, double maxZ)
    {
        MinX = minX;
        MinY = minY;
        MinZ = minZ;
        MaxX = maxX;
        MaxY = maxY;
        MaxZ = maxZ;
        ShiftX = 0;
        ShiftY = 0;
        ShiftZ = 0;
    }

    public Bound(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, int shiftX, int shiftY, int shiftZ)
    {
        MinX = minX;
        MinY = minY;
        MinZ = minZ;
        MaxX = maxX;
        MaxY = maxY;
        MaxZ = maxZ;
        ShiftX = shiftX;
        ShiftY = shiftY;
        ShiftZ = shiftZ;
    }
    
    public Bound copy()
    {
        return new Bound(MinX, MinY, MinZ, MaxX, MaxY, MaxZ, ShiftX, ShiftY, ShiftZ);
    }
}
