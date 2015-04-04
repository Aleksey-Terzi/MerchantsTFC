package com.aleksey.merchants.Core;

import java.util.Map;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

import com.aleksey.merchants.Core.Constants;
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
    
    public static boolean areAnvilRecipesRegistered()
    {
        Map map = AnvilManager.getInstance().getPlans();
        
        return map.containsKey(TrusselPlan);
    }
    
    public static void registerAnvilRecipes()
    {
        AnvilManager manager = AnvilManager.getInstance();
        
        manager.addPlan(TrusselPlan, new PlanRecipe(new RuleEnum[] { RuleEnum.DRAWLAST, RuleEnum.UPSETANY, RuleEnum.HITANY }));
        manager.addPlan(AnvilDiePlan, new PlanRecipe(new RuleEnum[] { RuleEnum.HITLAST, RuleEnum.PUNCHANY, RuleEnum.HITANY }));
        manager.addPlan(FlanPlan, new PlanRecipe(new RuleEnum[] { RuleEnum.HITLAST, RuleEnum.SHRINKANY, RuleEnum.HITANY }));
        
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
