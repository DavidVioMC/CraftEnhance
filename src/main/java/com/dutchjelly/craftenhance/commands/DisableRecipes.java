package com.dutchjelly.craftenhance.commands;

import com.dutchjelly.craftenhance.commandhandling.CmdInterface;
import com.dutchjelly.craftenhance.commandhandling.CustomCmd;
import com.dutchjelly.craftenhance.commandhandling.CustomCmdHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CustomCmd(cmdPath="ceh.disablerecipes", perms="perms.recipe-editor")
public class DisableRecipes implements CmdInterface{
    private CustomCmdHandler handler;
    public DisableRecipes(CustomCmdHandler handler){
        this.handler = handler;
    }

    @Override
    public String getDescription() {
        return "Opens a GUI where existing default recipes can be disabled or enabled again.";
    }

    @Override
    public void handlePlayerCommand(Player p, String[] args) {
        handler.getMain().getGUIContainer().openRecipeDisabler(p);
    }

    @Override
    public void handleConsoleCommand(CommandSender sender, String[] args) {
        sender.sendMessage("This command is not supported for a console sender.");
    }
}
