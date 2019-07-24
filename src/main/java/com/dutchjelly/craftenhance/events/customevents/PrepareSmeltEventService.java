package com.dutchjelly.craftenhance.events.customevents;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.FurnaceInventory;

public class PrepareSmeltEventService implements Listener{

    @EventHandler
    public void onClick(InventoryClickEvent e){
        if(e.getClickedInventory() instanceof FurnaceInventory){
            FurnaceInventory inv = (FurnaceInventory)e.getClickedInventory();
            if(e.getSlotType() == InventoryType.SlotType.CRAFTING){
                //handle code for calling preparesmeltevent
                //Set inventoryinteractevent as parameter
                //maybe set a destination slot
            }
            if(e.getSlotType() == InventoryType.SlotType.FUEL){
                //maybe implement this, it's not needed currenlty
                //but always good to maintain modulairity
            }
        }
        if(e.getView().getTopInventory() instanceof FurnaceInventory){
            FurnaceInventory inv = (FurnaceInventory)e.getView().getTopInventory();
            if(e.getClickedInventory().equals(e.getView().getBottomInventory()) && e.isShiftClick()){
                //trying to shiftclick items into furnace
            }

        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e){
        if(e.getInventory() instanceof FurnaceInventory){
            if(e.getInventorySlots().contains(0)){
                //handle dragging items onto the furnace one slot (spreading items out
                //by holding rightclick)
            }

        }
    }

    @EventHandler
    public void onMove(InventoryMoveItemEvent e){
        if(e.getDestination() instanceof FurnaceInventory){

        }
    }

}
