package com.dutchjelly.craftenhance.Util;

import com.dutchjelly.bukkitadapter.Adapter;
import com.dutchjelly.craftenhance.CraftEnhance;
import com.dutchjelly.craftenhance.messaging.Debug;
import com.dutchjelly.craftenhance.model.CraftRecipe;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.material.MaterialData;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RecipeUtil {

    //This class is only used for static utilities. Creating an instance
    //is illegal.
    public RecipeUtil() {
        throw new NotImplementedException();
    }

    private static List<String> UsedKeys = new ArrayList<>();

    public static String MakeNameSpaced(String key){
        //A key and namespace of the recipe on the server.
        String recipeKey = "recipe" + key.toLowerCase().replaceAll("[^a-z0-9 ]", "");
        String uniqueKey = recipeKey;
        int counter = 2; //Start with 2 because first unique key isn't listed as {recipeKey}1.
        while(UsedKeys.contains(uniqueKey)){
            uniqueKey = recipeKey + counter;
            counter++;
        }

        UsedKeys.add(uniqueKey);
        return uniqueKey;
    }


    //Prints content. Used for local debugging on Windows only!
    public static void PrintContent(ItemStack[] content){
        for(int i = 0; i < content.length; i++){
            if(content[i] == null) System.out.println(i + ": null");
            else System.out.println(i + ": " + content[i].getType());
        }
    }

    //Checks if content is an empty recipe.
    public static boolean IsNullArray(ItemStack[] content){
        for(int i = 0; i < content.length; i++){
            if(!IsNullElement(content[i])) return false;
        } return true;
    }

    //Checks if element is not a valid crafting material.
    public static boolean IsNullElement(ItemStack element){
        return element == null || element.getType().equals(Material.AIR);
    }



    //Looks for every index if the item in recipe has an equal type of the item in content.
    public static boolean AreEqualTypes(ItemStack[] recipe, ItemStack[] content){
        if(recipe == null || content == null || recipe.length != content.length)
            return false;
        for(int i = 0; i < recipe.length; i++){
            if(!RecipeUtil.AreEqualTypes(content[i], recipe[i])) return false;
        }
        return true;
    }

    //Looks if the types of content and recipe match.
    public static boolean AreEqualTypes(ItemStack content, ItemStack recipe){
        content = EnsureNullAir(content);
        recipe = EnsureNullAir(recipe);
        if(content == null){
            return recipe == null;
        }
        return recipe != null && recipe.getType().equals(content.getType());
        //return recipe != null && Adapter.AreEqualTypes(recipe, content);
    }

    //Looks for every index if the item in recipe is equal to the item in content.
    public static boolean AreEqualItems(ItemStack[] recipe, ItemStack[] content){
        if(recipe == null || content == null || recipe.length != content.length)
            return false;
        for(int i = 0; i < recipe.length; i++){
            if(!RecipeUtil.AreEqualItems(content[i], recipe[i])){
//                Debug.Send("-------------------------------");
//                Debug.Send("Found two no equal items on index " + i + "...");
//                Debug.Send(content[i]);
//                Debug.Send(recipe[i]);
//                Debug.Send("-------------------------------");
                return false;
            }
        }
        return true;
    }

    //Looks if content and recipe are equal items.
    public static boolean AreEqualItems(ItemStack content, ItemStack recipe){
        content = EnsureNullAir(content);
        recipe = EnsureNullAir(recipe);
        return content == recipe || (content != null && recipe != null &&
                recipe.isSimilar(content));
    }

    //Used to counter changes in spigot versions: Inventories contain air instead of
    //null items in versions below 1.14.
    private static ItemStack EnsureNullAir(ItemStack item){
        if(item == null) return item;
        if(item.getType().equals(Material.AIR))
            return null;
        return item;
    }

    //Mirrors the content vertically. Works with any size matrix.
    public static ItemStack[] MirrorVerticle(ItemStack[] content){
        //012  ->  210
        //345  ->  543
        //678  ->  876
        if(content == null) return null;
        int root = (int)Math.sqrt(content.length);
        ItemStack[] mirrored = new ItemStack[9];
        for(int i = 0; i < content.length; i+=root){
            for(int j = 0; j < root; j++){
                //Reverses the row for every loop of i.
                mirrored[i+2 - j] = content[i+j];
            }
        }
        //To verify that the mirroring is working. I should write a unit test for this.
        //Debug.Send("Mirrored \n" + String.join(", ", Arrays.asList(content).stream().map(x -> x == null ? "null" : x.toString()).collect(Collectors.toList())) + " to \n" + String.join(",", Arrays.asList(mirrored).stream().map(x -> x == null ? "null" : x.toString()).collect(Collectors.toList())));
        return mirrored;
    }

}
