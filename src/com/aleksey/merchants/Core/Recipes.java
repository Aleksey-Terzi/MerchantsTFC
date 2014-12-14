package com.aleksey.merchants.Core;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

import com.bioxx.tfc.TFCItems;
import com.bioxx.tfc.api.Constant.Global;

import cpw.mods.fml.common.registry.GameRegistry;

public class Recipes
{
    public static void registerRecipes()
    {
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockList.Warehouse, 1), new Object[] { "ppp", "pfp", "ppp", Character.valueOf('p'), "woodLumber", Character.valueOf('f'), Items.feather }));

        registerStallRecipes();
    }
    
    private static void registerStallRecipes()
    {
        ItemStack[] clothes = new ItemStack[] {
            new ItemStack(TFCItems.WoolCloth, 1),
            new ItemStack(TFCItems.SilkCloth, 1),
            new ItemStack(TFCItems.BurlapCloth, 1),
        };
        
        ItemStack stall = new ItemStack(BlockList.Stall, 1);
        
        for(int i = 0; i < clothes.length; i++)
        {
            GameRegistry.addRecipe(new ShapedOreRecipe(stall, new Object[] { "pcp", "pbp", "ppp", Character.valueOf('p'), "woodLumber", Character.valueOf('c'), clothes[i], Character.valueOf('b'), Items.writable_book }));
        }
    }
}