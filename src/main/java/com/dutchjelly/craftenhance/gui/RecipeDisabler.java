package com.dutchjelly.craftenhance.gui;

import com.dutchjelly.craftenhance.util.GUIButtons;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class RecipeDisabler extends PageGUI implements GUIElement {

    private List<ItemStack> recipes;
    private GUIContainer container;

    public RecipeDisabler(GUIContainer container){
        super(54, "Recipe Disabler");
        this.container = container;
        recipes = container.getMain().getRecipeLoader().getServerRecipes();
        initGUI();
    }

    private void initGUI(){
        List<ItemStack> recipes = container.getMain().getRecipeLoader().getServerRecipes();
        List<ItemStack> disabled = container.getMain().getRecipeLoader().getDisabled();
        for(ItemStack recipe : recipes){
            if(disabled.contains(recipe))
                this.addElement(showDisable(recipe));
            else
                this.addElement(recipe);
        }
    }

    private ItemStack showDisable(ItemStack recipe){
        recipe = recipe.clone();
        ItemMeta meta = recipe.getItemMeta();
        meta.setDisplayName(meta.getDisplayName() + " [DISABLED]");
        recipe.setItemMeta(meta);
        return recipe;
    }


    @Override
    public Inventory getInventory() {
        return this.getCurrentPage();
    }

    @Override
    public boolean isEventTriggerer(Inventory inv) {
        return this.containsPage(inv);
    }

    @Override
    public void handleEvent(InventoryClickEvent e) {
        Player p = (Player)e.getWhoClicked();
        if(!e.isCancelled())
            e.setCancelled(true);
        if(e.getCurrentItem().equals(GUIButtons.previous)){
            this.scroll(-1);
        }
        else if(e.getCurrentItem().equals(GUIButtons.next)){
            this.scroll(1);
        }
        //Still have to handle clicking on recipes. It's very confusing because the super class
        //contains a list of it's content, which is quite redundant.
        container.openGUIElement(this, p);
    }

    @Override
    public Class<?> getInstance() {
        return this.getClass();
    }
}
