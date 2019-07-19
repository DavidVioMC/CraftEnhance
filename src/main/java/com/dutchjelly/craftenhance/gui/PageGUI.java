package com.dutchjelly.craftenhance.gui;

import com.dutchjelly.craftenhance.messaging.Debug;
import com.dutchjelly.craftenhance.util.GUIButtons;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PageGUI {

    private int GUIsize;
    private int index = 0;
    private String name;
    private List<Inventory> pages = new ArrayList<>();
    private List<ItemStack> contents = new ArrayList<>();

    public PageGUI(int GUIsize, String name){
        this.GUIsize = GUIsize;
        this.name = name;¼
        addPage();
    }

    private void addPage(){
        Inventory inv = Bukkit.createInventory(null, GUIsize,
                ChatColor.translateAlternateColorCodes('&', name));
        for(int i = GUIsize-9; i < GUIsize; i++)
            inv.setItem(i, GUIButtons.filling);
        inv.setItem(GUIsize-2, GUIButtons.next);
        inv.setItem(GUIsize-8, GUIButtons.previous);
        pages.add(inv);
    }

    public void addElements(List<ItemStack> elements){
        elements.forEach(x -> addElement(x));
    }

    public void addElement(ItemStack element){
        int addIndex = 0;
        while(addIndex < pages.size() && pages.get(addIndex).firstEmpty() == -1)
            addIndex++;
        if(addIndex == pages.size()){
            addPage();
        }
        pages.get(addIndex).addItem(element);
        contents.add(element);
    }

    public void scroll(int direction){
        if(index+direction < 0 || index+direction >= pages.size()) return;
        index += direction;
    }

    public Inventory getCurrentPage(){
        return pages.get(index);
    }

    public List<ItemStack> getContents(){
        return contents;
    }

    public boolean containsPage(Inventory inv){
        for (Inventory page : pages) {
            if(page.equals(inv))
                return true;
        }
        return false;
    }
}
