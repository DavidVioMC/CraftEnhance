package com.dutchjelly.craftenhance.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dutchjelly.craftenhance.CraftEnhance;
import org.bukkit.inventory.ItemStack;

import com.dutchjelly.craftenhance.files.FileManager;

public class CraftRecipe extends CustomRecipe{

	private ItemStack[] recipe;

	private CraftRecipe(){

    }
	public CraftRecipe(String perm, ItemStack result, ItemStack[] recipe){
		setPermission(perm);
		setContent(recipe);
		formatContentAmount();
	}

	public ItemStack[] getContents(){
		return recipe;
	}

	public void setContent(ItemStack[] content){
		recipe = content;
		formatContentAmount();
	}

	@SuppressWarnings("unchecked")
	public static CraftRecipe deserialize(Map<String,Object> args){
		FileManager fm = CraftEnhance.getPlugin().getFileManager();
		List<String> recipeKeys;
		CraftRecipe recipe = new CraftRecipe();

        //Deserialize content for base class.
		recipe.deserializeBase(args);

		//Deserialize the recipe of the CraftRecipe.
        recipe.setContent(new ItemStack[9]);
		recipeKeys = (List<String>)args.get("recipe");
		for(int i = 0; i < recipe.recipe.length; i++){
			recipe.recipe[i] = fm.getItem(recipeKeys.get(i));
		}
		return recipe;
	}

	@Override
	public Map<String, Object> serialize() {
		FileManager fm = CraftEnhance.getPlugin().getFileManager();
		Map<String, Object> serialized = new HashMap<>();
		serialized.putAll(serializeBase());
		String recipeKeys[] = new String[recipe.length];
		for(int i = 0; i < recipe.length; i++){
			recipeKeys[i] = fm.getItemKey(recipe[i]);
		}
		serialized.put("recipe", recipeKeys);
		return serialized;
	}

	
	private void formatContentAmount(){
		for(ItemStack item : recipe){
			if(item != null && item.getAmount() != 1)
				item.setAmount(1);
		}
	}
	
	public String toString(){
        return baseString();
	}
}
