package com.aleksey.merchants.Core;

import java.util.Map;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;

import com.bioxx.tfc.api.TFCItems;
import com.bioxx.tfc.api.Constant.Global;
import com.bioxx.tfc.api.Crafting.AnvilManager;
import com.bioxx.tfc.api.Crafting.AnvilRecipe;
import com.bioxx.tfc.api.Crafting.AnvilReq;
import com.bioxx.tfc.api.Crafting.PlanRecipe;
import com.bioxx.tfc.api.Enums.RuleEnum;

import cpw.mods.fml.common.registry.GameRegistry;

public class Recipes
{
    private static final String TrusselPlan = "trussel";
    private static final String AnvilDiePlan = "anvildie";
    private static final String FlanPlan = "flan";
    
    public static void registerRecipes()
    {
        registerWarehouseRecipes();
        registerStallRecipes();
        registerStorageRackRecipes();
    }
    
    private static void registerWarehouseRecipes()
    {
        for(int i = 0; i < 16; i++)
        {
            ItemStack warehouse = new ItemStack(BlockList.Warehouse, 1, i);
            ItemStack lumber = new ItemStack(TFCItems.SinglePlank, 1, i);
            
            GameRegistry.addRecipe(warehouse, new Object[] { "ppp", "pfp", "ppp", Character.valueOf('p'), lumber, Character.valueOf('f'), Items.feather });
        }

        for(int i = 16; i < Global.WOOD_ALL.length; i++)
        {
            ItemStack warehouse = new ItemStack(BlockList.Warehouse2, 1, i - 16);
            ItemStack lumber = new ItemStack(TFCItems.SinglePlank, 1, i);
            
            GameRegistry.addRecipe(warehouse, new Object[] { "ppp", "pfp", "ppp", Character.valueOf('p'), lumber, Character.valueOf('f'), Items.feather });
        } 
    }
    
    private static void registerStallRecipes()
    {        
        for(int i = 0; i < BlockList.Stalls.length; i++)
        {
            ItemStack stall = new ItemStack(BlockList.Stalls[i], 1);
            ItemStack lumber = new ItemStack(TFCItems.SinglePlank, 1, i);
            
            GameRegistry.addRecipe(new ShapedOreRecipe(stall, new Object[] { "pcp", "pbp", "ppp", Character.valueOf('p'), lumber, Character.valueOf('c'), "materialCloth", Character.valueOf('b'), Items.writable_book }));
        }
    }
    
    private static void registerStorageRackRecipes()
    {
        ItemStack stick = new ItemStack(TFCItems.Stick);
        
        for(int i = 0; i < 16; i++)
        {
            ItemStack storageRack = new ItemStack(BlockList.StorageRack, 1, i);
            ItemStack lumber = new ItemStack(TFCItems.SinglePlank, 1, i);
            
            GameRegistry.addRecipe(storageRack, new Object[] { "sss", "l l", "lll", Character.valueOf('s'), stick, Character.valueOf('l'), lumber});
        }

        for(int i = 16; i < Global.WOOD_ALL.length; i++)
        {
            ItemStack storageRack = new ItemStack(BlockList.StorageRack2, 1, i - 16);
            ItemStack lumber = new ItemStack(TFCItems.SinglePlank, 1, i);
            
            GameRegistry.addRecipe(storageRack, new Object[] { "sss", "l l", "lll", Character.valueOf('s'), stick, Character.valueOf('l'), lumber});
        } 
    }
    
    public static boolean areAnvilRecipesRegistered()
    {
        Map map = AnvilManager.getInstance().getPlans();
        
        return map.containsKey(TrusselPlan);
    }
    
    public static void registerAnvilRecipes(World world)
    {
        AnvilManager manager = AnvilManager.getInstance();
        //We need to set the world ref so that all anvil recipes can generate correctly
        AnvilManager.world = world;

        manager.addPlan(TrusselPlan, new PlanRecipe(new RuleEnum[] { RuleEnum.DRAWLAST, RuleEnum.UPSETNOTLAST, RuleEnum.HITNOTLAST }));
        manager.addPlan(AnvilDiePlan, new PlanRecipe(new RuleEnum[] { RuleEnum.HITLAST, RuleEnum.PUNCHNOTLAST, RuleEnum.HITNOTLAST }));
        manager.addPlan(FlanPlan, new PlanRecipe(new RuleEnum[] { RuleEnum.HITLAST, RuleEnum.SHRINKNOTLAST, RuleEnum.HITNOTLAST }));
        
        for(int i = 0; i < Constants.Dies.length; i++)
        {
            DieInfo info = Constants.Dies[i];
            AnvilReq anvil = getAnvil(info.Level);
            
            Item trusselMetalItem = GameRegistry.findItem("terrafirmacraft", info.TrusselMetalName);
            ItemStack trussel = new ItemStack(ItemList.Trussel, 1, i);

            manager.addRecipe(new AnvilRecipe(new ItemStack(trusselMetalItem), null, TrusselPlan, false, anvil, trussel).addRecipeSkill(Global.SKILL_GENERAL_SMITHING));

            Item anvilDieMetalItem = GameRegistry.findItem("terrafirmacraft", info.AnvilDieMetalName);
            ItemStack anvilDie = new ItemStack(ItemList.AnvilDie, 1, i);

            manager.addRecipe(new AnvilRecipe(new ItemStack(anvilDieMetalItem), null, AnvilDiePlan, false, anvil, anvilDie).addRecipeSkill(Global.SKILL_GENERAL_SMITHING));
        }
        
        for(int i = 0; i < Constants.Coins.length; i++)
        {
            CoinInfo info = Constants.Coins[i];
            AnvilReq anvil = getAnvil(info.Level);
            
            Item flanMetalItem = GameRegistry.findItem("terrafirmacraft", info.MetalName);
            ItemStack flan = new ItemStack(ItemList.Flan, 1, i);

            manager.addRecipe(new AnvilRecipe(new ItemStack(flanMetalItem), null, FlanPlan, false, anvil, flan).addRecipeSkill(Global.SKILL_GENERAL_SMITHING));
        }
    }
    
    private static AnvilReq getAnvil(int level)
    {
        switch(level)
        {
            case 0:
            case 1:
                return AnvilReq.COPPER;
            case 2:
                return AnvilReq.BRONZE;
            case 3:
                return AnvilReq.WROUGHTIRON;
            case 4:
                return AnvilReq.STEEL;
            case 5:
                return AnvilReq.BLACKSTEEL;
            default:
                return AnvilReq.REDSTEEL;
        }
    }
}
