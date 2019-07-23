package com.dutchjelly.craftenhance.crafthandling;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import com.dutchjelly.craftenhance.CraftEnhance;
import com.dutchjelly.craftenhance.Util.RecipeUtil;
import com.dutchjelly.craftenhance.model.CFurnaceRecipe;
import com.dutchjelly.craftenhance.model.CustomRecipe;
import com.dutchjelly.craftenhance.model.RecipeType;
import org.bukkit.Material;
import org.bukkit.inventory.*;

import com.dutchjelly.craftenhance.messaging.Debug;
import com.dutchjelly.craftenhance.model.CraftRecipe;

import static com.dutchjelly.craftenhance.model.RecipeType.FURNACE;

public class RecipeLoader {
	
	
	private CraftEnhance main;
	private Iterator<org.bukkit.inventory.Recipe> iterator;

	List<FurnaceRecipe> furnaceRecipes = new ArrayList<>();
	List<ShapedRecipe> shapedRecipes = new ArrayList<>();
	List<ShapelessRecipe> shapelessRecipes = new ArrayList<>();

	
	public RecipeLoader(CraftEnhance main){
		this.main = main;
	}

	public void mapExistingRecipes(){
	    main.getServer().resetRecipes(); //maybe redundant, but why not
	    Iterator<Recipe> iterator = main.getServer().recipeIterator();
	    while(iterator.hasNext()){
	        Recipe r = iterator.next();
	        if(r instanceof FurnaceRecipe)
	            furnaceRecipes.add((FurnaceRecipe)r);
	        else if(r instanceof ShapedRecipe)
	            shapedRecipes.add((ShapedRecipe)r);
	        else if(r instanceof ShapelessRecipe)
	            shapelessRecipes.add((ShapelessRecipe)r);
        }
    }

    public void ensureLoaded(CustomRecipe recipe){
        if(isLoaded(recipe)) return;
        switch (recipe.getRecipeType()){
            case CRAFTING_TABLE:
                loadRecipe((CraftRecipe) recipe);
                break;
            case FURNACE:
                loadRecipe((CFurnaceRecipe) recipe);
                break;
        }
    }

    public void ensureUnloaded(CustomRecipe recipe){
        if(!isLoaded(recipe)) return;
        switch (recipe.getRecipeType()){
            case CRAFTING_TABLE:
                unloadRecipe((CraftRecipe) recipe);
                break;
            case FURNACE:
                unloadRecipe((CFurnaceRecipe) recipe);
                break;
        }
    }

    private void loadRecipe(CFurnaceRecipe recipe){
        //add the FurnaceRecipe to the server.
    }

    public void setDefaultResult(CFurnaceRecipe recipe){
        ItemStack recipeContent = recipe.getRecipe();
        if(recipeContent == null) return;
        FurnaceRecipe matching = furnaceRecipes.stream().
                filter(x -> x.getInput().getType().equals(recipeContent.getType())).
                findFirst().orElse(null);
        if(matching != null)
            recipe.setDefaultResult(matching.getResult());
    }

    public void setDefaultResult(CraftRecipe recipe){
        Recipe matching = shapedRecipes.stream().
                filter(x -> isSimilarShapedRecipe(x, recipe)).findFirst().orElse(null);
        if(matching != null){
            recipe.setDefaultResult(matching.getResult());
            return;
        }
        matching = shapelessRecipes.stream().
                filter(x -> isSimilarShapeLessRecipe(x, recipe)).findFirst().orElse(null);
        recipe.setDefaultResult(matching == null ? null : matching.getResult());
    }

    private void unloadRecipe(CFurnaceRecipe recipe){
        Iterator<Recipe> iterator = main.getServer().recipeIterator();
        while(iterator.hasNext()){
            Recipe irecipe = iterator.next();
            if(irecipe instanceof FurnaceRecipe){
                FurnaceRecipe frecipe = (FurnaceRecipe)irecipe;
                if(frecipe.getInput().equals(recipe.getRecipe())
                        && frecipe.getResult().equals(recipe.getResult())
                        && frecipe.getExperience() == recipe.getSmeltXp()){
                    iterator.remove();
                    return;
                }
            }
        }
    }

    private void unloadRecipe(CraftRecipe recipe){
        Iterator<Recipe> iterator = main.getServer().recipeIterator();
        while(iterator.hasNext()){
            Recipe irecipe = iterator.next();
            if(irecipe instanceof ShapedRecipe){
                ShapedRecipe srecipe = (ShapedRecipe)irecipe;
                if(recipe.getResult().equals(srecipe.getResult())
                        && isSimilarShapedRecipe(srecipe, recipe)){
                    iterator.remove();
                    return;
                }
            }
        }
    }

    private void loadRecipe(CraftRecipe recipe){
        main.getServer().addRecipe(RecipeUtil.ShapeRecipe(recipe));
    }



    private boolean isLoaded(CustomRecipe recipe){
	    List<Recipe> matchingResults = main.getServer().getRecipesFor(recipe.getResult());
	    if(recipe instanceof CraftRecipe){
	        CraftRecipe craftRecipe = (CraftRecipe)recipe;
            return matchingResults.stream().anyMatch(x ->
                    (x instanceof ShapedRecipe)
                    && (isSimilarShapedRecipe(x, craftRecipe)));
        }
        if(recipe instanceof CFurnaceRecipe){
            CFurnaceRecipe cFurnaceRecipe = (CFurnaceRecipe)recipe;
            return matchingResults.stream().anyMatch(x ->
                    (x instanceof FurnaceRecipe)
                    && ((FurnaceRecipe) x).getInput().equals(cFurnaceRecipe.getRecipe())
                    && ((FurnaceRecipe) x).getExperience() == cFurnaceRecipe.getSmeltXp());
        }
        return false;
    }



	public void loadRecipes(){
		if(!main.getConfig().getBoolean("enable-recipes")){
			Debug.Send("The custom recipes are disabled on the server.");
			return;
		}
		List<CraftRecipe> queue = new ArrayList<CraftRecipe>();
		org.bukkit.inventory.Recipe similar;
		main.getServer().resetRecipes();
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
	
	private boolean needsLoading(CraftRecipe r, org.bukkit.inventory.Recipe similar){
		if(similar == null) return true;
		return !RecipeUtil.AreEqualTypes(similar.getResult(), r.getResult());
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
		queue.forEach(x -> {
		    Debug.Send("Adding " + x.toString() + " with shape " + String.join(",", RecipeUtil.ShapeRecipe(x).getShape()) + " to server recipes...");
		    main.getServer().addRecipe(RecipeUtil.ShapeRecipe(x));
        });
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
		return RecipeUtil.AreEqualTypes(getShapedRecipeContent((ShapedRecipe) serverRecipe), customRecipe.getContents());
		//return contentsEqual(getShapedRecipeContent((ShapedRecipe) serverRecipe), customRecipe.getContents());
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
			if(RecipeUtil.IsNullElement(item)) continue;
			//This system works differently in 1.13.2
			if(!choices.contains(item)) return false;

			//Check if choices contains an element with the same type.
			if(choices.stream().filter(x -> RecipeUtil.AreEqualTypes(x, item)).collect(Collectors.toList()).isEmpty())
			    return false;
			choices.remove(item);
		}
		return true;
	}
	
	
	@SuppressWarnings("unused")
	private void printContent(ItemStack[] content){
		
		for(int i = 0; i < content.length; i++){
			if(content[i] == null) System.out.println(i + ": null");
			else System.out.println(i + ": " + content[i].getType());
		}
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
}
