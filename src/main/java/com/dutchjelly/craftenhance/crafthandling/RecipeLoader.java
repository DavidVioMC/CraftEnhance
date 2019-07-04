package com.dutchjelly.craftenhance.crafthandling;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.dutchjelly.craftenhance.CraftEnhance;
import com.dutchjelly.craftenhance.util.RecipeUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import com.dutchjelly.craftenhance.messaging.Debug;
import com.dutchjelly.craftenhance.util.CraftRecipe;

public class RecipeLoader {
	
	
	private CraftEnhance main;
	private Iterator<org.bukkit.inventory.Recipe> iterator;
    private List<ItemStack> disabled = new ArrayList<>();
	
	public RecipeLoader(CraftEnhance main){
		this.main = main;
	}
	
	public void loadRecipes(){
		if(!main.getConfig().getBoolean("enable-recipes")){
			Debug.Send("The custom recipes are disabled on the server.");
			return;
		}
		List<CraftRecipe> queue = new ArrayList<CraftRecipe>();
		org.bukkit.inventory.Recipe similar;
		main.getServer().resetRecipes();
		unloadDisabled();
		for(CraftRecipe r : main.getFileManager().getRecipes()){
			resetIterator();
			similar = getNextSimilar(r);
			handleDefaults(r, similar);
			if(needsLoading(r, similar)){
				queue.add(r);
			}
		}
		addAll(queue);
	}

	public void disable(ItemStack result){
	    if(!disabled.contains(result))
	        disabled.add(result);
	    loadRecipes();
    }

    public List<ItemStack> getDisabled(){
	    return disabled;
    }

	public List<ItemStack> getServerRecipes(){
	    List<ItemStack> results = new ArrayList<>();
        main.getServer().resetRecipes();
        resetIterator();
        Recipe recipe;
        while(iterator.hasNext()){
            recipe = iterator.next();
            results.add(recipe.getResult());
        }
        return results;
    }

    private void unloadDisabled(){
	    resetIterator();
        while(iterator.hasNext()){
            if(disabled.contains(iterator.next().getResult())){
                iterator.remove();
            }
        }
    }

	private boolean needsLoading(CraftRecipe r, org.bukkit.inventory.Recipe similar){
		if(similar == null) return true;
		return !areEqualTypes(similar.getResult(), r.getResult());
	}

	private void handleDefaults(CraftRecipe r, org.bukkit.inventory.Recipe similar){
		if(similar == null)
			r.setDefaultResult(new ItemStack(Material.AIR));
		else
			r.setDefaultResult(similar.getResult());
	}
	
	private void resetIterator(){
		iterator = main.getServer().recipeIterator();
	}
	
	private void addAll(List<CraftRecipe> queue){
		if(queue == null) return;
		queue.forEach(x -> main.getServer().addRecipe(RecipeUtil.ShapeRecipe(x)));
	}
	
	//Uses the iterator to find a recipe with equal content.
	private org.bukkit.inventory.Recipe getNextSimilar(CraftRecipe r){
		org.bukkit.inventory.Recipe currentIteration;
		while(iterator.hasNext()){
			currentIteration = iterator.next();
			if(isSimilarShapedRecipe(currentIteration, r)) return currentIteration;
			if(isSimilarShapeLessRecipe(currentIteration, r)) return currentIteration;
		}
		return null;
	}
	
	private boolean isSimilarShapedRecipe(org.bukkit.inventory.Recipe serverRecipe, CraftRecipe customRecipe){
		if(!(serverRecipe instanceof ShapedRecipe)) return false;
		return contentsEqual(getShapedRecipeContent((ShapedRecipe) serverRecipe), customRecipe.getContents());
	}
	
	private boolean isSimilarShapeLessRecipe(org.bukkit.inventory.Recipe serverRecipe, CraftRecipe customRecipe){
		if(!(serverRecipe instanceof ShapelessRecipe)) return false;
		return allMaterialsMatch((ShapelessRecipe) serverRecipe, customRecipe);
	}
	
	private boolean allMaterialsMatch(ShapelessRecipe recipe, CraftRecipe customRecipe){
		ItemStack[] content = customRecipe.getContents();
		List<ItemStack> choices = new ArrayList<>();
		choices.addAll(recipe.getIngredientList());
		for(ItemStack item : content){
			if(item == null || item.getType().equals(Material.AIR)) continue;
			//This system works differently in 1.13.2
			if(!choices.contains(item)) return false;
			choices.remove(item);
		}
		return true;
	}

	
	private ItemStack[] getShapedRecipeContent(ShapedRecipe r){
		ItemStack[] content = new ItemStack[9];
		String[] shape = r.getShape();
		int columnIndex;
		for(int i = 0; i < shape.length; i++){
			columnIndex = 0;
			for(char c : shape[i].toCharArray()){
				content[(i*3) + columnIndex] = r.getIngredientMap().get(c);
				columnIndex++;
			}
		}
		return content;
	}
	
	private boolean contentsEqual(ItemStack[] one, ItemStack[] two){
		if(one.length != two.length) return false;
		for(int i = 0; i < one.length; i++){
			if(!areEqualItems(one[i], two[i]))
				return false;
		}
		return true;
	}
	
	private boolean areEqualItems(ItemStack one, ItemStack two){
		return one == two || (one != null && two != null && areEqualTypes(one,two));
	}
	
	private boolean areEqualTypes(ItemStack a, ItemStack b){
		return a.getType().equals(b.getType());
	}
}
