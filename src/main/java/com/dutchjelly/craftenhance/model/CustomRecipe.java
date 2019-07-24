package com.dutchjelly.craftenhance.model;

import com.dutchjelly.craftenhance.CraftEnhance;
import com.dutchjelly.craftenhance.files.FileManager;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("Recipe")
public abstract class CustomRecipe implements ConfigurationSerializable {

    @Override
    public abstract Map<String, Object> serialize();

    public static CustomRecipe deserialize(Map<String,Object> args){
        CustomRecipe recipe;
        switch((RecipeType) args.get("type")){
            case CRAFTING_TABLE:
                recipe = CraftRecipe.deserialize(args);
                recipe.recipeType = RecipeType.CRAFTING_TABLE;
                return recipe;
            case FURNACE:
                break;
        }
        return null;
    }

    //Function only accessible for child objects.
    protected void deserializeBase(Map<String,Object> args){
        FileManager fm = CraftEnhance.getPlugin().getFileManager();
        permission = (String)args.get("permission");
        result = fm.getItem((String)args.get("result"));
    }

    protected Map<String,Object> serializeBase(){
        FileManager fm = CraftEnhance.getPlugin().getFileManager();
        Map<String,Object> serialized = new HashMap<>();
        serialized.put("permission", getPermission());
        serialized.put("result", fm.getItemKey(getResult()));
        serialized.put("type", recipeType);
        return serialized;
    }

    private ItemStack result;
    private String key;
    private ItemStack defaultResult;
    private String permission;
    private RecipeType recipeType;

    public RecipeType getRecipeType() {
        return recipeType;
    }

    public ItemStack getDefaultResult() {
        return defaultResult;
    }

    public void setDefaultResult(ItemStack defaultResult) {
        this.defaultResult = defaultResult;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public ItemStack getResult() {
        return result;
    }

    public void setResult(ItemStack result) {
        this.result = result;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String baseString(){
        return "Recipe of " + (result != null ? result.getType().name() : "null") + " with key " + (key != null ? key : "null");
    }

    //If the bukkitRecipe gives the same result as this recipe, it's similar.
    public abstract boolean isSimilar(Recipe bukkitRecipe);

    //If the recipe is similar and if the other properties match, it's equal.
    public abstract boolean equals(Recipe bukkitRecipe);

    public abstract Recipe getServerRecipe();

}
