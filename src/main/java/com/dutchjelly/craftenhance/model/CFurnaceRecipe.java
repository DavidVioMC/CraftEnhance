package com.dutchjelly.craftenhance.model;

import com.dutchjelly.craftenhance.CraftEnhance;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

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
    public boolean isSimilar(Recipe bukkitRecipe) {
        return false;
    }

    @Override
    public boolean equals(Recipe bukkitRecipe) {
        return false;
    }

    @Override
    public Recipe getServerRecipe() {
        return null;
    }

    @Override
    public boolean isSimilarContent(Inventory inv) {
        if(inv instanceof FurnaceInventory){
            FurnaceInventory fInv = (FurnaceInventory)inv;
        }
        return false;
    }

    @Override
    public boolean isEqualContent(Inventory inv) {
        return false;
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
