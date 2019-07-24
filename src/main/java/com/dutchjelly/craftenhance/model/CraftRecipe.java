package com.dutchjelly.craftenhance.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.dutchjelly.bukkitadapter.Adapter;
import com.dutchjelly.craftenhance.CraftEnhance;
import com.dutchjelly.craftenhance.Util.RecipeUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.inventory.ItemStack;

import com.dutchjelly.craftenhance.files.FileManager;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import static com.dutchjelly.craftenhance.Util.RecipeUtil.IsNullArray;
import static com.dutchjelly.craftenhance.Util.RecipeUtil.IsNullElement;

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
	public boolean isSimilar(Recipe bukkitRecipe) {

	    //This could be done in one return statement, but it'd be harder to read.
		if(bukkitRecipe instanceof ShapedRecipe){
            return RecipeUtil.AreEqualTypes(getShapedRecipeContent((ShapedRecipe) bukkitRecipe), getContents());
		}
		if(bukkitRecipe instanceof ShapelessRecipe){
            return allMaterialsMatch((ShapelessRecipe) bukkitRecipe, getContents());
        }
        return false;
	}

    @Override
    public boolean equals(Recipe bukkitRecipe) {
        return isSimilar(bukkitRecipe) && this.getResult().equals(bukkitRecipe.getResult());
    }

    @Override
    public Recipe getServerRecipe(){
        ItemStack[] content = getContents().clone();
        format(content);

        ShapedRecipe shaped = Adapter.GetShapedRecipe(
                CraftEnhance.getPlugin(CraftEnhance.class),
                "ceh" + RecipeUtil.MakeNameSpaced(getKey()),
                getResult()
        );
        shaped.shape(getShape(content));
        mapIngredients(shaped, content);
        return shaped;
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


    private boolean isSimilarShapedRecipe(Recipe serverRecipe, CraftRecipe customRecipe){
        if(!(serverRecipe instanceof ShapedRecipe)) return false;
        return RecipeUtil.AreEqualTypes(getShapedRecipeContent((ShapedRecipe) serverRecipe), customRecipe.getContents());
    }

    private boolean isSimilarShapeLessRecipe(Recipe serverRecipe, CraftRecipe customRecipe){
        if(!(serverRecipe instanceof ShapelessRecipe)) return false;
        return allMaterialsMatch((ShapelessRecipe) serverRecipe, getContents());
    }

    private boolean allMaterialsMatch(ShapelessRecipe recipe, ItemStack[] content){
        List<ItemStack> choices = new ArrayList<>();
        choices.addAll(recipe.getIngredientList());
        for(ItemStack item : content){
            if(IsNullElement(item)) continue;
            //This system works differently in 1.13.2
            if(!choices.contains(item)) return false;

            //Check if choices contains an element with the same type.
            if(choices.stream().filter(x -> RecipeUtil.AreEqualTypes(x, item)).collect(Collectors.toList()).isEmpty())
                return false;
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

    //Ensures that matrix has a default size of 9. Returns the ensured
    //array.
    private ItemStack[] ensureDefaultSize(ItemStack[] matrix){
        if(matrix.length == 9) return matrix;
        ItemStack[] defaultMatrix = new ItemStack[9];
        for(int i = 0; i < 9; i++){
            defaultMatrix[i] = null;
        }
        defaultMatrix[0] = matrix[0];
        defaultMatrix[1] = matrix[1];
        defaultMatrix[3] = matrix[2];
        defaultMatrix[4] = matrix[3];

        return defaultMatrix;
    }

    //Formats content so that it's shifted to the left top.
    private void format(ItemStack[] content){
        if(IsNullArray(content)) return;
        boolean nullRow = true, nullColumn = true;
        while(nullRow || nullColumn){
            for(int i = 0; i < 3; i++){
                if(!IsNullElement(content[i]))
                    nullRow = false;
                if(!IsNullElement(content[i*3]))
                    nullColumn = false;
            }
            if(nullRow) shiftUp(content);
            if(nullColumn) shiftLeft(content);
        }
    }

    //Shifts content to the left one position.
    private void shiftLeft(ItemStack[] content){
        for(int i = 1; i < content.length; i++){
            if(i % 3 != 0){
                content[i-1] = content[i];
                content[i] = null;
            }
        }
    }

    //Shifts content to the top one position.
    private void shiftUp(ItemStack[] content){
        for(int i = 3; i < content.length; i++){
            content[i-3] = content[i];
            content[i] = null;
        }
    }

    //Gets the shape of the recipe 'content'.
    private String[] getShape(ItemStack[] content){
        String recipeShape[] = {"","",""};
        for(int i = 0; i < 9; i++){
            if(content[i] != null)
                recipeShape[i/3] += (char)('A' + i);
            else
                recipeShape[i/3] += ' ';
        }
        return trimShape(recipeShape);
    }

    //Trims the shape so that there are no redundant spaces or elements in shape.
    private String[] trimShape(String[] shape){
        List<String> TrimmedShape = new ArrayList<>();
        int maxLength = 0;
        int temp;
        for(int i = 0; i < shape.length; i++){
            temp = StringUtils.stripEnd(shape[i], " ").length();
            if(temp > maxLength)
                maxLength = temp;
        }
        for(int i = 0; i < shape.length; i++){
            shape[i] = shape[i].substring(0, maxLength);
            if(shape[i].trim().length() > 0) TrimmedShape.add(shape[i]);
        }
        return TrimmedShape.toArray(new String[0]);
    }

    private void mapIngredients(ShapedRecipe shapedRecipe, ItemStack[] content){
        for(int i = 0; i < 9; i++){
            if(content[i] != null){
                //recipe.setIngredient((char) ('A' + i), content[i].getType());
                Adapter.SetIngredient(shapedRecipe, (char) ('A' + i), content[i]);
            }
        }
    }


}
