package com.aleksey.merchants.Render.Blocks;

import com.aleksey.merchants.Core.PointF;

public class BoundTransform
{
    public Bound Bound;
    public PointF[] Transforms;
    
    public BoundTransform(Bound bound, PointF[] transforms)
    {
        Bound = bound;
        Transforms = transforms;
    }
}
