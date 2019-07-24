package com.dutchjelly.craftenhance.crafthandling;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.dutchjelly.craftenhance.Util.RecipeUtil;
import com.dutchjelly.craftenhance.model.CustomRecipe;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Material;
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.JavaPlugin;

public class RecipeLoader {
	
	
	private JavaPlugin plugin;

	private List<Recipe> originalRecipes = new ArrayList<>();

	public static RecipeLoader init(JavaPlugin plugin){
	    RecipeLoader loader = new RecipeLoader(plugin);
        plugin.getServer().resetRecipes(); //maybe redundant, but why not
        Iterator<Recipe> iterator = plugin.getServer().recipeIterator();
        while(iterator.hasNext()){
            loader.originalRecipes.add(iterator.next());
        }
        return loader;
    }

    public RecipeLoader(){
	    throw new NotImplementedException("Use the init factory method to make instances.");
    }

	private RecipeLoader(JavaPlugin plugin){
		this.plugin = plugin;
	}

	//Makes sure if the recipe is registered on the server properly. To do
    //this it'll set the default result and load the recipe if necessary.
    public void ensureLoaded(CustomRecipe recipe){
	    if(recipe == null) return;
	    if(RecipeUtil.IsNullElement(recipe.getDefaultResult()))
	        setDefaultResult(recipe);
        if(isLoaded(recipe)) return;
        plugin.getServer().addRecipe(recipe.getServerRecipe());
    }

    public void ensureLoaded(List<CustomRecipe> recipes){
        if(recipes == null) return;
        recipes.forEach(x -> ensureUnloaded(x));
    }

    public void ensureUnloaded(CustomRecipe recipe){
        if(recipe == null) return;
        if(!isLoaded(recipe)) return;
        Iterator<Recipe> iterator = plugin.getServer().recipeIterator();
        while(iterator.hasNext()){
            if(recipe.equals(iterator.next())){
                iterator.remove();
                return;
            }
        }
    }

    public void ensureUnloaded(List<CustomRecipe> recipes){
	    if(recipes == null) return;
	    recipes.forEach(x -> ensureUnloaded(x));
    }

    private void setDefaultResult(CustomRecipe recipe){
	    Recipe similar = originalRecipes.stream().filter(x -> recipe.isSimilar(x)).findFirst().orElse(null);
	    if(similar == null)
	        recipe.setDefaultResult(new ItemStack(Material.AIR));
	    else
	        recipe.setDefaultResult(similar.getResult());
    }

    private boolean isLoaded(CustomRecipe recipe){
	    List<Recipe> matchingResults = plugin.getServer().getRecipesFor(recipe.getResult());
	    return matchingResults.stream().anyMatch(x -> recipe.equals(x));
    }




}
