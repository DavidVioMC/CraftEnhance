package com.dutchjelly.craftenhance.model;

import com.dutchjelly.craftenhance.CraftEnhance;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class CFurnaceRecipe extends CustomRecipe {


    public static CFurnaceRecipe deserialize(){
        return null;
    }

    @Override
    public Map<String, Object> serialize() {
        return null;
    }

    @Override
    public void load() {
        CraftEnhance.getPlugin().getRecipeLoader().setDefaultResult(this);
        CraftEnhance.getPlugin().getRecipeLoader().ensureUnloaded(this);
    }


    private ItemStack recipe;
    private long smeltDuration;
    private float smeltXp;
    private boolean allowHopperFlow;


    public ItemStack getRecipe() {
        return recipe;
    }

    public void setRecipe(ItemStack recipe) {
        this.recipe = recipe;
    }

    public boolean isAllowHopperFlow() {
        return allowHopperFlow;
    }

    public void setAllowHopperFlow(boolean allowHopperFlow) {
        this.allowHopperFlow = allowHopperFlow;
    }

    public long getSmeltDuration() {
        return smeltDuration;
    }

    public void setSmeltDuration(long smeltDuration) {
        this.smeltDuration = smeltDuration;
    }

    public float getSmeltXp() {
        return smeltXp;
    }

    public void setSmeltXp(float smeltXp) {
        this.smeltXp = smeltXp;
    }
}
